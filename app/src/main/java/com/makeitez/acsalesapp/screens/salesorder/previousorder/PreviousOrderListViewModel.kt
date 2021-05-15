package com.makeitez.acsalesapp.screens.salesorder.previousorder

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.SalesOrderService
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.endOfDay
import com.makeitez.acsalesapp.utils.extensions.startOfDay
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import io.realm.Case
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.coroutines.launch
import org.apache.commons.lang3.time.DateUtils
import java.util.*

class PreviousOrderListViewModel(application: Application) : ACViewModel(application) {

    data class OrderSearchInfo(
        val searchedCustomerName: String,
        val searchedDateRange: String
    )

    private lateinit var salesOrderService: SalesOrderService
    private var customerAccountNumber: String? = null
    private var orderStatusCode: Int = SalesOrderStatus.Success.code
    private var hasInit = false
    var isUserSearchEnabled: Boolean = false
    var customerNameSearch: String = ""; private set
    var fromDate: Date? = null; private set
    var toDate: Date? = null; private set

    val orderSearchInfo = MutableLiveData<OrderSearchInfo>()
    val previousOrderList = MutableLiveData<List<SalesOrder>>()
    val contentState = MediatorLiveData<ListContentState>()
    val salesOrderDetailsProgress = MutableLiveData<Boolean>()
    val onSalesOrderDetailsLoadedEvent = MutableLiveData<Event<SalesOrder>>()

    fun init(viewType: PreviousOrdersViewType, customerAccountNumber: String? = null, salesOrderService: SalesOrderService) {
        this.salesOrderService = salesOrderService
        if (!hasInit) {
            when(viewType) {
                PreviousOrdersViewType.CustomerPreviousOrders -> {
                    this.customerAccountNumber = customerAccountNumber
                    this.orderStatusCode = SalesOrderStatus.Success.code
                }
                PreviousOrdersViewType.OrderHistory -> {
                    this.isUserSearchEnabled = true
                    this.orderStatusCode = All_ORDER_STATUS_CODE
                }
                PreviousOrdersViewType.PendingApprovals -> {
                    this.orderStatusCode = SalesOrderStatus.Pending.code
                }
            }

            contentState.addSource(previousOrderList) {
                checkContentState()
            }
            contentState.addSource(inProgress) {
                checkContentState()
            }

            if (viewType == PreviousOrdersViewType.OrderHistory) {
                resetSearch()
            } else {
                rebuildData()
                fetchPreviousOrders()
            }

            hasInit = true
        } else {
            rebuildData() // needed when coming back to this screen from deleting
        }
    }

    fun resetSearch() {
        setSearch("", DateUtils.addWeeks(Date(), -1), Date())
    }

    fun setSearch(customerName: String, fromDate: Date?, toDate: Date?) {
        this.customerNameSearch = customerName
        this.fromDate = fromDate
        this.toDate = toDate
        rebuildData()
        mapSearchInfoForDisplay()
        fetchPreviousOrders()
    }

    private fun mapSearchInfoForDisplay() {
        orderSearchInfo.value = OrderSearchInfo(
            searchedCustomerName = customerNameSearch,
            searchedDateRange = getString(R.string.previous_order_custom_date_range, FormattingHelper.formatDate(fromDate), FormattingHelper.formatDate(toDate))
        )
    }

    fun fetchPreviousOrders() {
        withProgress {
            try {
                salesOrderService.fetchSalesOrders(customerNameSearch.takeIf { it.isNotEmpty() }, fromDate, toDate, orderStatusCode, customerAccountNumber)
                rebuildData()
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onPreviousOrderSelected(previousOrder: SalesOrder) {
        getSalesOrderDetails(previousOrder)
    }

    private fun getSalesOrderDetails(previousOrder: SalesOrder) {
        val salesOrderStatus = previousOrder.orderStatus ?: return
        viewModelScope.launch {
            salesOrderDetailsProgress.value = true
            try {
                val pastSalesOrder = salesOrderService.getSalesOrder(previousOrder.docNo, salesOrderStatus)
                onSalesOrderDetailsLoadedEvent.value = Event(pastSalesOrder)
            } catch (e: Exception) {
                handleGenericException(e)
            }
            salesOrderDetailsProgress.value = false
        }
    }

    private fun rebuildData() {
        previousOrderList.value = Realm.getDefaultInstance()
            .where<SalesOrder>()
            .equalTo("isLocal", false)
            .apply {
                if (orderStatusCode != All_ORDER_STATUS_CODE) {
                    equalTo("orderStatusCode", orderStatusCode)
                }
                if (customerAccountNumber != null) {
                    equalTo("customer.accountNumber", customerAccountNumber)
                }
                if (customerNameSearch.isNotEmpty()) {
                    beginGroup()
                    contains("customer.companyName", customerNameSearch, Case.INSENSITIVE)
                    or()
                    contains("customer.accountNumber", customerNameSearch, Case.INSENSITIVE)
                    endGroup()
                }
                if (fromDate != null && toDate != null) {
                    between("createdDatetime", fromDate?.startOfDay(), toDate?.endOfDay())
                }
            }
            .sort("createdDatetime", Sort.DESCENDING)
            .findAll()
            .toMutableList()
    }

    private fun checkContentState() {
        contentState.value = when {
            previousOrderList.value?.isNotEmpty() == true -> ListContentState.NotEmpty
            inProgress.value != true -> ListContentState.EmptyData
            else -> ListContentState.Empty
        }
    }

    companion object {
        private const val All_ORDER_STATUS_CODE = 0
    }

}