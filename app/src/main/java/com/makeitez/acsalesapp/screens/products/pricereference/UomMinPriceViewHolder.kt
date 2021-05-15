package com.makeitez.acsalesapp.screens.products.pricereference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.Uom
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_uom_min_price_list.view.*


class UomMinPriceViewHolder(listItemView: View) :
    RecyclerView.ViewHolder(listItemView) {

    private val context = itemView.context
    private val uomMinPriceLabelText = itemView.productUomMinPriceLabelText
    private val uomMinPriceText = itemView.productUomMinPriceText

    fun bind(uom: Uom, currency: String) {
        val context = context ?: return
        uomMinPriceLabelText.text = context.getString(R.string.price_reference_uom_min_price_label, uom.description)
        uomMinPriceText.text = uom.minSalePrice?.let {
            context.getString(
                R.string.price_reference_uom_min_price,
                FormattingHelper.formatUnitPrice(it, currency)
            )
        } ?: {
            context.getString(
                R.string.cart_unknown_price,
                currency
            )
        }()
    }

    companion object {
        fun newInstance(parent: ViewGroup) =
            UomMinPriceViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_uom_min_price_list, parent, false)
            )
    }
}