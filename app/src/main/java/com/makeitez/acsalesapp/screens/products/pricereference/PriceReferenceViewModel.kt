package com.makeitez.acsalesapp.screens.products.pricereference

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.models.PriceHistory
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.Uom

class PriceReferenceViewModel(application: Application): ACViewModel(application) {
    val uomList = MutableLiveData<Pair<List<Uom>, String>>()
    val priceHistoryList = MutableLiveData<List<PriceHistory>>()

    fun setPriceReferenceData(currency: String, currentProductOrder: ProductOrder?) {
        if(currentProductOrder != null) {

            val product = currentProductOrder.product ?: return

            uomList.value = Pair(product.uomDetailList, currency)
            priceHistoryList.value = product.priceHistory
        }
    }

}