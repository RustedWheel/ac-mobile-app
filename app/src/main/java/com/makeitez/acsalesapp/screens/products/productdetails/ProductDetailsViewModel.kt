package com.makeitez.acsalesapp.screens.products.productdetails

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.ProductService
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.dao.RealmProductDAO
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm

class ProductDetailsViewModel(application: Application) : ACViewModel(application) {

    private lateinit var productService: ProductService
    private var itemCode: String = ""

    val product: RealmObjectLiveData<Product> by lazy {
        (RealmProductDAO(Realm.getDefaultInstance()).get(itemCode) ?: Product()).asLiveData()
    }

    val showProductDetails = MutableLiveData<Boolean>()
    val downloadedPdfFileUri = MutableLiveData<Event<Uri>>()

    fun init(productCode: String, productService: ProductService) {
        this.productService = productService
        val hasInit = itemCode.isNotEmpty()
        if (!hasInit) {
            itemCode = productCode
            getProductDetails()
        }
    }

    fun getProductDetails() {
        withProgress {
            try {
                productService.getProductDetails(null, itemCode)
                showProductDetails.value = true
            } catch (e: Exception) {
                handleGenericException(e)
                showProductDetails.value = false
            }
        }
    }

    fun downloadProductDetailPdf(context: Context) {
        withProgress {
            try {
                val fileUri = productService.getProductDetailsPdf(context, itemCode)
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