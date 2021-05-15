package com.makeitez.acsalesapp.screens.salesorder.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_saved_order.view.*

class SavedOrdersViewHolder(listItemView: View, listener: Listener) : RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onSalesOrderPressed(savedOrder: SalesOrder)
        fun onDeleteSalesOrder(savedOrder: SalesOrder)
    }

    private var savedOrder: SalesOrder? = null

    private val dateHeader = itemView.savedOrderDateHeader
    private val savedOrderCard = itemView.savedOrderCard
    private val savedOrderDeleteButton = itemView.savedOrderDeleteButton
    private val savedOrderCompanyName = itemView.savedOrderCompanyName
    private val savedOrderCreatedTime = itemView.savedOrderCreatedTime

    init {
        savedOrderCard.setOnClickListener {
            savedOrder?.let(listener::onSalesOrderPressed)
        }
        savedOrderDeleteButton.setOnClickListener {
            savedOrder?.let(listener::onDeleteSalesOrder)
        }
    }

    fun bind(savedOrder: SalesOrder, showDateHeader: Boolean) {
        this.savedOrder = savedOrder
        dateHeader.isVisible = showDateHeader
        dateHeader.text = if (showDateHeader) FormattingHelper.formatDate(savedOrder.createdDatetime, FormattingHelper.dateFormatLong) else ""
        savedOrderCompanyName.text = savedOrder.customer?.companyName
        savedOrderCreatedTime.text = FormattingHelper.formatDate(savedOrder.createdDatetime, FormattingHelper.timeDateFormat)
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener) = SavedOrdersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_saved_order, parent, false),
            listener
        )
    }

}