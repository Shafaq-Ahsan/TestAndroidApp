package com.app.coderByte.fragments


import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.app.coderByte.R
import com.app.coderByte.viewmodels.MainActivityViewModel
import androidx.navigation.fragment.findNavController
import com.app.coderByte.ApplicationClass
import com.app.coderByte.adapter.ImagePagerAdapter
import com.app.coderByte.databinding.FragmentDetailBinding
import com.app.coderByte.utils.*
import com.app.network_module.models.response.DataResponse
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.ArrayList

internal class DetailFragment : BaseFragment() {

    private var position: Int? = 0
    private lateinit var mBinding: FragmentDetailBinding
    private lateinit var mViewModel: MainActivityViewModel
    private var dataList = ArrayList<DataResponse>()
    private lateinit var imagesAdapter: ImagePagerAdapter


    override fun getFragmentLayout() = R.layout.fragment_detail

    override fun getViewBinding() {
        mBinding = binding as FragmentDetailBinding
    }


    override fun getViewModel() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
    }

    override fun observe() {
    }


    override fun setLiveDataValues() {
        mViewModel.responseData.value?.let {
            setData(it)
        }
    }

    private fun setData(it: ArrayList<DataResponse>) {
        dataList = it
        val dataItem = position?.let { it1 -> dataList[it1] }
        mBinding.dataResponse = dataItem
        mBinding.priceTitle = ApplicationClass.languageJson?.listingScreen?.priceTitle ?: ""
        mBinding.dateTitle = ApplicationClass.languageJson?.listingScreen?.dateTitle ?: ""
        dataItem?.let { it1 -> imagesAdapter.setData(it1) }

    }

    override fun init() {
        position = arguments?.getInt(Enums.Ids.POSITION.key) ?: 0
        setImagesViewPager()
    }

    override fun setListeners() {
        mBinding.navIcon.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setImagesViewPager() {
        imagesAdapter = ImagePagerAdapter(requireContext())
        mBinding.carDetailsTop.viewpagerImages.adapter = imagesAdapter
        TabLayoutMediator(
            mBinding.carDetailsTop.tabDots,
            mBinding.carDetailsTop.viewpagerImages
        ) { tab, _ ->
            mBinding.carDetailsTop.viewpagerImages.setCurrentItem(tab.position, true)
        }.attach()
    }

    override fun setLanguageData() {

    }

    override fun onClick(v: View?) {

    }


}


