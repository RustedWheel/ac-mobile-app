package com.makeitez.acsalesapp.screens.customers.paymentlist

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.tableview.ACTableData
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.CustomerService
import com.makeitez.acsalesapp.models.AccountsReceivablePayment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.dao.RealmCustomerDAO
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import io.realm.Realm
import kotlinx.coroutines.launch

class PaymentListViewModel(application: Application): ACViewModel(application) {
    private var accountNo: String = ""
    private var selectedPaymentRow: Int = 0;

    private lateinit var customerService: CustomerService

    val customer: RealmObjectLiveData<Customer> by lazy {
        (RealmCustomerDAO(Realm.getDefaultInstance()).get(accountNo) ?: Customer()).asLiveData()
    }

    val paymentListData : LiveData<ACTableData?> by lazy {
        Transformations.map(customer) { customer ->
            customer?.paymentList?.let {
                mapPaymentListToTableData(it)
            }
        }
    }

    val hasError = MutableLiveData<Boolean>()
    val contentState = MediatorLiveData<ListContentState>()
    val paymentDetailProgress = MutableLiveData<Boolean>()
    val downloadedPdfFileUri = MutableLiveData<Event<Uri>>()

    fun init(customerAccountNumber: String, customerService: CustomerService) {
        this.customerService = customerService
        val hasInit = accountNo.isNotEmpty()
        if (!hasInit) {
            accountNo = customerAccountNumber

            contentState.addSource(customer) { checkContentState() }
            contentState.addSource(inProgress) { checkContentState() }
            contentState.addSource(hasError) { checkContentState() }

            fetchPaymentList()
        }
    }

    fun fetchPaymentList() {
        withProgress {
            try {
                hasError.value = false
                customerService.getPaymentList(accountNo)
            } catch (e: Exception) {
                hasError.value = true
                handleGenericException(e)
            }
        }
    }

    private fun mapPaymentListToTableData(paymentList: List<AccountsReceivablePayment>): ACTableData {
        return ACTableData(
            rowHeaderHeader = "Doc No",
            columnHeader = listOf("Date", "Description", "Amount", "Unapplied Amount", "CHQRV", "Knock-off Amount"),
            rowHeaderData = paymentList.map { it.docNo },
            columnData = paymentList.map { listOf(
                FormattingHelper.formatDate(it.docDate),
                it.description,
                FormattingHelper.formatPrice(it.amount),
                FormattingHelper.formatPrice(it.unappliedAmount),
                FormattingHelper.formatDate(it.chequeReceivalDate),
                FormattingHelper.formatPrice(it.knockOffAmount)
            ) }
        )
    }

    fun onPaymentRowClick(row: Int) {
        selectedPaymentRow = row
    }

    private fun checkContentState() {
        contentState.value = when {
            inProgress.value == true -> ListContentState.Empty
            hasError.value == true -> ListContentState.Empty
            !customer.value?.paymentList.isNullOrEmpty() -> ListContentState.NotEmpty
            else -> ListContentState.EmptyData
        }
    }

    fun downloadPaymentDetailStatement(context: Context) {
        val paymentInfoList = customer.value?.paymentList ?: return
        val docNo = paymentInfoList.getOrNull(selectedPaymentRow)?.docNo ?: return

        viewModelScope.launch {
            paymentDetailProgress.value = true
            try {
                val fileUri = customerService.downloadPaymentDetailStatement(context, accountNo, docNo)
                if(fileUri != null) {
                    downloadedPdfFileUri.value = Event(fileUri)
                } else {
                    setMessage(ErrorMessage(getString(R.string.generic_error_something_went_wrong_when_download_pdf)))
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
            paymentDetailProgress.value = false
        }
    }

}