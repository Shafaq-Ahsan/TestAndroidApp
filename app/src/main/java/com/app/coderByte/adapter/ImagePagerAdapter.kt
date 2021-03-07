package com.app.coderByte.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.coderByte.databinding.ItemBannerImagesBinding
import com.app.coderByte.utils.loadImage
import com.app.network_module.models.response.DataResponse


internal class ImagePagerAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var mPagerList: DataResponse? = null
    fun setData(data: DataResponse) {
        mPagerList = data //= data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(mContext, mPagerList, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mPagerList?.imageUrls?.size ?: 0
    }

}

class ViewHolder(private val binding: ItemBannerImagesBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemBannerImagesBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }

    fun bind(context: Context, data: DataResponse?, position: Int) {
        binding.apply {
            val imageView = binding.image as ImageView
            if (data?.imageUrlsThumbnails?.size ?: 0 > 0)
                context.loadImage(
                    data?.imageUrls?.get(position)?: "",
                    imageView,
                    data?.imageUrlsThumbnails?.get(0)?: ""
                )
            else
                context.loadImage(data?.imageUrls?.get(position)?: "", imageView, "")


        }

    }
}
