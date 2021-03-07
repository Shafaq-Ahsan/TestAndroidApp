package com.app.coderByte.imageCaching;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import com.app.coderByte.R;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.app.coderByte.utils.ExtensionFunctionsKt.decodeSampledBitmapFromFileDescriptor;

public class ImageLoader {

    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50; //50MB

    private static final int DISK_CACHE_INDEX = 0;

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static final int TAG_KEY_URI =R.id.TAG_URI; // Tags to prevent misplacement of pictures

    private final LruCache<String, Bitmap> mMemoryCache;

    private DiskLruCache mDiskLruCache;

    private boolean mIsDiskLruCacheCreated = false; // Are there any disks used

    private static ImageLoader mImageLoader;

    private final Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            ImageView imageView = result.imageView;
            String uri = (String) imageView.getTag(TAG_KEY_URI);
            if (uri.equals(result.uri)) {
                imageView.setImageBitmap(result.bitmap);
            }
        }
    };

    public static ImageLoader getInstance(Context ctx) {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(ctx);
        }
        return mImageLoader;
    }

    private ImageLoader(Context context) {
        Context mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };

        // Get image cache path
        File diskCacheDir = getDiskCacheDir(mContext, "dubizzle");
        if (!diskCacheDir.exists())
            diskCacheDir.mkdirs();

        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
//                mDiskLruCache = DiskLruCache.open(diskCacheDir, getAppVersion(mContext), 1, DISK_CACHE_SIZE);
                // Create a DiskLruCache instance and initialize the cache data
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                // The second parameter is the application version number, generally set to 1. When the version number changes, all previous cache files will be cleared.
                // This feature has little effect in actual development. In actual development, the general version of the updated data cache still needs to be valid
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadBitmap(String url, ImageView imgView) {
        loadBitmap(url, imgView, 0, 0);
    }

    public void loadBitmap(final String url, final ImageView imgView, final int reqWidth, final int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemCache(url);
        imgView.setTag(TAG_KEY_URI, url);// Prevent misalignment
        if (bitmap != null) {
            imgView.setImageBitmap(bitmap);
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap runBitmap = loadBitmap(url, reqWidth, reqHeight);
                if (runBitmap != null) {
                    LoaderResult result = new LoaderResult(imgView, url, runBitmap);
                    mMainHandler.obtainMessage(1, result).sendToTarget();
                }
            }
        };

        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    /**
     * Load bitmap
     * 1. First check whether there is the bitmap from the memory
     * 2. The memory does not search for the bitmap from the disk
     * 3. If neither of the above is downloaded from the Internet, it is cached in disk and memory
     * 4. If there is no memory, the disk does not exist, or the cache memory is insufficient, it is directly pulled from the network
     *
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemCache(url);// Memory read
        if (bitmap != null) {
            return bitmap;
        }

        try {
            bitmap = loadBitmapFromDiskCache(url, reqWidth, reqHeight);// Disk read
            if (bitmap != null) {
                return bitmap;
            }

            bitmap = loadBitmapFromHttp(url, reqWidth, reqHeight);// Network download cache
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadBitmapFromUrl(url);// Pull pictures directly from the web
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromMemCache(String url) {
        String key = hashKeyFormUrl(url);
        return getBitmapFromMemoryCache(key);
    }

    private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException {
        if (mDiskLruCache == null) {
            return null;
        }

        String key = hashKeyFormUrl(url);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null) {
            // Download the file and write to the cache after getting the file output stream
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToSteam(url, outputStream)) {
                editor.commit();
            } else {
                editor.abort();// If the bitmap is not downloaded and written to outputsteam, the entire operation is rolled back
            }

            mDiskLruCache.flush();
        }

        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }

    /**
     * Get cache from disk and store in memory
     *
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws IOException
     */
    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) throws IOException {
        if (mDiskLruCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }

        /**
         * The cache lookup of DiskLruCache is to obtain a Snapshot object through the Get () method of DiskLruCache,
         * Then get the cached file input stream through the Snapshot object, so that you can get the Bitmap
         */

        return bitmap;
    }

    /**
     * The MD5 of the URL is generally used as the key to avoid illegal characters
     *
     * @param url
     */
    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Add to memory
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Get bitmap from memory
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * Get version number
     *
     * @param ctx
     * @return
     */
    private int getAppVersion(Context ctx) {
        PackageInfo info = null;
        try {
            info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Get the size of free space
     *
     * @param path storage path
     * @return
     */
    private long getUsableSpace(File path) {
        return path.getUsableSpace();
    }

    /**
     * Get disk cache address
     *
     * @param ctx
     * @param uniqueName unique name
     * @return
     */
    private File getDiskCacheDir(Context ctx, String uniqueName) {
        boolean externalAvailable = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable());

        String cachePath;

        if (externalAvailable) {
            cachePath = ctx.getExternalCacheDir().getPath();/// sdcard / Android / data / package_name / cache this path
        } else {
            cachePath = ctx.getCacheDir().getPath(); /// data / data / package_name / cache this path.
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Write cache
     *
     * @param urlPath
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToSteam(String urlPath, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            URL url = new URL(urlPath);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            try {
                if (out != null)
                    out.close();

                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Direct web download via url address
     *
     * @param urlPath
     * @return
     */
    private Bitmap downloadBitmapFromUrl(String urlPath) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlPath);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    // --------------------- Configure thread pool ----------------------
    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1; // Number of cores
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1; // Maximum number of cores
    private static final long KEEP_ALIVE = 10L; // Thread idle timeout

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private static class LoaderResult {
        public ImageView imageView;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView imageView, String uri, Bitmap bitmap) {
            this.imageView = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }

}
