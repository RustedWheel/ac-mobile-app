package com.makeitez.acsalesapp.screens.products.productdetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.ProductService
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.models.dao.RealmProductDAO
import com.makeitez.acsalesapp.models.dao.RealmProductHistoryDAO
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm

class ProductHistoryDetailsViewModel(application: Application) : ACViewModel(application) {

    private lateinit var productService: ProductService
    private var productHistoryId: String = ""
    private var docNo: String = ""
    private var itemCode: String = ""

    val product: RealmObjectLiveData<Product> by lazy {
        (RealmProductDAO(Realm.getDefaultInstance()).get(itemCode) ?: Product()).asLiveData()
    }

    val productHistory: RealmObjectLiveData<ProductHistory> by lazy {
        (RealmProductHistoryDAO(Realm.getDefaultInstance()).get(productHistoryId) ?: ProductHistory()).asLiveData()
    }

    val showProductHistoryDetails = MutableLiveData<Boolean>()

    fun init(productHistoryUuid: String, docNo: String, productCode: String, productService: ProductService) {
        this.productService = productService
        val hasInit = productHistoryId.isNotEmpty()
        if (!hasInit) {
            this.productHistoryId = productHistoryUuid
            this.docNo = docNo
            this.itemCode = productCode
            getProductHistoryDetails()
        }
    }

    fun getProductHistoryDetails() {
        withProgress {
            try {
                productService.getProductHistoryDetails(productHistoryId, docNo, itemCode)
                showProductHistoryDetails.value = true
            } catch (e: Exception) {
                handleGenericException(e)
                showProductHistoryDetails.value = false
            }
        }
    }

}