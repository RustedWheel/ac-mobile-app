package com.makeitez.acsalesapp.screens.customers

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.CustomerService
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.dao.RealmCustomerDAO
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm

class CustomerCreditViewModel(application: Application) : ACViewModel(application) {

    private var accountNo: String = ""

    private lateinit var customerService: CustomerService

    val customer: RealmObjectLiveData<Customer> by lazy {
        (RealmCustomerDAO(Realm.getDefaultInstance()).get(accountNo) ?: Customer()).asLiveData()
    }

    val shouldDismiss = MutableLiveData<Boolean>()

    fun init(customerAccountNumber: String, fetchApiDetails: Boolean, customerService: CustomerService) {
        this.customerService = customerService
        val hasInit = accountNo.isNotEmpty()
        if (!hasInit) {
            accountNo = customerAccountNumber
            if(fetchApiDetails) getCustomerDetails()
        }
    }

    private fun getCustomerDetails() {
        withProgress {
            try {
                customerService.getCustomerDetails(accountNo)
            } catch (e: Exception) {
                handleGenericException(e)
                shouldDismiss.value = true
            }
        }
    }
}