package com.makeitez.acsalesapp.screens.salesorder

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.services.SalesOrderService
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.SalesOrderViolationSummary
import com.makeitez.acsalesapp.models.api.SalesOrderResult
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.services.ResultCode
import com.makeitez.acsalesapp.services.api.ACAPIException
import com.makeitez.acsalesapp.utils.Event
import io.realm.Realm
import io.realm.kotlin.where
import java.util.*


class ViewSalesOrderViewModel(application: Application) : BaseSalesOrderViewModel(application) {
    enum class PendingOrderConfirmationType {
        APPROVED,
        REJECTED,
        INVALID
    }

    data class OrderCustomerInfo(
        val accountNumber: String,
        val companyOrBranchName: String,
        val currencyCode: String
    )

    data class SubmissionRecord(
        val submissionDocNo: String,
        val submissionDateTime: Date
    )

    private lateinit var salesOrderService: SalesOrderService

    private val salesOrderDocNo: String
        get() = salesOrder.docNo

    val orderStatus: SalesOrderStatus?
        get() = salesOrder.orderStatus

    val confirmedTotalAmount: Double
        get() = salesOrder.confirmedTotalAmount

    val confirmedTaxAmount: Double
        get() = salesOrder.confirmedTaxAmount

    val salesOrderViolationSummary: SalesOrderViolationSummary?
        get() = salesOrder.salesOrderViolationSummary

    val isCreditLimitExceeded: Boolean
        get() = salesOrderViolationSummary?.hasExceededCreditLimit ?: false

    val isOverdueLimitExceeded: Boolean
        get() = salesOrderViolationSummary?.hasExceededOverdueLimit ?: false

    val orderCustomerInfo = MutableLiveData<OrderCustomerInfo>()
    val submissionRecord = MutableLiveData<SubmissionRecord>()
    val submissionStatusAndTotal = MutableLiveData<String>()
    val onReorderSalesOrderEvent = MutableLiveData<Event<SalesOrder>>()
    val downloadedPdfFileUri = MutableLiveData<Event<Uri>>()

    val onHasSOViolationEvent = MutableLiveData<Event<SalesOrderResult>>()
    val onSOConfirmedMessageEvent = MutableLiveData<Event<String>>()

    fun init(salesOrderService: SalesOrderService) {
        this.salesOrderService = salesOrderService
    }

    fun viewSalesOrder(salesOrderToView: SalesOrder) {
        this.salesOrder = salesOrderToView

        mapCustomerInfoToDisplay()

        mapDocNoAndDatetimeToDisplay()

        mapOrderStatusAndTotalToDisplay()

        handleSalesOrderChange()
    }

    fun viewProductOrder(productOrder: ProductOrder) {
        currentProductOrder = productOrder
    }

    private fun mapCustomerInfoToDisplay() {
        customer?.let {
            orderCustomerInfo.value = OrderCustomerInfo(
                it.accountNumber,
                getBranchOptionName(salesOrder.confirmedBranchName),
                it.currencyCode
            )
        }
    }

    private fun mapDocNoAndDatetimeToDisplay() {
        submissionRecord.value = SubmissionRecord(
            salesOrderDocNo,
            salesOrder.createdDatetime
        )
    }

    private fun mapOrderStatusAndTotalToDisplay() {
        val totalLabelStringRes = when (orderStatus) {
            SalesOrderStatus.Success -> R.string.cart_total_label
            SalesOrderStatus.Pending -> R.string.cart_pending_total_label
            SalesOrderStatus.Rejected -> R.string.cart_rejected_total_label
            else -> return
        }

        // show the confirmed total and tax according to the total/tax rate at the point of submission
        val totalLabel = getTotalLabel(confirmedTotalAmount.toBigDecimal())
        val totalTaxLabel = getTotalTaxLabel(confirmedTaxAmount.toBigDecimal())

        submissionStatusAndTotal.value = getString(totalLabelStringRes, totalLabel, totalTaxLabel)
    }

    override fun handleSpecificSalesOrderChange() {
    }

    fun reorderSalesOrder() {
        onReorderSalesOrderEvent.value = Event(SalesOrder.mapForReordering(salesOrder))
    }

    fun confirmPendingSalesOrder(acceptSalesOrder: Boolean) {
        withProgress {
            try {
                val response = salesOrderService.confirmPendingSalesOrder(salesOrderDocNo, acceptSalesOrder)
                when (response.resultCode) {
                    ResultCode.SUCCESS.value -> {
                        handleSalesOrderConfirmation(
                            if (acceptSalesOrder) {
                                PendingOrderConfirmationType.APPROVED
                            } else {
                                PendingOrderConfirmationType.REJECTED
                            }
                        )
                    }
                    ResultCode.CONFIRM_APPROVAL.value -> {
                        response.data?.let {
                            onHasSOViolationEvent.value = Event(it)
                        }
                    }
                    ResultCode.DUPLICATE_ORDER.value -> {
                        handleDuplicateSalesOrderConfirmation(response.data?.duplicateOrderStatus)
                    }
                }
            } catch (e: Exception) {
                if(e is ACAPIException && e.code == ResultCode.INVALID_ORDER.value) {
                    // Pending Order can become invalid from a referred Branch/UOM getting deleted, then it will be rejected in backend
                    handleSalesOrderConfirmation(PendingOrderConfirmationType.INVALID)
                } else {
                    handleGenericException(e)
                }
            }
        }
    }

    fun confirmPendingSalesOrderWithSOViolations() {
        withProgress {
            try {
                val response = salesOrderService.confirmPendingSalesOrderWithSOViolations(salesOrderDocNo)
                when (response.resultCode) {
                    ResultCode.DUPLICATE_ORDER.value -> {
                        handleDuplicateSalesOrderConfirmation(response.data?.duplicateOrderStatus)
                    }
                    else -> {
                        handleSalesOrderConfirmation(PendingOrderConfirmationType.APPROVED)
                    }
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    private fun handleDuplicateSalesOrderConfirmation(duplicateOrderStatus: SalesOrderStatus?) {
        duplicateOrderStatus?.let {
            handleSalesOrderConfirmation(if(it == SalesOrderStatus.Success) PendingOrderConfirmationType.APPROVED else PendingOrderConfirmationType.REJECTED)
        }
    }

    private fun handleSalesOrderConfirmation(confirmationType: PendingOrderConfirmationType) {
        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.where<SalesOrder>()
                .equalTo("isLocal", false)
                .equalTo("docNo", salesOrderDocNo)
                .findAll()
                .deleteAllFromRealm()
        }

        onSOConfirmedMessageEvent.value = Event(getString(
            when(confirmationType) {
                PendingOrderConfirmationType.APPROVED -> R.string.pending_order_approved_message
                PendingOrderConfirmationType.REJECTED -> R.string.pending_order_rejected_message
                PendingOrderConfirmationType.INVALID -> R.string.pending_order_invalid_message
            }
        ))
    }

    fun downloadStatement(context: Context) {
        withProgress {
            try {
                val fileUri = salesOrderService.downloadSalesOrderStatement(context, salesOrder.docNo)
                if (fileUri != null) {
                    downloadedPdfFileUri.value = Event(fileUri)
                } else {
                    setMessage(ErrorMessage(getString(R.string.generic_error_something_went_wrong_when_download_pdf)))
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }
}