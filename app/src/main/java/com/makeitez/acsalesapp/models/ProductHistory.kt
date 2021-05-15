package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import java.util.*

open class ProductHistory : RealmObject() {

    @PrimaryKey
    var uuid: String = ""; private set

    @field:Json(name = "docNo")
    var docNo: String = ""; private set

    @field:Json(name = "accNo")
    var accountNumber: String = ""

    @field:Json(name = "companyName")
    var companyName: String = ""

    @field:Json(name = "docDate")
    var docDate: Date = Date(); private set

    @field:Json(name = "quantity")
    var quantity: Int = 0; private set

    @field:Json(name = "uom")
    var uom: String? = null; private set

    @field:Json(name = "unitPrice")
    var unitPrice: Double = 0.0; private set

    @field:Json(name = "selectedBranchCode")
    var selectedBranchCode: String? = null; private set

    @field:Json(name = "branchName")
    var branchName: String? = null; private set

    @field:Json(name = "currencyCode")
    var currencyCode: String = ""; private set

    @field:Json(name = "discount")
    var discount: Double = 0.0; private set

    @field:Json(name = "foc")
    var foc: Int = 0; private set

    @field:Json(name = "destination")
    var destination: String = ""; private set

    @field:Json(name = "deliveryDate")
    var deliveryDate: Date = Date(); private set

    @field:Json(name = "remarks")
    var remarks: String = ""; private set

    @field:Json(name = "newPrice")
    var newPrice: Double? = null; private set

    @field:Json(name = "rate")
    var rate: Double = 0.0; private set

    var itemCode: String = ""; private set

    var finalUnitPrice: Double = 0.0; private set

    var lastUpdated: Long = System.currentTimeMillis()

    companion object {

        fun fromId(uuid: String, realm: Realm): ProductHistory {
            val productHistory = realm.where<ProductHistory>().equalTo("uuid", uuid).findFirst()
            return productHistory ?: realm.createObject(ProductHistory::class.java, uuid)
        }

        fun updateFromProductHistoryList(productCode: String, untrackedProductHistoryList: List<ProductHistory>, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedProductHistoryList.forEach {
                updateFromUntracked(productCode, it, lastUpdatedTime, realm)
            }
            deleteAllObsolete(productCode, lastUpdatedTime, realm)
        }

        fun updateFromUntracked(productCode: String, untrackedProductHistory: ProductHistory, updateTime: Long = System.currentTimeMillis(), realm: Realm) =
            fromId("$productCode-${untrackedProductHistory.docNo}", realm).apply {
                docNo = untrackedProductHistory.docNo
                accountNumber = untrackedProductHistory.accountNumber
                companyName = untrackedProductHistory.companyName
                docDate = untrackedProductHistory.docDate
                quantity = untrackedProductHistory.quantity
                uom = untrackedProductHistory.uom
                unitPrice = untrackedProductHistory.unitPrice


                selectedBranchCode = untrackedProductHistory.selectedBranchCode
                branchName = untrackedProductHistory.branchName
                currencyCode = untrackedProductHistory.currencyCode
                itemCode = productCode
                finalUnitPrice = untrackedProductHistory.unitPrice
                lastUpdated = updateTime
            }

        fun updateFromUntrackedDetailed(productHistoryUuid: String, untrackedProductHistory: ProductHistory, updateTime: Long = System.currentTimeMillis(), realm: Realm) =
            fromId(productHistoryUuid, realm).apply {
                docNo = untrackedProductHistory.docNo
                accountNumber = untrackedProductHistory.accountNumber
                companyName = untrackedProductHistory.companyName
                docDate = untrackedProductHistory.docDate
                quantity = untrackedProductHistory.quantity
                uom = untrackedProductHistory.uom
                unitPrice = untrackedProductHistory.unitPrice

                discount = untrackedProductHistory.discount
                foc = untrackedProductHistory.foc
                destination = untrackedProductHistory.destination
                deliveryDate = untrackedProductHistory.deliveryDate
                remarks = untrackedProductHistory.remarks
                newPrice = untrackedProductHistory.newPrice
                rate = untrackedProductHistory.rate
                lastUpdated = updateTime
            }

        fun deleteAllObsolete(productCode: String? = null, timeInMillis: Long, realm: Realm) {
            realm.where<ProductHistory>().lessThan("lastUpdated", timeInMillis)
                .apply {
                    if (productCode != null) {
                        equalTo("itemCode", productCode)
                    }
                }
                .findAll()
                .deleteAllFromRealm()
        }
    }
}