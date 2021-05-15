package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmObject
import java.util.*

open class CustomerAgingDetail: RealmObject() {
    @field:Json(name = "docDate")
    var date: Date = Date()

    @field:Json(name = "docNo")
    var docNo: String = ""

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

    @field:Json(name = "isOverdue")
    var isOverdue: String = ""

    @field:Json(name = "outstanding")
    var totalOverdue: Double = 0.0

}