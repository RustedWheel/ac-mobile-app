package com.makeitez.acsalesapp.models.api.request

import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.api.PayloadDate
import com.squareup.moshi.Json

class CreateSalesOrderRequestData : BaseApiSalesOrder() {
    @field:Json(name = "itemDetails")
    private var itemDetails: Array<CreateProductOrderRequestData>? = null

    @field:Json(name = "isReject")
    private var isReject: Boolean? = null

    @field:Json(name = "isConfirmApproval")
    private var isConfirmApproval: Boolean? = null

    @field:Json(name = "deliveryDate")
    private var deliveryDate: String? = null

    @field:Json(name = "requestId")
    private var requestId: String? = null

    companion object {
        fun mapSalesOrder(salesOrder: SalesOrder, confirmationDocNo: String? = null) =
            CreateSalesOrderRequestData()
                .apply {
                    // Passing the doc no means we are confirming order
                    if (confirmationDocNo != null) {
                        docNo = confirmationDocNo
                    }
                    requestId = salesOrder.submissionRequestId
                    accNo = salesOrder.customer?.accountNumber
                    companyName = salesOrder.customer?.companyName
                    description = ""
                    selectedBranchCode = salesOrder.selectedBranch?.branchCode
                    itemDetails = salesOrder.productOrders.map {
                        CreateProductOrderRequestData.mapFromProductOrder(it)
                    }.toTypedArray()
                    orderRemarks = salesOrder.remarks?.remarks
                    po = salesOrder.remarks?.po
                    totalAmount = salesOrder.calculateTotal()?.toDouble()
                    taxAmount = salesOrder.calculateTax()?.toDouble()
                    currencyCode = salesOrder.customer?.currencyCode
                    currencyRate = salesOrder.customer?.currencyRate
                    deliveryDate = salesOrder.remarks?.deliveryDate?.let { PayloadDate(it).toString() }
                }

        fun confirmPendingSORequestData(docNo: String, approve: Boolean) =
            CreateSalesOrderRequestData().apply {
                this.docNo = docNo
                this.isReject = !approve
            }

        fun confirmPendingSOWithSOViolationsRequestData(docNo: String) =
            CreateSalesOrderRequestData().apply {
                this.docNo = docNo
                this.isConfirmApproval = true
            }
    }
}