package com.makeitez.acsalesapp.screens.announcement.newproduct

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.AnnouncementService
import com.makeitez.acsalesapp.services.ProductService
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.utils.Event
import io.realm.Case
import io.realm.Realm
import io.realm.kotlin.where

class AnnounceNewProductViewModel(application: Application): ACViewModel(application) {

    private lateinit var productService: ProductService
    private lateinit var announcementService: AnnouncementService

    private var newProductCode: String = ""
    private var newProductRemarks: String = ""

    val isSearchButtonEnabled = MutableLiveData<Boolean>(false)
    val verifiedProduct = MutableLiveData<Product>()
    val onAnnouncementAddedEvent = MutableLiveData<Event<Unit>>()

    fun init(productService: ProductService, announcementService: AnnouncementService) {
        this.productService = productService
        this.announcementService = announcementService
    }

    fun verifyProductCode() {
        withProgress {
            try {
                productService.getProductDetails(null, newProductCode)
                Realm.getDefaultInstance().where<Product>().equalTo("itemCode", newProductCode, Case.INSENSITIVE).findFirst()?.let { product ->
                    if(product.isActive()) {
                        verifiedProduct.value = product
                    } else {
                        setMessage(ErrorMessage(getString(R.string.announce_new_product_dialog_inactive_product_message, newProductCode)))
                    }
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun confirmNewProductAnnouncement() {
        val newProduct = verifiedProduct.value ?: return
        withProgress {
            try {
                announcementService.createNewProductAnnouncement(newProduct.itemCode, newProductRemarks)
                onAnnouncementAddedEvent.value = Event(Unit)
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onProductCodeChange(code: String) {
        newProductCode = code

        isSearchButtonEnabled.value = newProductCode.isNotBlank()
    }

    fun onProductRemarksChange(remarks: String) {
        newProductRemarks = remarks
    }
}