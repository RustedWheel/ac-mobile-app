package com.makeitez.acsalesapp.screens.products.productdetails

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.ProductService
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.ListContentState
import io.realm.Case
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class ProductHistoryListViewModel(application: Application) : ACViewModel(application) {

    data class SelectedProductHistorySummary(val productHistoryId: String, val docNo: String, val productCode: String)

    private lateinit var productService: ProductService
    private var itemCode: String = ""

    private val searchTerm = MutableLiveData<String>()
    val productHistoryList = MutableLiveData<List<ProductHistory>>()
    val onProductHistorySelectedEvent = MutableLiveData<Event<SelectedProductHistorySummary>>()
    val contentState = MediatorLiveData<ListContentState>()

    init {
        contentState.addSource(productHistoryList) {
            checkContentState()
        }
        contentState.addSource(inProgress) {
            checkContentState()
        }
    }

    fun init(productCode: String, productService: ProductService) {
        this.productService = productService
        val hasInit = this.itemCode.isNotEmpty()
        this.itemCode = productCode
        if (!hasInit) {
            onSearchTermChanged("")
            fetchProductHistory()
        }
    }

    fun onSearchTermChanged(term: String) {
        searchTerm.value = term
        rebuildList()
    }

    private fun rebuildList() {
        val searchTerm = searchTerm.value ?: ""
        productHistoryList.value = Realm.getDefaultInstance().where<ProductHistory>()
            .equalTo("itemCode", itemCode)
            .beginGroup()
                .contains("companyName", searchTerm, Case.INSENSITIVE)
                .or()
                .contains("branchName", searchTerm, Case.INSENSITIVE)
                .or()
                .contains("accountNumber", searchTerm, Case.INSENSITIVE)
            .endGroup()
            .sort("docDate", Sort.DESCENDING)
            .findAll()
    }

    fun fetchProductHistory() {
        withProgress {
            try {
                productService.fetchProductHistory(itemCode)
                rebuildList()
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onProductHistoryClick(productHistory: ProductHistory) {
        onProductHistorySelectedEvent.value = Event(SelectedProductHistorySummary(productHistory.uuid, productHistory.docNo, productHistory.itemCode))
    }

    private fun checkContentState() {
        val searchTerm = searchTerm.value ?: ""
        contentState.value = when {
            productHistoryList.value?.isNotEmpty() == true -> {
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