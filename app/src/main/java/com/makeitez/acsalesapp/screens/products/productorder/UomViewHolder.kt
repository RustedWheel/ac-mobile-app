package com.makeitez.acsalesapp.screens.products.productorder

import android.widget.TextView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.Uom
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.extensions.toStringRemoveTrailingZeroes

class UomViewHolder(private val uomText: TextView) {
    fun bind(uom: Uom?) {
        uom?.let {
            uomText.text = uomText.context
                .getString(R.string.product_order_uom_desc, it.description, it.rate.toStringRemoveTrailingZeroes(Config.UNIT_RATE_DPS))
        }
    }
}