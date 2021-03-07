package com.app.coderByte.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.coderByte.ApplicationClass
import com.app.coderByte.databinding.ItemListCardBinding
import com.app.coderByte.databinding.ShimmerItemListBinding
import com.app.coderByte.interfaces.BindHolder
import com.app.network_module.models.response.DataResponse
import com.app.coderByte.interfaces.ListClickListener
import com.app.coderByte.utils.capitalizeWords
import com.app.coderByte.utils.loadImage
import com.app.coderByte.utils.parseDate


internal class DataAdapter(
    private val mContext: Context,
    private var list: ArrayList<DataResponse>,
    private val mListClickLister: ListClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val LIST = 1
        const val SHIMMER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SHIMMER -> ShimmerListHolder.from(parent)
            else -> ListViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BindHolder -> holder.bind(position, mListClickLister, mContext, list)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list.size <= 0) {
            true -> SHIMMER
            else -> LIST
        }
    }

    fun setData(data: ArrayList<DataResponse>) {
        this.list = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (list.size <= 0) 4 else list.size
    }

}

private class ListViewHolder(
    private val binding: ItemListCardBinding

) : RecyclerView.ViewHolder(binding.root), BindHolder {
    companion object {
        fun from(
            parent: ViewGroup
        ): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemListCardBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return ListViewHolder(binding)
        }
    }

    override fun bind(
        position: Int,
        mListClickLister: ListClickListener,
        mContext: Context,
        list: ArrayList<DataResponse>
    ) {
        val dataItem = list[position]
        dataItem.imageUrlsThumbnails?.get(0)?.let { mContext.loadImage(it, binding.ivBanner) }
        binding.tvPrice.text = dataItem.price ?: ""
        binding.tvTitle.text = dataItem.name?.capitalizeWords() ?: ""
        binding.tvPriceTitle.text =
            ApplicationClass.languageJson?.listingScreen?.priceTitle ?: ""
        binding.tvDataTitle.text = ApplicationClass.languageJson?.listingScreen?.dateTitle ?: ""
        dataItem.createdAt?.let {
            binding.tvDate.text = parseDate(it)
        }

        itemView.setOnClickListener {
            it?.let {
                mListClickLister.onItemClick(position)
            }
        }
    }


}

private class ShimmerListHolder(binding: ShimmerItemListBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ShimmerItemListBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return ShimmerListHolder(binding)
        }
    }
}
