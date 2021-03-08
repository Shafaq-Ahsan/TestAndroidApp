package com.app.coderByte.fragments


import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.coderByte.R
import com.app.coderByte.databinding.FragmentHomeBinding
import com.app.coderByte.viewmodels.MainActivityViewModel
import java.util.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.coderByte.adapter.DataAdapter
import com.app.coderByte.interfaces.ListClickListener
import com.app.coderByte.utils.Enums
import com.app.coderByte.utils.Toast
import com.app.network_module.models.response.DataResponse

internal class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
    ListClickListener {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var dataAdapter: DataAdapter
    private var dataList = ArrayList<DataResponse>()
    private lateinit var recyclerView: RecyclerView


    override fun getFragmentLayout() = R.layout.fragment_home

    override fun getViewBinding() {
        mBinding = binding as FragmentHomeBinding
    }


    override fun getViewModel() {
        mViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel::class.java)
    }

    override fun observe() {
        mViewModel.responseData.observe(this, Observer {
            it ?: return@Observer
            setData(it)
        })
    }


    override fun setLiveDataValues() {
        mViewModel.responseData.value?.let {
            setData(it)
        }
    }

    private fun setData(it: ArrayList<DataResponse>) {
        mBinding.refresh.isRefreshing = false
        dataList = it

    }

    override fun init() {
        mBinding.lifecycleOwner = this
        mBinding.viewModel= mViewModel
        setupRecyclerView()
        mViewModel.responseData.value ?: mViewModel.getData()
    }

    override fun setListeners() {
        mBinding.refresh.setOnRefreshListener(this)
    }

    private fun setupRecyclerView() {
        dataAdapter = DataAdapter(requireContext(), dataList,this)
        val manager = GridLayoutManager(context,2)
        recyclerView = mBinding.recyclerDataView.apply {
            layoutManager = manager
            adapter = dataAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun setLanguageData() {

    }

    override fun onClick(v: View?) {

    }

    override fun onRefresh() {
        mViewModel.getData()
    }

    override fun onItemClick(position: Int) {
        dataList[position].name?.let { requireContext().Toast(it) }
        val bundle = bundleOf(
            Enums.Ids.POSITION.key to position
        )
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }

}


