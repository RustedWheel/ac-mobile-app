package com.makeitez.acsalesapp.models.enums

enum class AnnouncementType(val code: Int) {
    General(1),
    NewProduct(2);

    companion object {
        fun valueOf(typeCode: Int): AnnouncementType? {
            return values().find {
                it.code == typeCode
            }
        }

        fun valueOfOrGeneral(statusCode: Int): AnnouncementType {
            return valueOf(statusCode) ?: General
        }
    }
}