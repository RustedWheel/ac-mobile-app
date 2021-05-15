package com.makeitez.acsalesapp.screens.customers.customerdetails

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.CustomerService
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.dao.RealmCustomerDAO
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm

class CustomerDetailsViewModel(application: Application) : ACViewModel(application) {

    private lateinit var customerService: CustomerService

    private var accountNo: String = ""

    val customer: RealmObjectLiveData<Customer> by lazy {
        (RealmCustomerDAO(Realm.getDefaultInstance()).get(accountNo) ?: Customer()).asLiveData()
    }

    val shouldShowDetails = MutableLiveData<Boolean>()
    val downloadedPdfFileUri = MutableLiveData<Event<Uri>>()

    fun init(customerAccountNumber: String, customerService: CustomerService) {
        this.customerService = customerService
        val hasInit = accountNo.isNotEmpty()
        if (!hasInit) {
            accountNo = customerAccountNumber
            getCustomerDetails()
        }
    }

    fun getCustomerDetails() {
        withProgress {
            try {
                customerService.getCustomerDetails(accountNo)
                shouldShowDetails.value = true
            } catch (e: Exception) {
                handleGenericException(e)
                shouldShowDetails.value = false
            }
        }
    }

    fun downloadMonthlyStatement(context: Context) {
        withProgress {
            try {
                val fileUri = customerService.downloadMonthlyStatement(context, accountNo)
                if(fileUri != null) {
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