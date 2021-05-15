package com.makeitez.acsalesapp.screens.customers.aging

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.CustomerService
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.CustomerAgingSummary
import com.makeitez.acsalesapp.models.dao.RealmCustomerDAO
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm
import kotlinx.coroutines.launch

class AgingSummaryViewModel(application: Application) : ACViewModel(application) {

    data class ProgressStatus(val inProgress: Boolean, val hasError: Boolean = false)

    private lateinit var customerService: CustomerService

    private var accountNo: String = ""

    val customer: RealmObjectLiveData<Customer> by lazy {
        (RealmCustomerDAO(Realm.getDefaultInstance()).get(accountNo) ?: Customer()).asLiveData()
    }

    val agingSummary : LiveData<CustomerAgingSummary?> by lazy {
        Transformations.map(customer) {
            it.agingSummary
        }
    }

    val progressStatus = MutableLiveData<ProgressStatus>()

    fun init(customerAccountNumber: String, customerService: CustomerService) {
        this.customerService = customerService
        val hasInit = accountNo.isNotEmpty()
        if (!hasInit) {
            accountNo = customerAccountNumber
            fetchAgingSummary()
        }
    }

    fun fetchAgingSummary() {
        viewModelScope.launch {
            try {
                progressStatus.value = ProgressStatus(true)
                customerService.getAgingDetails(accountNo)
                progressStatus.value = ProgressStatus(false)
            } catch (e: Exception) {
                handleGenericException(e)
                progressStatus.value = ProgressStatus(inProgress = false, hasError = true)
            }
        }
    }

}