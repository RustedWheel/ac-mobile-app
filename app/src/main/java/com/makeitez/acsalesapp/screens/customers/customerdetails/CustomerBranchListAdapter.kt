package com.makeitez.acsalesapp.screens.customers.customerdetails

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.CustomerBranch
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class CustomerBranchListAdapter : RecyclerView.Adapter<CustomerBranchViewHolder>() {

    private val branchList: MutableList<CustomerBranch> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerBranchViewHolder =
        CustomerBranchViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: CustomerBranchViewHolder, position: Int) = holder.bind(branchList[position])

    override fun getItemCount() = branchList.size

    override fun getItemId(position: Int): Long = branchList[position].branchCode.hashCode().toLong()

    fun setList(newList: List<CustomerBranch>) {
        branchList.clearAndAddAll(newList)

        notifyDataSetChanged()
    }
}