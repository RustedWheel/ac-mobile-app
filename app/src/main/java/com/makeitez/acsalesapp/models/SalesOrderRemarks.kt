package com.makeitez.acsalesapp.models

import com.makeitez.acsalesapp.utils.extensions.isNotNullOrBlank
import io.realm.RealmObject
import java.util.*

open class SalesOrderRemarks: RealmObject() {

    var deliveryDate: Date = Date()
    var po: String = ""
    var remarks: String = ""

    fun isFilled(): Boolean {
        return po.isNotNullOrBlank() || remarks.isNotNullOrBlank()
    }

    companion object {
        fun createUntracked(deliveryDate: Date = Date(), po: String = "", remarks: String = ""): SalesOrderRemarks {
            val salesOrderRemarks = SalesOrderRemarks()
            salesOrderRemarks.deliveryDate = deliveryDate
            salesOrderRemarks.po = po
            salesOrderRemarks.remarks = remarks
            return salesOrderRemarks
        }
    }
}