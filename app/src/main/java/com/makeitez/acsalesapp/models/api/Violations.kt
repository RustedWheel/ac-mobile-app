package com.makeitez.acsalesapp.models.api

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Violations(
    @field:Json(name = "hasNewPrice") val isNewPrice: Boolean,
    @field:Json(name = "hasDiscount") val hasDiscount: Boolean,
    @field:Json(name = "hasFOC") val hasFOC: Boolean,
    @field:Json(name = "hasOverdue") val hasOverdue: Boolean,
    @field:Json(name = "overdueAmount") val overdueAmount: Double,
    @field:Json(name = "hasExceededCreditLimit") val hasExceededCreditLimit: Boolean,
    @field:Json(name = "exceededCreditLimitAmount") val exceededCreditLimitAmount: Double
) : Parcelable