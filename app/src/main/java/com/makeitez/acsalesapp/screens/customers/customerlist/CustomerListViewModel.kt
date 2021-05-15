package com.makeitez.acsalesapp.screens.customers.customerlist

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.CustomerService
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.ListContentState
import io.realm.Case
import io.realm.Realm
import io.realm.kotlin.where

class CustomerListViewModel(application: Application) : ACViewModel(application) {

    private lateinit var customerService: CustomerService
    private val searchTerm = MutableLiveData<String>()
    val customerList = MutableLiveData<List<Customer>>()
    val onCustomerSelectedEvent = MutableLiveData<Event<Customer>>()
    val contentState = MediatorLiveData<ListContentState>()

    init {
        contentState.addSource(customerList) {
            checkContentState()
        }
        contentState.addSource(inProgress) {
            checkContentState()
        }
        onSearchTermChanged("")
        fetchCustomers()
    }

    fun init(customerService: CustomerService){
        this.customerService = customerService
    }

    fun onSearchTermChanged(term: String) {
        searchTerm.value = term
        rebuildList()
    }

    private fun rebuildList() {
        val searchTerm = searchTerm.value ?: ""
        customerList.value = Realm.getDefaultInstance().where<Customer>()
            .beginGroup()
            .contains("companyName", searchTerm, Case.INSENSITIVE)
            .or()
            .contains("accountNumber", searchTerm, Case.INSENSITIVE)
            .endGroup()
            .sort("companyName")
            .findAll()
    }

    fun fetchCustomers() {
        withProgress {
            try {
                customerService.fetchCustomers()
                rebuildList()
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onCustomerClick(customer: Customer) {
        onCustomerSelectedEvent.value = Event(customer)
    }

    private fun checkContentState() {
        val searchTerm = searchTerm.value ?: ""
        contentState.value = when {
            customerList.value?.isNotEmpty() == true -> {
                ListContentState.NotEmpty
            }
            searchTerm.isNotEmpty() && inProgress.value != true -> {
                ListContentState.EmptySearch
            }
            searchTerm.isEmpty() && inProgress.value != true -> {
                ListContentState.EmptyData
            }
            else -> ListContentState.Empty
        }
    }
}