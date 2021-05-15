package com.makeitez.acsalesapp.models.api

import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.Product
import com.squareup.moshi.Json

data class VerifySalesOrderResponseData(
    @field:Json(name = "debtorDetails") val customer: Customer,
    @field:Json(name = "productDetailsList") val products: List<Product>
)