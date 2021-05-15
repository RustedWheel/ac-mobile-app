package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import java.util.*


open class AccountsReceivablePayment : RealmObject() {
    @field:Json(name = "docNo")
    var docNo: String = ""; private set

    @field:Json(name = "docDate")
    var docDate: Date = Date(); private set

    @field:Json(name = "accNo")
    var accountNumber: String = ""; private set

    @field:Json(name = "companyName")
    var companyName: String = ""; private set

    @field:Json(name = "description")
    var description: String = ""; private set

    @field:Json(name = "currencyCode")
    var currencyCode: String = ""; private set

    @field:Json(name = "amount")
    var amount: Double = 0.0; private set

    @field:Json(name = "unappliedAmount")
    var unappliedAmount: Double = 0.0; private set

    @field:Json(name = "knockOffAmount")
    var knockOffAmount: Double = 0.0; private set

    @field:Json(name = "chqrv")
    var chequeReceivalDate: Date = Date(); private set

    @field:Json(name = "createdUser")
    var createdUser: String = ""; private set

    @field:Json(name = "createdDatetime")
    var createdDatetime: Date = Date(); private set

    @field:Json(name = "lastModifiedUser")
    var lastModifiedUser: String = ""; private set

    @field:Json(name = "lastModifiedDatetime")
    var lastModifiedDatetime: Date = Date(); private set
}