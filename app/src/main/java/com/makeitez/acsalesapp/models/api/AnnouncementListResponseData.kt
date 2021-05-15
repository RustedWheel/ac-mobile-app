package com.makeitez.acsalesapp.models.api

import com.makeitez.acsalesapp.models.GeneralAnnouncement
import com.makeitez.acsalesapp.models.NewProductAnnouncement
import com.squareup.moshi.Json

data class AnnouncementListResponseData(
    @field:Json(name = "announcementList") val generalAnnouncements: List<GeneralAnnouncement>,
    @field:Json(name = "productList") val newProductAnnouncements: List<NewProductAnnouncement>
)