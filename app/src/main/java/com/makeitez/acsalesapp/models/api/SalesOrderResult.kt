package com.makeitez.acsalesapp.models.api

import android.os.Parcelable
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesOrderResult(
    @field:Json(name = "docNo") val docNo: String,
    @field:Json(name = "soReportName") val soReportName: String?,
    @field:Json(name = "violationReason") val violations: Violations?,
    @field:Json(name = "orderStatus") val duplicateOrderStatusCode: Int?) : Parcelable {

    val duplicateOrderStatus: SalesOrderStatus?
        get() = duplicateOrderStatusCode?.let {
            SalesOrderStatus.valueOf(it)
        }
}