package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json

data class HomeNotificationSummary(
    @field:Json(name = "announcement") val generalAnnouncement: Int = 0,
    @field:Json(name = "product") val newProductAnnouncement: Int = 0,
    @field:Json(name = "approval") val pendingApproval: Int = 0
)