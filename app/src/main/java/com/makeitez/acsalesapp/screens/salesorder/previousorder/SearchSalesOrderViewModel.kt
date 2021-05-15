package com.makeitez.acsalesapp.screens.salesorder.previousorder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.utils.extensions.getDateFromPicker
import java.util.*

class SearchSalesOrderViewModel(application: Application) : ACViewModel(application) {

    data class State(
        val customer: String,
        val fromDate: Date?,
        val toDate: Date?
    )

    private var hasInit = false
    val state = MutableLiveData<State>()

    fun setup(search: String, fromDate: Date?, toDate: Date?) {
        if(!hasInit) {
            state.value = State(search, fromDate, toDate)
            hasInit = true
        }
    }

    fun setCustomerSearch(search: String) {
        if (search == state.value?.customer) return
        state.value = state.value?.copy(
            customer = search
        )
    }

    fun setFromDate(day: Int, month: Int, year: Int) {
        state.value = state.value?.copy(
            fromDate = getDateFromPicker(day, month, year)
        )
    }

    fun setToDate(day: Int, month: Int, year: Int) {
        state.value = state.value?.copy(
            toDate = getDateFromPicker(day, month, year)
        )
    }
}