package com.makeitez.acsalesapp.models.api.request

import com.squareup.moshi.Json

open class BaseApiSalesOrder {
    @field:Json(name = "docNo")
    var docNo: String? = null
        protected set

    @field:Json(name = "accNo")
    var accNo: String? = null
        protected set

    @field:Json(name = "companyName")
    var companyName: String? = null
        protected set

    @field:Json(name = "description")
    var description: String? = null
        protected set

    @field:Json(name = "selectedBranchCode")
    var selectedBranchCode: String? = null
        protected set

    @field:Json(name = "orderRemarks")
    var orderRemarks: String? = null
        protected set

    @field:Json(name = "po")
    var po: String? = null
        protected set

    @field:Json(name = "totalAmount")
    var totalAmount: Double? = null
        protected set

    @field:Json(name = "taxAmount")
    var taxAmount: Double? = null
        protected set

    @field:Json(name = "currencyCode")
    var currencyCode: String? = null
        protected set

    @field:Json(name = "currencyRate")
    var currencyRate: Double? = null
        protected set
}