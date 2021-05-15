package com.makeitez.acsalesapp.screens.customers.customerlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll

class CustomerListAdapter(
    private val customerListListener: CustomerViewHolder.Listener) : RecyclerView.Adapter<CustomerViewHolder>() {

    private val customerList: MutableList<Customer> = arrayListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder = CustomerViewHolder.newInstance(parent, customerListListener)

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) = holder.bind(customerList[position])

    override fun getItemCount() = customerList.size

    override fun getItemId(position: Int): Long = customerList[position].accountNumber.hashCode().toLong()

    fun setList(newList: List<Customer>) {
        customerList.clearAndAddAll(newList)
        notifyDataSetChanged()
    }
}