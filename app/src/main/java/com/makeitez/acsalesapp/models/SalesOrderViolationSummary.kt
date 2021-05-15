package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import java.util.*

open class SalesOrderViolationSummary: RealmObject() {
    @field:Json(name = "requestedUserId")
    var requestedUserId: String = ""; private set

    @field:Json(name = "requestedDatetime")
    var requestedDateTime: Date = Date(); private set

    @field:Json(name = "netTotal")
    var netTotal: Double = 0.0; private set

    @field:Json(name = "hasOverdue")
    var hasExceededOverdueLimit: Boolean = false; private set

    @field:Json(name = "hasExceededCreditLimit")
    var hasExceededCreditLimit: Boolean = false; private set

    @field:Json(name = "overdueSummary")
    var exceededOverdueLimitSummary: ExceededOverdueLimitSummary? = null; private set

    @field:Json(name = "exceedCreditLimitSummary")
    var exceededCreditLimitSummary: ExceededCreditLimitSummary? = null; private set
}