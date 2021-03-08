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

//home fragment to show list of items
internal class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
    ListClickListener { // list click interface
    private lateinit var mBinding: FragmentHomeBinding // binding variable
    private lateinit var mViewModel: MainActivityViewModel //viewmodel variabole
    private lateinit var dataAdapter: DataAdapter //adapter variable
    private var dataList = ArrayList<DataResponse>() //data
    private lateinit var recyclerView: RecyclerView // recyclerview variable


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

    // set data when call is updated
    private fun setData(it: ArrayList<DataResponse>) {
        mBinding.refresh.isRefreshing = false // stop pull to refresh
        dataList = it //set class data

    }

    override fun init() {
        mBinding.lifecycleOwner = this // for binding
        mBinding.viewModel = mViewModel // set binding variable
        setupRecyclerView() // recycler view setup
        mViewModel.responseData.value
            ?: mViewModel.getData() // call api when there is no value in live data otherwise used that value
    }

    override fun setListeners() {
        mBinding.refresh.setOnRefreshListener(this) //pull to refresh listner
    }

    // recycler view setup
    private fun setupRecyclerView() {
        dataAdapter = DataAdapter(requireContext(), dataList, this)
        val manager = GridLayoutManager(context, 2)
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

    // SwipeRefreshLayout.OnRefreshListener overided method
    override fun onRefresh() {
        mViewModel.getData()
    }

    // on list click method
    override fun onItemClick(position: Int) {
        dataList[position].name?.let { requireContext().Toast(it) }
        val bundle = bundleOf(
            Enums.Ids.POSITION.key to position
        )
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
    }

}


