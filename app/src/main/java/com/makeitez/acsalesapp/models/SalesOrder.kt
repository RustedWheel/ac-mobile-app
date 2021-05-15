package com.makeitez.acsalesapp.models

import com.makeitez.acsalesapp.models.api.PreviousOrderSummaryResponseData
import com.makeitez.acsalesapp.models.api.SalesOrderResponseData
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.utils.extensions.times
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import java.math.BigDecimal
import java.util.*

open class SalesOrder : RealmObject(){

    @PrimaryKey
    var docNo: String = ""; private set

    var customer: Customer? = null; private set

    var selectedBranch: CustomerBranch? = null; private set

    var productOrders: RealmList<ProductOrder> = RealmList(); private set

    var remarks: SalesOrderRemarks? = null; private set

    var submissionRequestId: String = ""; private set

    var createdDatetime: Date = Date()

    // Branch and UOM can be altered over time, so we store the confirmed ones in an SO
    var confirmedBranchName: String? = null; private set
    var confirmedTotalAmount: Double = 0.0; private set
    var confirmedTaxAmount: Double = 0.0; private set

    private var orderStatusCode: Int = 0

    var salesAgent: String = ""; private set

    var salesOrderViolationSummary: SalesOrderViolationSummary? = null; private set

    var isLocal: Boolean = false; private set

    var lastUpdated: Long = System.currentTimeMillis()

    val orderStatus: SalesOrderStatus?
        get() = SalesOrderStatus.valueOf(orderStatusCode)

    fun calculateTotal(): BigDecimal? = if (isSubmittable()) {
        productOrders.fold(BigDecimal.ZERO) { total: BigDecimal, order: ProductOrder ->
            total + order.getPrice()
        }
    } else {
        null
    }

    fun calculateTax(): BigDecimal? {
        val total = calculateTotal()
        return total?.let {
            val taxRate = customer?.taxRate ?: 0f
            it * taxRate
        }
    }

    fun isSubmittable(): Boolean = productOrders.size > 0 && productOrders.all { it.isOrderValid() }

    fun generateSubmissionRequestId() {
        val userName = ACUser.getCurrentUser()?.userName ?: ""
        this.submissionRequestId = "$userName-${System.currentTimeMillis()}"
    }

    fun setSelectedBranch(selectedBranch: CustomerBranch?) {
        this.selectedBranch = selectedBranch
    }

    fun setRemarks(remarks: SalesOrderRemarks) {
        this.remarks = remarks
    }

    fun findProductOrder(product: Product): ProductOrder? {
        return productOrders.find { it.product?.itemCode == product.itemCode }
    }

    companion object {

        fun createUntracked(customer: Customer): SalesOrder {
            val orderRequest = SalesOrder()
            orderRequest.customer = customer
            orderRequest.remarks = SalesOrderRemarks.createUntracked()
            return orderRequest
        }

        fun fromId(docNo: String, realm: Realm): SalesOrder {
            val salesOrder = realm.where<SalesOrder>().equalTo("docNo", docNo).findFirst()
            return salesOrder ?: realm.createObject(SalesOrder::class.java, docNo)
        }

        fun updateFromPreviousOrderList(untrackedPreviousOrderList: List<PreviousOrderSummaryResponseData>, customerAccountNumber: String?, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedPreviousOrderList.forEach {
                updateFromUntracked(it, lastUpdatedTime, realm)
            }
            deleteNonLocalObsolete(lastUpdatedTime, customerAccountNumber, realm)
        }

        private fun updateFromUntracked(untrackedPreviousOrder: PreviousOrderSummaryResponseData, updateTime: Long, realm: Realm): SalesOrder =
            fromId(untrackedPreviousOrder.docNo, realm).apply {
                salesAgent = untrackedPreviousOrder.salesAgent
                customer = Customer.updateFromUntracked(untrackedPreviousOrder, updateTime, realm)
                confirmedTotalAmount = untrackedPreviousOrder.totalAmount
                createdDatetime = untrackedPreviousOrder.createdDatetime
                orderStatusCode = untrackedPreviousOrder.orderStatusCode
                isLocal = false
                lastUpdated = updateTime
            }


        private fun deleteNonLocalObsolete(timeInMillis: Long, customerAccountNumber: String?, realm: Realm) {
            realm.where<SalesOrder>()
                .equalTo("isLocal", false)
                .apply {
                    if (customerAccountNumber != null) {
                        equalTo("customer.accountNumber", customerAccountNumber)
                        equalTo("orderStatusCode", SalesOrderStatus.Success.code)
                    }
                }
                .lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }

        fun saveLocally(salesOrder: SalesOrder, realm: Realm = Realm.getDefaultInstance()) {
            realm.executeTransaction {
                val savedOrderCreatedTime = System.currentTimeMillis()
                salesOrder.docNo = savedOrderCreatedTime.toString()
                salesOrder.createdDatetime = Date(savedOrderCreatedTime)
                salesOrder.isLocal = true
                it.insertOrUpdate(salesOrder)
            }
        }

        fun mapFromPreviousOrder(previousSO: SalesOrderResponseData): SalesOrder {
            var trackedCustomer: Customer? = null
            Realm.getDefaultInstance().executeTransaction { realm ->
                trackedCustomer = Customer.updateFromUntracked(previousSO.customerDetails, realm = realm)
            }

            return SalesOrder().apply {
                docNo = previousSO.docNo ?: ""
                trackedCustomer?.let {trackedCustomer ->
                    customer = trackedCustomer
                    // branches can't be deleted after an order is made with it, so the Customer will have the branch
                    selectedBranch = trackedCustomer.branches.firstOrNull {
                        it.branchCode == previousSO.selectedBranchCode
                    }
                }
                previousSO.productOrderResponses.forEach {
                    productOrders.add(ProductOrder.mapFromPreviousProductOrder(it))
                }
                remarks = SalesOrderRemarks.createUntracked(previousSO.deliveryDate ?: Date(), previousSO.po ?: "", previousSO.orderRemarks ?: "")
                createdDatetime = previousSO.createdDatetime
                confirmedBranchName = previousSO.branchName
                confirmedTotalAmount = previousSO.totalAmount ?: 0.0
                confirmedTaxAmount = previousSO.taxAmount ?: 0.0
                orderStatusCode = previousSO.orderStatusCode
                salesAgent = previousSO.salesAgent ?: ""
                salesOrderViolationSummary = previousSO.salesOrderViolationSummary
                isLocal = false
            }
        }

        fun mapForReordering(previousSO: SalesOrder): SalesOrder {
            return SalesOrder().apply {
                customer = previousSO.customer
                selectedBranch = previousSO.selectedBranch
                remarks = previousSO.remarks?.run {
                    SalesOrderRemarks.createUntracked(po = po, remarks = remarks)
                } ?: SalesOrderRemarks.createUntracked()
                previousSO.productOrders.filter { it.product?.productStatus?.isActive() ?: false }
                    .map {
                        productOrders.add(ProductOrder.mapForReordering(it))
                    }
            }
        }
    }
}