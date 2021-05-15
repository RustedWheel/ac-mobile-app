package com.makeitez.acsalesapp.screens.customers.aging

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.makeitez.acsalesapp.components.tableview.ACTableData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.CustomerAgingDetail
import com.makeitez.acsalesapp.models.dao.RealmCustomerDAO
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import io.realm.Realm

class AgingDetailsViewModel(application: Application) : ACViewModel(application) {
    private var accountNo: String = ""

    val customer: RealmObjectLiveData<Customer> by lazy {
        (RealmCustomerDAO(Realm.getDefaultInstance()).get(accountNo) ?: Customer()).asLiveData()
    }

    val agingTableData : LiveData<ACTableData?> by lazy {
        Transformations.map(customer) { customer ->
            customer?.agingSummary?.detailAgingList?.let {
                mapAgingListToTableData(it)
            }
        }
    }

    val contentState : LiveData<ListContentState> by lazy {
        Transformations.map(customer) {
            if(it?.agingSummary?.detailAgingList.isNullOrEmpty()) {
                ListContentState.EmptyData
            } else {
                ListContentState.NotEmpty
            }
        }
    }

    fun init(customerAccountNumber: String) {
        val hasInit = accountNo.isNotEmpty()
        if (!hasInit) {
            accountNo = customerAccountNumber
        }
    }

    private fun mapAgingListToTableData(agingList: List<CustomerAgingDetail>): ACTableData {
        return ACTableData(
            rowHeaderHeader = "Date",
            columnHeader = listOf("Doc No", "Current", "1 Month", "2 Months", "3 Months", "4 Months", "5 & Over", "Balance", "Is Overdue", "Total Overdue"),
            rowHeaderData = agingList.map { FormattingHelper.formatDate(it.date) },
            columnData = agingList.map { listOf(
                it.docNo,
                FormattingHelper.formatPriceIfNotZero(it.currentOutstanding),
                FormattingHelper.formatPriceIfNotZero(it.oneMonthOutstanding),
                FormattingHelper.formatPriceIfNotZero(it.twoMonthOutstanding),
                FormattingHelper.formatPriceIfNotZero(it.threeMonthOutstanding),
                FormattingHelper.formatPriceIfNotZero(it.fourMonthOutstanding),
                FormattingHelper.formatPriceIfNotZero(it.fiveMonthAndOverOutstanding),
                FormattingHelper.formatPrice(it.balance),
                it.isOverdue,
                FormattingHelper.formatPrice(it.totalOverdue)
            )
            }
        )
    }

}
