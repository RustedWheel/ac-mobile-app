package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject

open class ExceededCreditLimitSummary: RealmObject() {
    @field:Json(name = "arOutstanding")
    var arOutstanding: Double = 0.0; private set

    @field:Json(name = "doOutstanding")
    var doOutstanding: Double = 0.0; private set

    @field:Json(name = "soOutstanding")
    var soOutstanding: Double = 0.0; private set

    @field:Json(name = "pdCheque")
    var pdCheque: Double = 0.0; private set

    @field:Json(name = "totalOutstanding")
    var totalOutstanding: Double = 0.0; private set

    @field:Json(name = "originalCreditLimit")
    var originalCreditLimit: Double = 0.0; private set

    @field:Json(name = "creditLimitIncrement")
    var creditLimitIncrement: Double = 0.0; private set

    @field:Json(name = "increasedCreditLimit")
    var increasedCreditLimit: Double = 0.0; private set

    @field:Json(name = "exceededCredit")
    var exceededCredit: Double = 0.0; private set
}