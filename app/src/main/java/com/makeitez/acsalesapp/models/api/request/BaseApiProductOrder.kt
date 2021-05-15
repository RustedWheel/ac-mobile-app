package com.makeitez.acsalesapp.models.api.request

import com.squareup.moshi.Json

open class BaseApiProductOrder {
    @field:Json(name = "itemCode")
    var itemCode: String? = null
        protected set

    @field:Json(name = "description")
    var description: String? = null
        protected set

    @field:Json(name = "quantity")
    var quantity: Int? = null
        protected set

    @field:Json(name = "uom")
    var uom: String? = null
        protected set

    @field:Json(name = "unitPrice")
    var unitPrice: Double? = null
        protected set

    @field:Json(name = "focQuantity")
    var focQuantity: Int? = null
        protected set

    @field:Json(name = "newPrice")
    var newPrice: Double? = null
        protected set

    @field:Json(name = "discount")
    var discount: Double? = null
        protected set

    @field:Json(name = "destination")
    var destination: String? = null
        protected set

    @field:Json(name = "remarks")
    var remarks: String? = null
        protected set

    @field:Json(name = "minSalePrice")
    var minSalePrice: Double? = null
        protected set

    @field:Json(name = "rate")
    var rate: Double? = null
        protected set
}