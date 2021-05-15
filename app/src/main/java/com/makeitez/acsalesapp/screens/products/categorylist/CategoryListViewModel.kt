package com.makeitez.acsalesapp.screens.products.categorylist

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.ProductService
import com.makeitez.acsalesapp.models.ProductCategory
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.ListContentState
import io.realm.Case
import io.realm.Realm
import io.realm.kotlin.where

class CategoryListViewModel(application: Application) : ACViewModel(application) {

    private lateinit var productService: ProductService
    private val searchTerm = MutableLiveData<String>()
    val categoryList = MutableLiveData<List<ProductCategory>>()
    val onCategorySelectedEvent = MutableLiveData<Event<String>>()
    val contentState = MediatorLiveData<ListContentState>()

    init {
        contentState.addSource(categoryList) {
            checkContentState()
        }
        contentState.addSource(inProgress) {
            checkContentState()
        }
        onSearchTermChanged("")
        fetchCategoryList()
    }

    fun init(productService: ProductService) {
        this.productService = productService
    }

    fun onSearchTermChanged(term: String) {
        searchTerm.value = term
        rebuildList()
    }

    private fun rebuildList() {
        val searchTerm = searchTerm.value ?: ""
        categoryList.value = Realm.getDefaultInstance().where<ProductCategory>()
            .contains("description", searchTerm, Case.INSENSITIVE)
            .sort("description")
            .findAll()
    }

    fun fetchCategoryList() {
        withProgress {
            try {
                productService.fetchProductCategories()
                rebuildList()
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onCategoryClick(productCategory: ProductCategory) {
        onCategorySelectedEvent.value = Event(productCategory.type)
    }

    private fun checkContentState() {
        val searchTerm = searchTerm.value ?: ""
        contentState.value = when {
            categoryList.value?.isNotEmpty() == true -> {
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