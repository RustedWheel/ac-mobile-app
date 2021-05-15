package com.makeitez.acsalesapp.services

import android.content.Context
import android.net.Uri
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.ProductCategory
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import io.realm.Realm

class ProductService(private val networkService: NetworkService) {

    suspend fun fetchProductCategories() {
        val productCategoryResponse = networkService.call {
            it.getProductCategories()
        }
        val untrackedCategories = productCategoryResponse.data ?: listOf()
        Realm.getDefaultInstance().executeTransaction { realm ->
            ProductCategory.updateFromCategoryList(untrackedCategories, realm)
        }
    }

    suspend fun fetchProducts(accountNumber: String?, category: String) {
        val productsResponse = networkService.call {
            it.getProducts(accountNumber, category)
        }
        val untrackedProducts = productsResponse.data ?: listOf()
        Realm.getDefaultInstance().executeTransaction { realm ->
            Product.updateFromProductList(untrackedProducts, category, realm)
        }
    }

    suspend fun getProductDetails(accountNumber: String?, itemCode: String) {
        val productResponse = networkService.call {
            it.getProductDetails(accountNumber, itemCode)
        }
        productResponse.data?.let {
            Realm.getDefaultInstance().executeTransaction { realm ->
                Product.updateFromUntracked(it, realm = realm)
            }
        }
    }

    suspend fun getProductDetailsPdf(appContext: Context, itemCode: String): Uri? {
        val responseBody = networkService.callForResponse {
            it.downloadProductDetailsPdf(itemCode)
        }
        return responseBody.body()?.byteStream()?.let {
            FileSystemHelper.savePdfFileToDownloadsFolder(appContext, it, itemCode)
        }
    }

    suspend fun fetchProductHistory(itemCode: String) {
        val productHistoryResponse = networkService.call {
            it.getProductHistoryList(itemCode)
        }
        val untrackedProductHistoryList = productHistoryResponse.data ?: listOf()
        Realm.getDefaultInstance().executeTransaction { realm ->
            ProductHistory.updateFromProductHistoryList(itemCode, untrackedProductHistoryList, realm)
        }
    }

    suspend fun getProductHistoryDetails(productHistoryId: String, docNo: String, itemCode: String) {
        val productHistoryDetailsResponse = networkService.call {
            it.getProductHistoryDetails(docNo, itemCode)
        }
        productHistoryDetailsResponse.data?.let { productHistory ->
            Realm.getDefaultInstance().executeTransaction { realm ->
                ProductHistory.updateFromUntrackedDetailed(productHistoryId, productHistory, realm = realm)
            }
        }
    }
}