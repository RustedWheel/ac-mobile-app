package com.makeitez.acsalesapp.screens.customers.customerlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.Customer
import kotlinx.android.synthetic.main.item_customer_list.view.*

class CustomerViewHolder(listItemView: View, customerListListener: Listener) : RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onCustomerClick(customer: Customer)
        fun onCustomerInfoClick(customer: Customer)
    }

    private val customerCard = itemView.customerCard
    private val customerNameText = itemView.customerNameText
    private val customerCurrencyText = itemView.customerCurrencyText
    private val customerAccNoText = itemView.customerAccNoText
    private val customerInfoImage = itemView.customerInfoImage

    private var customer: Customer? = null

    init {
        customerCard.setOnClickListener {
            customer?.let(customerListListener::onCustomerClick)
        }

        customerInfoImage.setOnClickListener {
            customer?.let(customerListListener::onCustomerInfoClick)
        }
    }

    fun bind(boundCustomer: Customer) {
        customer = boundCustomer

        customerNameText.text = boundCustomer.companyName
        customerCurrencyText.text = boundCustomer.currencyCode
        customerAccNoText.text = boundCustomer.accountNumber
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener) =
            CustomerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_customer_list, parent, false),
                listener
            )
    }
}