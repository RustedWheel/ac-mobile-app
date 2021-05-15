package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import java.util.*

open class PriceHistory : RealmObject() {

    @field:Json(name = "docType")
    var docType: String = ""; private set

    @field:Json(name = "docNo")
    var docNo: String = ""; private set

    @field:Json(name = "docDate")
    var docDate: Date = Date(); private set

    @field:Json(name = "quantity")
    var quantity: Int = 0; private set

    @field:Json(name = "uom")
    var uom: String = ""; private set

    @field:Json(name = "unitPrice")
    var unitPrice: Double = 0.0; private set

    @field:Json(name = "currencyCode")
    var currencyCode: String = ""; private set
}