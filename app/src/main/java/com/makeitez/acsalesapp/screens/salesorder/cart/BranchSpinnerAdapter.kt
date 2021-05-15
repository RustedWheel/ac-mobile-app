package com.makeitez.acsalesapp.screens.salesorder.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.CustomerBranch
import kotlinx.android.synthetic.main.item_customer_branch_spinner.view.*
import kotlinx.android.synthetic.main.item_general_dropdown_spinner.view.*

class BranchSpinnerAdapter(context: Context, branchList: List<CustomerBranch>) :
    ArrayAdapter<CustomerBranch>(context, 0, branchList) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_general_dropdown_spinner, parent, false)

        if (convertView == null) {
            spinnerView.tag = BranchViewHolder(spinnerView.generalDropDownText)
        }

        (spinnerView.tag as? BranchViewHolder)?.bind(getItem(position))

        return spinnerView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_customer_branch_spinner, parent, false)

        if (convertView == null) {
            spinnerView.tag = BranchViewHolder(spinnerView.customerBranchText)
        }

        (spinnerView.tag as? BranchViewHolder)?.bind(getItem(position))

        return spinnerView
    }
}