package com.app.coderByte.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.coderByte.adapter.DataAdapter
import com.app.network_module.models.response.DataResponse
//binding adapter util class
class Utils {
    companion object {
        @BindingAdapter("data")
        @JvmStatic
        fun dataList(
            recyclerView: RecyclerView,
            dataList: List<DataResponse>?
        ) {
            dataList?.let {
                recyclerView.adapter?.let { (recyclerView.adapter as DataAdapter).setData(dataList as ArrayList<DataResponse>) }
            }
        }
    }
}