package com.makeitez.acsalesapp.models.api.request

import com.makeitez.acsalesapp.models.SalesOrder
import com.squareup.moshi.Json

open class VerifySavedOrderRequestData {

    @field:Json(name = "accNo")
    var accNo: String? = null
        protected set

    @field:Json(name = "itemCodeList")
    var itemCodeList: List<String> = arrayListOf()
        protected set

    companion object {
        fun mapSalesOrder(salesOrder: SalesOrder) = VerifySavedOrderRequestData().apply {
            accNo = salesOrder.customer?.accountNumber
            itemCodeList = salesOrder.productOrders.mapNotNull { it.product?.itemCode }
        }
    }
}