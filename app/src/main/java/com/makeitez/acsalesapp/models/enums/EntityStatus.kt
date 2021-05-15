package com.makeitez.acsalesapp.models.enums

enum class EntityStatus(val code: Int) {
    Inactive(0),
    Active(1);

    fun isActive() = this == Active

    companion object {
        fun valueOf(statusCode: Int): EntityStatus? {
            return values().find {
                it.code == statusCode
            }
        }

        fun valueOfOrInactive(statusCode: Int): EntityStatus {
            return valueOf(statusCode) ?: Inactive
        }
    }
}