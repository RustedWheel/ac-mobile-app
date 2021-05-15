package com.makeitez.acsalesapp.screens.salesorder.cart

import android.widget.TextView
import com.makeitez.acsalesapp.models.CustomerBranch

class BranchViewHolder(private val branchText: TextView) {

    fun bind(branch: CustomerBranch?) {
        branch?.let {
            branchText.text = it.branchName
        }
    }
}