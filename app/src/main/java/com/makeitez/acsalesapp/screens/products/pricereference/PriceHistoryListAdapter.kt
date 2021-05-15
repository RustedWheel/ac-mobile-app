package com.makeitez.acsalesapp.screens.products.pricereference

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.PriceHistory
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll


class PriceHistoryListAdapter : RecyclerView.Adapter<PriceHistoryViewHolder>() {

    private val priceHistoryList: MutableList<PriceHistory> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceHistoryViewHolder =
        PriceHistoryViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: PriceHistoryViewHolder, position: Int) = holder.bind(priceHistoryList[position])

    override fun getItemCount() = priceHistoryList.size

    override fun getItemId(position: Int): Long = priceHistoryList[position].docNo.hashCode().toLong()

    fun setList(newList: List<PriceHistory>) {
        priceHistoryList.clearAndAddAll(newList)

        notifyDataSetChanged()
    }
}