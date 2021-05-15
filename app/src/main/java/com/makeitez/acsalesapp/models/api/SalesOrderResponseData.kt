package com.makeitez.acsalesapp.models.api

import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.SalesOrderViolationSummary
import com.makeitez.acsalesapp.models.api.request.BaseApiSalesOrder
import com.squareup.moshi.Json
import java.util.*

class SalesOrderResponseData: BaseApiSalesOrder() {
    @field:Json(name = "branchName")
    var branchName: String? = null; private set

    @field:Json(name = "itemDetails")
    var productOrderResponses: Array<ProductOrderResponseData> = arrayOf(); private set

    @field:Json(name = "createdDatetime")
    var createdDatetime: Date = Date(); private set

    @field:Json(name = "orderStatus")
    var orderStatusCode: Int = 0; private set

    @field:Json(name = "salesAgent")
    var salesAgent: String? = null; private set

    @field:Json(name = "debtorDetails")
    var customerDetails: Customer = Customer(); private set

    @field:Json(name = "orderViolation")
    var salesOrderViolationSummary: SalesOrderViolationSummary? = null; private set

    @field:Json(name = "deliveryDate")
    var deliveryDate: Date? = null; private set
}