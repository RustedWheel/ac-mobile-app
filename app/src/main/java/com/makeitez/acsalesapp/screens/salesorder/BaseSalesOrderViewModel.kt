package com.makeitez.acsalesapp.screens.salesorder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.SalesOrderRemarks
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import java.math.BigDecimal

abstract class BaseSalesOrderViewModel(application: Application) : ACViewModel(application) {
    data class CartTotalData(
        val total: BigDecimal?,
        val totalLabel: String,
        val totalTax: BigDecimal?,
        val totalTaxLabel: String
    )

    // Cart models
    protected lateinit var salesOrder: SalesOrder

    var currentProductOrder: ProductOrder? = null
        protected set

    // Model property accessors
    val customer: Customer?
        get() = salesOrder.customer

    val currency: String?
        get() = customer?.currencyCode

    val currencyRate: Double?
        get() = customer?.currencyRate

    protected val productOrders: MutableList<ProductOrder>
        get() = salesOrder.productOrders

    val salesOrderRemarks: SalesOrderRemarks?
        get() = salesOrder.remarks

    // Common live data
    val productOrderList = MutableLiveData<List<ProductOrder>>()

    val cartTotalData = MutableLiveData<CartTotalData>()

    protected fun handleSalesOrderChange() {
        notifyProductOrderListChanged()
        notifyCartTotalDataChanged()
        handleSpecificSalesOrderChange()
    }

    abstract fun handleSpecificSalesOrderChange()

    private fun notifyProductOrderListChanged() {
        productOrderList.value = productOrders.toList()
    }

    private fun notifyCartTotalDataChanged() {
        val total = salesOrder.calculateTotal()
        val totalTax = salesOrder.calculateTax()
        val totalLabel = getTotalLabel(total)
        val totalTaxLabel = getTotalTaxLabel(totalTax)

        cartTotalData.value = CartTotalData(
            total,
            totalLabel,
            totalTax,
            totalTaxLabel
        )
    }

    protected fun getTotalLabel(total: BigDecimal?): String {
        return currency?.let { currency ->
            total?.let { totalAmount ->
                FormattingHelper.formatPrice(totalAmount, currency)
            } ?: getString(R.string.cart_unknown_price, currency)
        } ?: ""
    }

    protected fun getTotalTaxLabel(totalTax: BigDecimal?): String {
        return totalTax?.let { totalTaxAmount ->
            getString(R.string.cart_tax, FormattingHelper.formatPrice(totalTaxAmount))
        } ?: ""
    }

    protected fun getBranchOptionName(branchName: String?): String {
        val customerName = customer?.companyName ?: ""

        return if(branchName.isNullOrEmpty()) {
            customerName
        } else {
            getString(R.string.cart_branch_option, customerName, branchName)
        }
    }
}