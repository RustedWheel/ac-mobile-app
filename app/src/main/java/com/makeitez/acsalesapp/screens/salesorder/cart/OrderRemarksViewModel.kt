package com.makeitez.acsalesapp.screens.salesorder.cart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.utils.extensions.getDateFromPicker
import java.util.*

class OrderRemarksViewModel(application: Application): ACViewModel(application) {
    data class State(
        val deliveryDate: Date,
        val po: String,
        val remarks: String
    )

    val state = MutableLiveData<State>()

    var hasInit = false

    fun init(deliveryDate: Date, po: String, remarks: String) {
        if(!hasInit) {
            state.value = State(deliveryDate, po, remarks)
            hasInit = true
        }
    }

    fun setDeliveryDate(day: Int, month: Int, year: Int) {
        state.value = state.value?.copy(
            deliveryDate = getDateFromPicker(day, month, year)
        )
    }

    fun setPO(po: String){
        state.value = state.value?.copy(
            po = po
        )
    }

    fun setRemarks(remarks: String){
        state.value = state.value?.copy(
            remarks = remarks
        )
    }

}