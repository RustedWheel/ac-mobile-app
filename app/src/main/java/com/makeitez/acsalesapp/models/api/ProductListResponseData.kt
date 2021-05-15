package com.makeitez.acsalesapp.models.api

import com.squareup.moshi.Json

data class ProductListResponseData(
    @field:Json(name = "itemCode") val itemCode: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "itemGroup") val itemGroup: String,
    @field:Json(name = "itemType") val itemType: String,
    @field:Json(name = "hasQuotation") val hasQuotation: Boolean,
    @field:Json(name = "imageUrl") val imageUrl: String
)