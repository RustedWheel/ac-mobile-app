package com.makeitez.acsalesapp.screens.salesorder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.services.SalesOrderService
import com.makeitez.acsalesapp.models.*
import com.makeitez.acsalesapp.models.api.SalesOrderResult
import com.makeitez.acsalesapp.services.ResultCode
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.utils.Event
import java.util.*

class CreateSalesOrderViewModel(application: Application) : BaseSalesOrderViewModel(application) {

    data class CartActionState(
        val isRemarksFilled: Boolean,
        val canContinue: Boolean
    )

    data class ConfirmOrderDeletionData(
        val message: String,
        val productOrderPos: Int
    )

    data class ConfirmSalesOrderResult(
        val docNo: String,
        val isPendingApproval: Boolean,
        val isDuplicateOrder: Boolean
    )

    private lateinit var salesOrderService: SalesOrderService

    val customerBranchOptions: List<CustomerBranch>?
        get() = customer?.branches?.toMutableList()
                ?.apply { add(0, CustomerBranch()) } // add empty option to represent the customer (no branch) selection
                ?.map { CustomerBranch.createUntracked(it, getBranchOptionName(it.branchName)) }
                ?.toList()

    val isOrderSubmittable: Boolean
        get() = salesOrder.isSubmittable() && !ACUser.isAdmin()

    val branchSelection = MutableLiveData<Int>()
    val cartActionState = MutableLiveData<CartActionState>()

    val onViewExistingProductOrderEvent = MutableLiveData<Event<ProductOrder>>()
    val onConfirmOrderDeletionEvent = MutableLiveData<Event<ConfirmOrderDeletionData>>()
    val onExitCartEvent = MutableLiveData<Event<Unit>>()

    val onHasViolationEvent = MutableLiveData<Event<SalesOrderResult>>()

    val onSalesOrderConfirmedEvent = MutableLiveData<Event<ConfirmSalesOrderResult>>()

    fun init(salesOrderService: SalesOrderService) {
        this.salesOrderService = salesOrderService
    }

    fun startSalesOrder(customer: Customer) {
        this.salesOrder = SalesOrder.createUntracked(customer)
        startSalesOrder()
    }

    fun startSalesOrder(salesOrder: SalesOrder) {
        this.salesOrder = salesOrder
        startSalesOrder()
    }

    private fun startSalesOrder() {
        this.salesOrder.generateSubmissionRequestId()

        branchSelection.value = 0

        // restore selectedBranch, more for reordering a previous/saved order
        salesOrder.selectedBranch?.let { selectedBranch ->
            val selectedBranchPos = customerBranchOptions?.indexOfFirst {
                it.branchCode == selectedBranch.branchCode
            } ?: 0

            branchSelection.value = selectedBranchPos
        }

        handleSalesOrderChange()
    }

    fun createNewProductOrder(product: Product) {
        currentProductOrder = ProductOrder.createUntracked(product)
    }

    fun editProductOrder(productOrder: ProductOrder) {
        currentProductOrder = productOrder
    }

    fun incrementProductOrder(productOrder: ProductOrder) {
        productOrder.offset(1)

        handleSalesOrderChange()
    }

    fun decrementProductOrder(productOrder: ProductOrder) {
        if (productOrder.quantity <= 1) {
            confirmProductOrderDeletion(productOrder)

        } else {
            productOrder.offset(-1)

            handleSalesOrderChange()
        }
    }

    fun confirmProductOrderDeletion(productOrder: ProductOrder) {
        onConfirmOrderDeletionEvent.value = Event(
            ConfirmOrderDeletionData(
                getString(
                    R.string.cart_item_delete_confirmation,
                    productOrder.quantity,
                    productOrder.product?.description ?: ""
                ), productOrders.indexOf(productOrder)
            )
        )
    }

    fun deleteProductOrder(productOrderPos: Int) {
        productOrders.removeAt(productOrderPos)

        handleSalesOrderChange()
    }

    fun checkProductOrderExists(product: Product): Boolean
        = salesOrder.findProductOrder(product) != null

    fun viewExistingProductOrder(product: Product) {
        salesOrder.findProductOrder(product)?.let {
            onViewExistingProductOrderEvent.value = Event(it)
        }
    }

    fun selectBranch(branchPos: Int) {
        val selectedOption = customerBranchOptions?.getOrNull(branchPos) ?: return
        val selectedBranch = customer?.branches?.find{ it.branchCode == selectedOption.branchCode}

        salesOrder.setSelectedBranch(selectedBranch)
        branchSelection.value = branchPos
    }

    fun setRemarks(deliveryDate: Date, po: String, remarks: String) {
        val salesOrderRemark = SalesOrderRemarks.createUntracked(deliveryDate, po, remarks)
        salesOrder.setRemarks(salesOrderRemark)
        notifyCartActionStateChanged()
    }

    fun saveSalesOrder() {
        SalesOrder.saveLocally(salesOrder)
        leaveCart()
    }

    fun deleteSalesOrder() = leaveCart()
    
    private fun leaveCart() {
        onExitCartEvent.value = Event(Unit)
    }

    fun confirmCurrentProductOrder(productOrder: ProductOrder) {
        if(!productOrders.contains(productOrder)) {
            productOrders.add(productOrder)
        }
        handleSalesOrderChange()
    }

    override fun handleSpecificSalesOrderChange() {
        notifyCartActionStateChanged()
    }

    private fun notifyCartActionStateChanged() {
        cartActionState.value = CartActionState(
            salesOrder.remarks?.isFilled() ?: false,
            isOrderSubmittable
        )
    }

    fun createSalesOrder() {
        withProgress {
            try {
                val response = salesOrderService.createSalesOrder(salesOrder)
                response.data?.let {
                    when (response.resultCode) {
                        ResultCode.SUCCESS.value -> {
                            handleSalesOrderConfirmation(it.docNo, false)
                        }
                        ResultCode.PENDING_ORDER.value -> {
                            onHasViolationEvent.value = Event(it)
                        }
                        ResultCode.DUPLICATE_ORDER.value -> {
                            handleDuplicateSalesOrder(it.docNo, it.duplicateOrderStatus)
                        }
                    }
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun confirmSalesOrder(docNo: String) {
        withProgress {
            try {
                val response = salesOrderService.createSalesOrder(salesOrder, docNo)
                response.data?.let {
                    when (response.resultCode) {
                        ResultCode.DUPLICATE_ORDER.value -> {
                            handleDuplicateSalesOrder(it.docNo, it.duplicateOrderStatus)
                        }
                        else -> {
                            val isPendingApproval = response.resultCode == ResultCode.PENDING_APPROVAL.value
                            handleSalesOrderConfirmation(it.docNo, isPendingApproval)
                        }
                    }
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    private fun handleDuplicateSalesOrder(docNo: String, duplicateOrderStatus: SalesOrderStatus?) {
        duplicateOrderStatus?.let {
            handleSalesOrderConfirmation(docNo, it == SalesOrderStatus.Pending, true)
        }
    }

    private fun handleSalesOrderConfirmation(docNo: String, isPendingApproval: Boolean, isDuplicateOrder: Boolean = false) {
        onSalesOrderConfirmedEvent.value = Event(ConfirmSalesOrderResult(docNo, isPendingApproval, isDuplicateOrder))
    }

}