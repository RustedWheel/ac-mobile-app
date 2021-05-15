package com.makeitez.acsalesapp.screens.products.productorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.makeitez.acsalesapp.R
import kotlinx.android.synthetic.main.item_general_dropdown_spinner.view.*
import kotlinx.android.synthetic.main.item_label_spinner.view.*

class LabelSpinnerAdapter(context: Context, labelList: List<String>) :
    ArrayAdapter<String>(context, 0, labelList) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_general_dropdown_spinner, parent, false)

        if (convertView == null) {
            spinnerView.tag = LabelViewHolder(spinnerView.generalDropDownText)
        }

        (spinnerView.tag as? LabelViewHolder)?.bind(getItem(position))

        return spinnerView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_label_spinner, parent, false)

        if (convertView == null) {
            spinnerView.tag = LabelViewHolder(spinnerView.labelText)
        }

        (spinnerView.tag as? LabelViewHolder)?.bind(getItem(position))

        return spinnerView
    }
}