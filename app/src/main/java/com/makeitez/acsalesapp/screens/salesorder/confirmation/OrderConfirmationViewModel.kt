package com.makeitez.acsalesapp.screens.salesorder.confirmation

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.SalesOrderService
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.utils.Event

class OrderConfirmationViewModel(application: Application) : ACViewModel(application) {

    private lateinit var salesOrderService: SalesOrderService
    val onViewSalesOrderEvent = MutableLiveData<Event<SalesOrder>>()

    fun init(salesOrderService: SalesOrderService) {
        this.salesOrderService = salesOrderService
    }

    fun getSalesOrder(docNo: String, isSuccessful: Boolean) {
        val orderStatus = if (isSuccessful) SalesOrderStatus.Success else SalesOrderStatus.Pending

        withProgress {
            try {
                val pastSalesOrder = salesOrderService.getSalesOrder(docNo, orderStatus)
                onViewSalesOrderEvent.value = Event(pastSalesOrder)

            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

}