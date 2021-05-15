package com.makeitez.acsalesapp.services

import android.content.Context
import android.net.Uri
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import io.realm.Realm

class CustomerService(private val networkService: NetworkService) {

    suspend fun fetchCustomers() {
        val response = networkService.call {
            it.getCustomers()
        }
        val untrackedCustomers = response.data ?: listOf()
        Realm.getDefaultInstance().executeTransaction { realm ->
            Customer.updateFromCustomerList(untrackedCustomers, realm)
        }
    }

    suspend fun getCustomerDetails(accountNumber: String) {
        val response = networkService.call {
            it.getCustomerDetails(accountNumber)
        }
        response.data?.let {
            Realm.getDefaultInstance().executeTransaction { realm ->
                Customer.updateFromUntracked(it, realm = realm)
            }
        }
    }

    suspend fun downloadMonthlyStatement(appContext: Context, accountNumber: String): Uri? {
        val responseBody = networkService.callForResponse {
            it.downloadCustomerMonthlyStatement(accountNumber)
        }
        return responseBody.body()?.byteStream()?.let {
            FileSystemHelper.savePdfFileToDownloadsFolder(appContext, it, accountNumber)
        }
    }

    suspend fun getAgingDetails(accountNumber: String) {
        val response = networkService.call {
            it.getAgingDetails(accountNumber)
        }
        response.data?.let {
            Realm.getDefaultInstance().executeTransaction { realm ->
                Customer.updateWithAgingSummary(it, realm = realm)
            }
        }
    }

    suspend fun getPaymentList(accountNumber: String) {
        val response = networkService.call {
            it.getPaymentList(accountNumber)
        }
        response.data?.let {
            Realm.getDefaultInstance().executeTransaction { realm ->
                Customer.updateWithPaymentList(it, accountNumber, realm = realm)
            }
        }
    }

    suspend fun downloadPaymentDetailStatement(appContext: Context, accountNumber: String, docNo: String): Uri? {
        val responseBody = networkService.callForResponse {
            it.downloadPaymentDetailStatement(accountNumber, docNo)
        }
        return responseBody.body()?.byteStream()?.let {
            FileSystemHelper.savePdfFileToDownloadsFolder(appContext, it, "$accountNumber-$docNo")
        }
    }
}