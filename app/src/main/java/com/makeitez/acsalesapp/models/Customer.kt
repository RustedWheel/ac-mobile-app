package com.makeitez.acsalesapp.models

import com.makeitez.acsalesapp.models.api.CustomerListResponseData
import com.makeitez.acsalesapp.models.api.PreviousOrderSummaryResponseData
import com.makeitez.acsalesapp.models.enums.EntityStatus
import com.makeitez.acsalesapp.utils.extensions.clearAndAddAll
import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where

open class Customer : RealmObject() {

    @PrimaryKey
    @field:Json(name = "accNo")
    var accountNumber: String = ""; private set

    @field:Json(name = "companyName")
    var companyName: String = ""; private set

    @field:Json(name = "currencyCode")
    var currencyCode: String = ""; private set

    @field:Json(name = "currencyRate")
    var currencyRate: Double = 0.0; private set

    @field:Json(name = "creditLimit")
    var creditLimit: Double = 0.0; private set

    @field:Json(name = "overdueLimit")
    var overdueLimit: Double = 0.0; private set

    @field:Json(name = "taxType")
    var taxType: String? = null; private set

    @field:Json(name = "taxRate")
    var taxRate: Float? = null; private set

    @field:Json(name = "displayTerm")
    var displayTerm: String? = null; private set

    @field:Json(name = "overdue")
    var overdue: Double = 0.0; private set

    @field:Json(name = "availableLimit")
    var availableLimit: Double = 0.0; private set

    @field:Json(name = "branches")
    var branches: RealmList<CustomerBranch> = RealmList(); private set

    @field:Json(name = "address")
    var address: String = ""; private set

    @field:Json(name = "postcode")
    var postcode: String = ""; private set

    @field:Json(name = "phoneNo")
    var phoneNumber: String = ""; private set

    @field:Json(name = "faxNo")
    var faxNumber: String = ""; private set

    @field:Json(name = "contactInfo")
    var contactPerson: String = ""; private set

    @field:Json(name = "email")
    var email: String = ""; private set

    @field:Json(name = "salesAgent")
    var salesAgent: String = ""; private set

    var agingSummary: CustomerAgingSummary? = null; private set

    var paymentList: RealmList<AccountsReceivablePayment> = RealmList(); private set

    @field:Json(name = "status")
    var status: Int = 0; private set

    var lastUpdated: Long = System.currentTimeMillis()

    val customerStatus: EntityStatus
        get() = EntityStatus.valueOfOrInactive(status)

    fun hasBranches(): Boolean {
        return !branches.isNullOrEmpty()
    }

    fun isActive() = customerStatus == EntityStatus.Active

    companion object {

        fun fromId(accountNumber: String, realm: Realm): Customer {
            val customer = realm.where<Customer>().equalTo("accountNumber", accountNumber).findFirst()
            return customer ?: realm.createObject(Customer::class.java, accountNumber)
        }

        fun updateFromCustomerList(untrackedCustomers: List<CustomerListResponseData>, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedCustomers.forEach {
                updateFromUntracked(it, lastUpdatedTime, realm)
            }
            deleteAllObsolete(lastUpdatedTime, realm)
        }

        private fun updateFromUntracked(untrackedCustomer: CustomerListResponseData, updateTime: Long, realm: Realm): Customer =
            fromId(untrackedCustomer.accountNumber, realm).apply {
                companyName = untrackedCustomer.companyName
                currencyCode = untrackedCustomer.currencyCode
                lastUpdated = updateTime
            }

        fun updateFromUntracked(untrackedPreviousOrder: PreviousOrderSummaryResponseData, updateTime: Long, realm: Realm): Customer =
            fromId(untrackedPreviousOrder.accountNumber, realm).apply {
                companyName = untrackedPreviousOrder.companyName
                untrackedPreviousOrder.currencyCode?.let {
                    currencyCode = it
                }
                lastUpdated = updateTime
            }

        fun updateFromUntracked(untrackedCustomer: Customer, updateTime: Long = System.currentTimeMillis(), realm: Realm): Customer {
            val customer = realm.copyToRealmOrUpdate(untrackedCustomer)
            customer.lastUpdated = updateTime
            return customer
        }

        fun updateWithAgingSummary(agingSummary: CustomerAgingSummary, updateTime: Long = System.currentTimeMillis(), realm: Realm): Customer =
            fromId(agingSummary.accountNumber, realm).apply {
                this.agingSummary = realm.copyToRealmOrUpdate(agingSummary)
                lastUpdated = updateTime
            }

        fun updateWithPaymentList(arPaymentList: List<AccountsReceivablePayment>, accNo: String, updateTime: Long = System.currentTimeMillis(), realm: Realm): Customer =
            fromId(accNo, realm).apply {
                this.paymentList.clearAndAddAll(arPaymentList)
                lastUpdated = updateTime
            }

        fun deleteAllObsolete(timeInMillis: Long, realm: Realm) {
            realm.where<Customer>().lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }
    }
}