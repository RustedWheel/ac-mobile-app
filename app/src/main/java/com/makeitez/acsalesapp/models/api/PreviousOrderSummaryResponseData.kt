package com.makeitez.acsalesapp.models.api

import com.squareup.moshi.Json
import java.util.*

data class PreviousOrderSummaryResponseData(
    @field:Json(name = "docNo") var docNo: String,
    @field:Json(name = "salesAgent") var salesAgent: String,
    @field:Json(name = "accNo") val accountNumber: String,
    @field:Json(name = "companyName") var companyName: String,
    @field:Json(name = "currencyCode") val currencyCode: String?,
    @field:Json(name = "totalAmount") var totalAmount: Double,
    @field:Json(name = "createdDatetime") var createdDatetime: Date,
    @field:Json(name = "orderStatus") var orderStatusCode: Int
)