package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CustomerAgingSummary: RealmObject() {
    @PrimaryKey
    @field:Json(name = "accNo")
    var accountNumber: String = ""; private set

    @field:Json(name = "companyName")
    var companyName: String = ""; private set

    @field:Json(name = "current")
    var currentOutstanding: Double = 0.0; protected set

    @field:Json(name = "month1")
    var oneMonthOutstanding: Double = 0.0; protected set

    @field:Json(name = "month2")
    var twoMonthOutstanding: Double = 0.0; protected set

    @field:Json(name = "month3")
    var threeMonthOutstanding: Double = 0.0; protected set

    @field:Json(name = "month4")
    var fourMonthOutstanding: Double = 0.0; protected set

    @field:Json(name = "month5AndOver")
    var fiveMonthAndOverOutstanding: Double = 0.0; protected set

    @field:Json(name = "balance")
    var balance: Double = 0.0; protected set

    @field:Json(name = "totalOverdue")
    var totalOverdue: Double = 0.0; private set

    @field:Json(name = "detailAgingList")
    var detailAgingList: RealmList<CustomerAgingDetail> = RealmList(); private set

}