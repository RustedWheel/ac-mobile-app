package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import java.util.*

open class DiffPrice: RealmObject() {
    @field:Json(name = "docDate")
    var docDate: Date = Date(); private set

    @field:Json(name = "unitPrice")
    var unitPrice: Double = 0.0; private set

    companion object {
        fun createStub(stubUnitPrice: Double) = DiffPrice().apply {  unitPrice =  stubUnitPrice }
    }
}