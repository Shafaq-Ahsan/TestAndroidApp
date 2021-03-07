package com.app.coderByte.interfaces

import android.content.Context
import com.app.network_module.models.response.DataResponse

internal interface BindHolder {
    fun bindHolder(
        position: Int,
        mListClickLister: ListClickListener,
        mContext: Context,
        list: ArrayList<DataResponse>
    )

}