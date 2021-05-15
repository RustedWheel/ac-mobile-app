package com.makeitez.acsalesapp.screens.products.productdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_product_history_list.view.*

class ProductHistoryViewHolder(itemView: View, productHistoryListener: Listener) : RecyclerView.ViewHolder(itemView) {

    interface Listener {
        fun onProductHistoryClicked(productHistory: ProductHistory)
    }

    private val productHistoryCard = itemView.productHistoryCard
    private val productHistoryCustomerName = itemView.productHistoryCustomerName
    private val productHistoryCustomerAccountNumber = itemView.productHistoryCustomerAccountNumber
    private val productHistoryCreatedDate = itemView.productHistoryCreatedDate
    private val productHistoryQuantity = itemView.productHistoryQuantity
    private val productHistoryPrice = itemView.productHistoryPrice

    private var productHistory: ProductHistory? = null

    init {
        productHistoryCard.setOnClickListener {
            productHistory?.let(productHistoryListener::onProductHistoryClicked)
        }
    }

    fun bind(productHistory: ProductHistory) {
        this.productHistory = productHistory
        this.productHistoryCustomerName.text = if(productHistory.branchName.isNullOrEmpty()) {
            productHistory.companyName
        } else {
            itemView.context.getString(R.string.product_history_company_branch_name, productHistory.companyName, productHistory.branchName)
        }
        this.productHistoryCustomerAccountNumber.text = productHistory.accountNumber
        this.productHistoryCreatedDate.text = FormattingHelper.formatDate(productHistory.docDate)
        this.productHistoryQuantity.text = "${productHistory.quantity} ${productHistory.uom}"
        this.productHistoryPrice.text = FormattingHelper.formatUnitPrice(productHistory.finalUnitPrice, productHistory.currencyCode)
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener) =
            ProductHistoryViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_product_history_list, parent, false),
                listener
            )
    }
}