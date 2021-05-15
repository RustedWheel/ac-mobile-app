package com.makeitez.acsalesapp.models.api.request

import com.makeitez.acsalesapp.models.enums.AnnouncementType
import com.squareup.moshi.Json

class CreateAnnouncementRequestData {
    @field:Json(name = "type")
    var type: Int = 0; private set

    @field:Json(name = "title")
    var title: String? = null; private set

    @field:Json(name = "message")
    var message: String? = null; private set

    @field:Json(name = "itemCode")
    var itemCode: String? = null; private set

    @field:Json(name = "includeLatestList")
    var includeLatestList: Boolean? = null; private set

    companion object {
        fun createGeneralRequestData(title: String, message: String, includeLatestList: Boolean): CreateAnnouncementRequestData =
            CreateAnnouncementRequestData().apply {
                this.type = AnnouncementType.General.code
                this.title = title
                this.message = message
                this.includeLatestList = includeLatestList
            }

        fun createNewProductRequestData(itemCode: String, remarks: String, includeLatestList: Boolean): CreateAnnouncementRequestData =
            CreateAnnouncementRequestData().apply {
                this.type = AnnouncementType.NewProduct.code
                this.itemCode = itemCode
                this.message = remarks
                this.includeLatestList = includeLatestList
            }
    }
}