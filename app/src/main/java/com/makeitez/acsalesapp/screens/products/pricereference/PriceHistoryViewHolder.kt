package com.makeitez.acsalesapp.screens.products.pricereference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.PriceHistory
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_price_history_list.view.*


class PriceHistoryViewHolder(listItemView: View) :
    RecyclerView.ViewHolder(listItemView) {

    private val priceHistoryDateText = itemView.productPriceHistoryDateText
    private val priceHistoryTypeText = itemView.productPriceHistoryTypeText
    private val priceHistoryQtyText = itemView.productPriceHistoryQtyText
    private val priceHistoryUnitPriceText = itemView.productPriceHistoryUnitPriceText

    fun bind(priceHistory: PriceHistory) {
        priceHistoryDateText.text = FormattingHelper.formatDate(priceHistory.docDate)
        priceHistoryTypeText.text = priceHistory.docType
        priceHistoryQtyText.text = "${priceHistory.quantity} ${priceHistory.uom}"
        priceHistoryUnitPriceText.text =
            FormattingHelper.formatUnitPrice(priceHistory.unitPrice, priceHistory.currencyCode)
    }

    companion object {
        fun newInstance(parent: ViewGroup) =
            PriceHistoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_price_history_list, parent, false)
            )
    }
}