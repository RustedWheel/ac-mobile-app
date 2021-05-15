package com.makeitez.acsalesapp.screens.salesorder.saved

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll
import org.apache.commons.lang3.time.DateUtils

class SavedOrdersListAdapter(private val listener: SavedOrdersViewHolder.Listener) : RecyclerView.Adapter<SavedOrdersViewHolder>() {

    private val savedOrderList: MutableList<SalesOrder> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedOrdersViewHolder =
        SavedOrdersViewHolder.newInstance(parent, listener)

    override fun onBindViewHolder(holder: SavedOrdersViewHolder, position: Int) {
        val previousSavedOrder = savedOrderList.getOrNull(position - 1)
        val currentSavedOrder = savedOrderList[position]
        val showDateHeader = if (previousSavedOrder == null) {
            true
        } else {
            !DateUtils.isSameDay(currentSavedOrder.createdDatetime, previousSavedOrder.createdDatetime)
        }
        holder.bind(currentSavedOrder, showDateHeader)
    }

    override fun getItemCount() = savedOrderList.size

    override fun getItemId(position: Int): Long = savedOrderList[position].docNo.hashCode().toLong()

    fun setList(newList: List<SalesOrder>) {
        savedOrderList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }
}