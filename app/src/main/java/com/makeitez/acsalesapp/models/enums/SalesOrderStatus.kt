package com.makeitez.acsalesapp.models.enums

enum class SalesOrderStatus(val code: Int) {
    Success(1),
    Pending(2),
    Rejected(3);

    companion object {
        fun valueOf(statusCode: Int): SalesOrderStatus? {
            return values().find {
                it.code == statusCode
            }
        }
    }
}