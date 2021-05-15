package com.makeitez.acsalesapp.screens.salesorder.previousorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_previous_order_list.view.*

class PreviousOrderViewHolder(listItemView: View, listener: Listener) : RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onPreviousOrderClick(previousOrder: SalesOrder)
    }

    private var previousOrder: SalesOrder? = null

    private val context = itemView.context
    private val dateHeader = itemView.previousOrderDateText
    private val previousOrderCard = itemView.previousOrderCard
    private val previousOrderCompanyText = itemView.previousOrderCompanyText
    private val previousOrderTimeText = itemView.previousOrderTimeText

    private val previousOrderStatusLayout = itemView.previousOrderStatusLayout
    private val previousOrderStatusImage = itemView.previousOrderStatusImage
    private val previousOrderTotalText = itemView.previousOrderTotalText

    private val previousOrderSalesAgentLayout = itemView.previousOrderSalesAgentLayout
    private val previousOrderSalesAgentText = itemView.previousOrderSalesAgentText

    init {
        previousOrderCard.setOnClickListener {
            previousOrder?.let(listener::onPreviousOrderClick)
        }
    }

    fun bind(previousOrder: SalesOrder, isAdmin: Boolean, showDateHeader: Boolean) {
        this.previousOrder = previousOrder

        dateHeader.isVisible = showDateHeader
        dateHeader.text = if (showDateHeader) FormattingHelper.formatDate(previousOrder.createdDatetime, FormattingHelper.dateFormatLong) else ""

        previousOrderCompanyText.text = previousOrder.customer?.companyName ?: ""
        previousOrderTimeText.text = FormattingHelper.formatDate(previousOrder.createdDatetime, FormattingHelper.timeDateFormat)

        previousOrder.orderStatus?.let {
            styleOrderStatusLayout(it)
            previousOrderTotalText.text = FormattingHelper.formatPrice(previousOrder.confirmedTotalAmount, previousOrder.customer?.currencyCode ?: "")
        }

        previousOrderSalesAgentLayout.isVisible = isAdmin
        if(isAdmin) {
            previousOrderSalesAgentText.text = previousOrder.salesAgent
        }
    }

    private fun styleOrderStatusLayout(orderStatus: SalesOrderStatus) {
        when(orderStatus) {
            SalesOrderStatus.Success -> {
                previousOrderStatusLayout.setBackgroundResource(R.color.ac_green_50)
                previousOrderStatusImage.setImageResource(R.drawable.ic_successful_order)
                previousOrderTotalText.setTextColor(ContextCompat.getColor(context, R.color.ac_green))
            }
            SalesOrderStatus.Pending -> {
                previousOrderStatusLayout.setBackgroundResource(R.color.ac_orange_50)
                previousOrderStatusImage.setImageResource(R.drawable.ic_pending_order)
                previousOrderTotalText.setTextColor(ContextCompat.getColor(context, R.color.ac_orange_900))
            }
            SalesOrderStatus.Rejected -> {
                previousOrderStatusLayout.setBackgroundResource(R.color.ac_red_50)
                previousOrderStatusImage.setImageResource(R.drawable.ic_rejected_order)
                previousOrderTotalText.setTextColor(ContextCompat.getColor(context, R.color.ac_red_900))
            }
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener) =
            PreviousOrderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_previous_order_list, parent, false), listener
            )
    }

}