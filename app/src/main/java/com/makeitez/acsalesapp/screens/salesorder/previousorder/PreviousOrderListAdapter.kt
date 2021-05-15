package com.makeitez.acsalesapp.screens.salesorder.previousorder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.ACUser
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll
import org.apache.commons.lang3.time.DateUtils

class PreviousOrderListAdapter(
    private val previousOrderListListener: PreviousOrderViewHolder.Listener
) : RecyclerView.Adapter<PreviousOrderViewHolder>() {
    private val isUserAdmin: Boolean = ACUser.isAdmin()
    private val previousOrderList: MutableList<SalesOrder> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousOrderViewHolder =
        PreviousOrderViewHolder.newInstance(parent, previousOrderListListener)

    override fun onBindViewHolder(holder: PreviousOrderViewHolder, position: Int) {
        val previousPreviousOrder = previousOrderList.getOrNull(position - 1)
        val currentPreviousOrder = previousOrderList[position]
        val showDateHeader = if (previousPreviousOrder == null) {
            true
        } else {
            !DateUtils.isSameDay(currentPreviousOrder.createdDatetime, previousPreviousOrder.createdDatetime)
        }
        holder.bind(currentPreviousOrder, isUserAdmin, showDateHeader)
    }

    override fun getItemCount(): Int = previousOrderList.size

    override fun getItemId(position: Int): Long = previousOrderList[position].docNo.hashCode().toLong()

    fun setList(newList: List<SalesOrder>) {
        previousOrderList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }
}