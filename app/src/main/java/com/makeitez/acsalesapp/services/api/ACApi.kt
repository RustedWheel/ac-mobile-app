package com.makeitez.acsalesapp.services.api

import com.makeitez.acsalesapp.models.*
import com.makeitez.acsalesapp.models.api.*
import com.makeitez.acsalesapp.models.api.request.CreateAnnouncementRequestData
import com.makeitez.acsalesapp.models.api.request.CreateSalesOrderRequestData
import com.makeitez.acsalesapp.models.api.request.VerifySavedOrderRequestData
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ACApi {

    @POST("api/authenticate")
    fun login(@Header("Authorization") authorization: String): Deferred<ApiResponse<ACUser>>

    @POST("api/logout")
    fun logout(): Deferred<BaseApiResponse>

    @POST("api/user/{playerId}")
    fun updatePlayerID(@Path(value = "playerId", encoded = true) playerID : String): Deferred<BaseApiResponse>

    @GET("api/notification/counter")
    fun getHomeNotificationSummary(): Deferred<ApiResponse<HomeNotificationSummary>>

    @GET("api/debtor")
    fun getCustomers(): Deferred<ApiResponse<List<CustomerListResponseData>>>

    @GET("api/debtor/{accNo}")
    fun getCustomerDetails(@Path(value = "accNo", encoded = true) accountNo : String): Deferred<ApiResponse<Customer>>

    @GET("api/debtor/download/{accNo}")
    fun downloadCustomerMonthlyStatement(@Path(value = "accNo", encoded = true) accountNo : String): Deferred<Response<ResponseBody>>

    @GET("api/debtor/aging")
    fun getAgingDetails(@Query("accNo") accountNo: String): Deferred<ApiResponse<CustomerAgingSummary>>

    @GET("api/payment")
    fun getPaymentList(@Query("accNo") accountNo: String): Deferred<ApiResponse<List<AccountsReceivablePayment>>>

    @GET("api/payment/download/{accNo}/{docNo}")
    fun downloadPaymentDetailStatement(@Path(value = "accNo", encoded = true) accountNo : String,
                                       @Path(value = "docNo", encoded = true) docNo : String): Deferred<Response<ResponseBody>>

    @GET("api/product/type")
    fun getProductCategories(): Deferred<ApiResponse<List<ProductCategory>>>

    @GET("api/product/list")
    fun getProducts(
        @Query("accNo") accNo: String?,
        @Query("categoryType") categoryType: String): Deferred<ApiResponse<List<ProductListResponseData>>>

    @GET("api/product/detail")
    fun getProductDetails(
        @Query("accNo") accNo: String?,
        @Query("itemCode") itemCode: String): Deferred<ApiResponse<Product>>

    @GET("api/product/detail/download/{itemCode}")
    fun downloadProductDetailsPdf(@Path(value = "itemCode", encoded = true) itemCode: String): Deferred<Response<ResponseBody>>

    @GET("api/product/history/list")
    fun getProductHistoryList(@Query("itemCode") itemCode: String): Deferred<ApiResponse<List<ProductHistory>>>

    @GET("api/product/history/detail")
    fun getProductHistoryDetails(@Query("docNo") docNo: String, @Query("itemCode") itemCode: String): Deferred<ApiResponse<ProductHistory>>

    @GET("api/querySalesOrder")
    fun getSalesOrders( @Query("customerName") customerName: String?,
                        @Query("fromDate") fromDate: String?,
                        @Query("toDate") toDate: String?,
                        @Query("orderStatus") orderStatus: Int,
                        @Query("accNo") customerAccountNumber: String?): Deferred<ApiResponse<List<PreviousOrderSummaryResponseData>>>

    @POST("api/salesOrder")
    fun createSalesOrder(@Body payload: CreateSalesOrderRequestData): Deferred<ApiResponse<SalesOrderResult>>

    @GET("api/salesOrder/detail")
    fun getSalesOrder(
        @Query("docNo") docNo: String,
        @Query("orderStatus") orderStatus: Int
    ): Deferred<ApiResponse<SalesOrderResponseData>>

    @POST("api/salesOrder/verify")
    fun verifySavedOrder(@Body payload: VerifySavedOrderRequestData): Deferred<ApiResponse<VerifySalesOrderResponseData>>

    @GET("api/salesOrder/download/{docNo}")
    fun downloadSalesOrderStatement(@Path(value = "docNo", encoded = true) docNo : String): Deferred<Response<ResponseBody>>

    @GET("api/announcement")
    fun getAnnouncementList(): Deferred<ApiResponse<AnnouncementListResponseData>>

    @POST("api/announcement")
    fun createAnnouncement(@Body payload: CreateAnnouncementRequestData): Deferred<ApiResponse<AnnouncementListResponseData>>

    @DELETE("api/announcement/{announcementId}")
    fun deleteAnnouncement(@Path(value = "announcementId", encoded = true) announcementId: String): Deferred<BaseApiResponse>
}