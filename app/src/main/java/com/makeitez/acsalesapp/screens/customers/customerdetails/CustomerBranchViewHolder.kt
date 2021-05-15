package com.makeitez.acsalesapp.screens.customers.customerdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.CustomerBranch
import kotlinx.android.synthetic.main.item_customer_details_branch_list.view.*

class CustomerBranchViewHolder(listItemView: View) :
    RecyclerView.ViewHolder(listItemView) {

    private val customerBranchText = itemView.customerBranchText

    fun bind(branch: CustomerBranch) {
        customerBranchText.text = branch.branchName
    }

    companion object {
        fun newInstance(parent: ViewGroup) =
            CustomerBranchViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_customer_details_branch_list, parent, false)
            )
    }
}