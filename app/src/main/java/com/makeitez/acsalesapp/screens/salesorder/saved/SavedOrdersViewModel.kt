package com.makeitez.acsalesapp.screens.salesorder.saved

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.SalesOrderService
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class SavedOrdersViewModel(application: Application) : ACViewModel(application) {

    private lateinit var salesOrderService: SalesOrderService
    val onSalesOrderVerifiedEvent = MutableLiveData<Event<SalesOrder>>()

    val savedOrders =
        Realm.getDefaultInstance()
            .where<SalesOrder>()
            .equalTo("isLocal", true)
            .sort("createdDatetime", Sort.DESCENDING)
            .findAll()
            .asLiveData()

    fun init(salesOrderService: SalesOrderService) {
        this.salesOrderService = salesOrderService
    }

    fun deleteSavedOrder(savedOrder: SalesOrder) {
        Realm.getDefaultInstance().executeTransaction {
            savedOrder.deleteFromRealm()
        }
    }

    fun deleteSavedOrder(savedOrderId: String) {
        Realm.getDefaultInstance().executeTransaction {
            val currentList = savedOrders.realmResults.toMutableList()
            currentList.find { it.docNo == savedOrderId }?.deleteFromRealm()
        }
    }

    fun verifySavedOrder(savedOrder: SalesOrder) {
        withProgress {
            try {
                val updatedOrder = salesOrderService.verifySavedOrder(savedOrder)
                if (updatedOrder != null) {
                    onSalesOrderVerifiedEvent.value = Event(updatedOrder)
                } else {
                    setMessage(ErrorMessage(getString(R.string.saved_order_customer_no_longer_exists)))
                }
                deleteSavedOrder(savedOrder)
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }
}