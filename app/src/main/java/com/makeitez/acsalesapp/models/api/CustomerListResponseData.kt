package com.makeitez.acsalesapp.models.api

import com.squareup.moshi.Json

data class CustomerListResponseData(
    @field:Json(name = "accNo") val accountNumber: String,
    @field:Json(name = "companyName") val companyName: String,
    @field:Json(name = "currencyCode") val currencyCode: String
)