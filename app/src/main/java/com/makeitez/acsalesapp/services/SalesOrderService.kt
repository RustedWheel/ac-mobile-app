package com.makeitez.acsalesapp.services

import android.content.Context
import android.net.Uri
import com.makeitez.acsalesapp.models.ApiResponse
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.models.api.PayloadDate
import com.makeitez.acsalesapp.models.api.SalesOrderResult
import com.makeitez.acsalesapp.models.api.request.CreateSalesOrderRequestData
import com.makeitez.acsalesapp.models.api.request.VerifySavedOrderRequestData
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import io.realm.Realm
import java.util.*

class SalesOrderService(private val networkService: NetworkService) {

    suspend fun createSalesOrder(
        salesOrder: SalesOrder,
        docNo: String? = null
    ): ApiResponse<SalesOrderResult> = networkService.callWithData { flexiApi, _ ->
        flexiApi.createSalesOrder(CreateSalesOrderRequestData.mapSalesOrder(salesOrder, docNo))
    }

    suspend fun confirmPendingSalesOrder(
        docNo: String,
        approve: Boolean
    ): ApiResponse<SalesOrderResult> = networkService.callWithData { flexApi, _ ->
        flexApi.createSalesOrder(
            CreateSalesOrderRequestData.confirmPendingSORequestData(
                docNo,
                approve
            )
        )
    }

    suspend fun confirmPendingSalesOrderWithSOViolations(docNo: String): ApiResponse<SalesOrderResult> =
        networkService.callWithData { flexApi, _ ->
            flexApi.createSalesOrder(
                CreateSalesOrderRequestData.confirmPendingSOWithSOViolationsRequestData(
                    docNo
                )
            )
        }

    suspend fun getSalesOrder(docNo: String, orderStatus: SalesOrderStatus): SalesOrder {
        val salesOrderResponse = networkService.call {
            it.getSalesOrder(docNo, orderStatus.code)
        }

        return salesOrderResponse.data?.let {
            SalesOrder.mapFromPreviousOrder(it)
        } ?: SalesOrder()
    }

    suspend fun fetchSalesOrders(
        customerNameSearch: String?,
        fromDate: Date?,
        toDate: Date?,
        orderStatus: Int = 0,
        customerAccountNumber: String?
    ) {
        val salesOrdersResponse = networkService.call {
            it.getSalesOrders(
                customerNameSearch,
                fromDate?.let { date -> PayloadDate(date).toString() },
                toDate?.let { date -> PayloadDate(date).toString() },
                orderStatus,
                customerAccountNumber
            )
        }
        val previousOrderSummaryList = salesOrdersResponse.data ?: listOf()
        Realm.getDefaultInstance().executeTransaction { realm ->
            SalesOrder.updateFromPreviousOrderList(
                previousOrderSummaryList,
                customerAccountNumber,
                realm
            )
        }
    }

    suspend fun verifySavedOrder(salesOrder: SalesOrder): SalesOrder? {
        val verifySavedOrderResponse = networkService.callWithData { flexiApi, _ ->
            flexiApi.verifySavedOrder(VerifySavedOrderRequestData.mapSalesOrder(salesOrder))
        }
        verifySavedOrderResponse.data?.let { response ->
            val updateTime = System.currentTimeMillis()
            val customerDetails = response.customer
            val productDetails = response.products
            Realm.getDefaultInstance().executeTransaction { realm ->
                Customer.updateFromUntracked(customerDetails, updateTime, realm)
                productDetails.forEach { product ->
                    Product.updateFromUntracked(product, updateTime, realm)
                }
            }

            // The customer and products references should be updated by this point
            val updatedSalesOrder = Realm.getDefaultInstance().copyFromRealm(salesOrder)
            // Check if the customer is active
            if (!customerDetails.isActive()) {
                return null
            }
            // Check if the selected customer branch still exists
            updatedSalesOrder.selectedBranch?.branchCode?.let { selectedBranchCode ->
                val selectedCustomerBranch = customerDetails.branches.find {
                    it.branchCode == selectedBranchCode
                }
                updatedSalesOrder.setSelectedBranch(selectedCustomerBranch)
            }
            // Check if any products are inactive
            updatedSalesOrder.productOrders.toMutableList().forEach { productOrder ->
                val productOrderIsNotActive =
                    productDetails.find { it.itemCode == productOrder.product?.itemCode }
                        ?.isActive() != true
                if (productOrderIsNotActive) {
                    updatedSalesOrder.productOrders.remove(productOrder)
                }
            }
            // Check if the UOM is gone
            updatedSalesOrder.productOrders.forEach { productOrder ->
                productDetails.find { it.itemCode == productOrder.product?.itemCode }
                    ?.let { product ->
                        val selectedUom =
                            productOrder.uom?.let { selectedUom -> product.uomDetailList.find { it.description == selectedUom.description } }
                        if (selectedUom == null) {
                            productOrder.setUom(product.getDefaultUom())
                        } else {
                            productOrder.setUom(selectedUom)
                        }
                    }
                productOrder.setDeliveryDate(Date())
            }
            return updatedSalesOrder
        }
        throw RuntimeException()
    }

    suspend fun downloadSalesOrderStatement(context: Context, docNo: String): Uri? {
        val responseBody = networkService.callForResponse {
            it.downloadSalesOrderStatement(docNo)
        }
        return responseBody.body()?.byteStream()?.let {
            FileSystemHelper.savePdfFileToDownloadsFolder(context, it, docNo)
        }
    }
}