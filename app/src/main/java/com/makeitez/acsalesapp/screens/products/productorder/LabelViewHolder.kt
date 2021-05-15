package com.makeitez.acsalesapp.screens.products.productorder

import android.widget.TextView

class LabelViewHolder(private val labelText: TextView) {

    fun bind(label: String?) {
        label?.let {
            labelText.text = label
        }
    }
}