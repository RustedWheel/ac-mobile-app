package com.makeitez.acsalesapp.screens.products.productorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.Uom
import kotlinx.android.synthetic.main.item_uom_spinner.view.*

class UomSpinnerAdapter(context: Context, uomList: List<Uom>) :
    ArrayAdapter<Uom>(context, 0, uomList) {

    private val baseUomDesc: String = uomList.find { u -> u.isBaseUom }?.description ?: ""

    private fun getSpinnerView(position: Int, convertView: View?, parent: ViewGroup, fromDropDown: Boolean): View {
        val spinnerView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_uom_spinner, parent, false)

        if (convertView == null) {
            spinnerView.tag = UomViewHolder(spinnerView.productUomText)
        }

        (spinnerView.tag as? UomViewHolder)?.bind(getItem(position))

        return spinnerView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getSpinnerView(position, convertView, parent, true)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getSpinnerView(position, convertView, parent, false)
    }
}