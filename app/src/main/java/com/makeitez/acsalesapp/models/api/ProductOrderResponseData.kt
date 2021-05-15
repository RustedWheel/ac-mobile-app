package com.makeitez.acsalesapp.models.api

import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.api.request.BaseApiProductOrder
import com.squareup.moshi.Json
import java.util.*

class ProductOrderResponseData: BaseApiProductOrder() {
    @field:Json(name = "deliveryDate")
    var deliveryDate: Date? = null; private set

    @field:Json(name = "productDetail")
    var productDetails: Product = Product(); private set
}