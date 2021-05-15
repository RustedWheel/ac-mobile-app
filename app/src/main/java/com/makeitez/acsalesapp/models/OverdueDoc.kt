package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import java.util.*

open class OverdueDoc : RealmObject() {
    @field:Json(name = "docNo")
    var docNo: String = ""; private set

    @field:Json(name = "docDate")
    var docDate: Date = Date(); private set

    @field:Json(name = "creditTerm")
    var creditTerm: String = ""; private set

    @field:Json(name = "dueDate")
    var dueDate: Date = Date(); private set

    @field:Json(name = "currencyCode")
    var currencyCode: String = ""; private set

    @field:Json(name = "currencyRate")
    var currencyRate: Double = 0.0; private set

    @field:Json(name = "outstanding")
    var outstanding: Double = 0.0; private set
}