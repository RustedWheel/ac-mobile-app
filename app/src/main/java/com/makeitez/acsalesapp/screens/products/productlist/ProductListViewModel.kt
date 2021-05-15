package com.makeitez.acsalesapp.screens.products.productlist

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.ProductService
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.ListContentState
import io.realm.Case
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.launch

class ProductListViewModel(application: Application) : ACViewModel(application) {

    private lateinit var productService: ProductService
    private var accountNo: String? = null
    private var category: String = ""
    private var isSalesOrderFlow: Boolean = false

    private val searchTerm = MutableLiveData<String>()
    val productDetailsProgress = MutableLiveData<Boolean>()
    val productList = MutableLiveData<List<Product>>()
    val onProductSelectedEvent = MutableLiveData<Event<Product>>()
    val contentState = MediatorLiveData<ListContentState>()

    init {
        contentState.addSource(productList) {
            checkContentState()
        }
        contentState.addSource(inProgress) {
            checkContentState()
        }
    }

    fun init(customerAccountNumber: String?, productCategory: String, isSalesOrderFlow: Boolean, productService: ProductService) {
        val hasInit = this.category.isNotEmpty()
        this.accountNo = customerAccountNumber
        this.category = productCategory
        this.isSalesOrderFlow = isSalesOrderFlow
        this.productService = productService
        if (!hasInit) {
            onSearchTermChanged("")
            fetchProductList()
        }
    }

    fun onSearchTermChanged(term: String) {
        searchTerm.value = term
        rebuildList()
    }

    private fun rebuildList() {
        val searchTerm = searchTerm.value ?: ""
        productList.value = Realm.getDefaultInstance().where<Product>()
            .apply {
                if (category != Product.ALL_ITEM_TYPE) {
                    equalTo("itemType", category)
                }
            }
            .beginGroup()
            .contains("description", searchTerm, Case.INSENSITIVE)
            .or()
            .contains("itemCode", searchTerm, Case.INSENSITIVE)
            .endGroup()
            .sort("description")
            .findAll()
    }

    fun fetchProductList() {
        withProgress {
            try {
                productService.fetchProducts(accountNo, category)
                rebuildList()
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onProductClick(product: Product) {
        if (isSalesOrderFlow) {
            getProductDetails(product)
        } else {
            onProductSelectedEvent.value = Event(product)
        }
    }

    private fun getProductDetails(product: Product) {
        viewModelScope.launch {
            try {
                productDetailsProgress.value = true
                productService.getProductDetails(accountNo, product.itemCode)
                onProductSelectedEvent.value = Event(product)
            } catch (e: Exception) {
                handleGenericException(e)
            } finally {
                productDetailsProgress.value = false
            }
        }
    }

    private fun checkContentState() {
        val searchTerm = searchTerm.value ?: ""
        contentState.value = when {
            productList.value?.isNotEmpty() == true -> {
                ListContentState.NotEmpty
            }
            searchTerm.isNotEmpty() && inProgress.value != true -> {
                ListContentState.EmptySearch
            }
            searchTerm.isEmpty() && inProgress.value != true -> {
                ListContentState.EmptyData
            }
            else -> ListContentState.Empty
        }
    }
}