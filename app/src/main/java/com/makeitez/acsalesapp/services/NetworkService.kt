package com.makeitez.acsalesapp.services


import com.makeitez.acsalesapp.models.ApiResponse
import com.makeitez.acsalesapp.models.BaseApiResponse
import com.makeitez.acsalesapp.services.api.BaseAPIException
import com.makeitez.acsalesapp.services.api.ACAPIException
import com.makeitez.acsalesapp.services.api.ACApi
import kotlinx.coroutines.Deferred
import retrofit2.Response

enum class ResultCode(val value: Int) {
    SUCCESS(0),
    FAILED(1),
    INACTIVE(2),
    SESSION_TIMEOUT(3),
    PENDING_ORDER(4),
    PENDING_APPROVAL(5),
    INACTIVE_CUSTOMER(6),
    INACTIVE_PRODUCT(7),
    NOT_FOUND(8),
    CONFIRM_APPROVAL(9),
    INVALID_ORDER(10),
    VERSION_UNMATCH(11),
    DUPLICATE_ORDER(12),
}

class NetworkService(private val api: ACApi) {

    private val successCodes = listOf(ResultCode.SUCCESS.value, ResultCode.PENDING_ORDER.value, ResultCode.PENDING_APPROVAL.value, ResultCode.CONFIRM_APPROVAL.value)

    suspend fun callBase(method: (ACApi) -> Deferred<BaseApiResponse>): BaseApiResponse {
        return validateBaseAPIResponse(method.invoke(api).await())
    }

    private fun validateBaseAPIResponse(response: BaseApiResponse): BaseApiResponse {
        if (response.resultCode !in successCodes) {
            throw ACAPIException(response)
        }
        return response
    }

    suspend fun <T> call(method: (ACApi) -> Deferred<ApiResponse<T>>): ApiResponse<T> {
        return validateAPIResponse(method.invoke(api).await())
    }

    suspend fun <T> callWithData(method: (ACApi, MutableMap<String, Any?>) -> Deferred<ApiResponse<T>>): ApiResponse<T> {
        return validateAPIResponse(method.invoke(api, mutableMapOf()).await())
    }

    suspend fun <T> callForResponse(method: (ACApi) -> Deferred<Response<T>>): Response<T> {
        return validateBaseResponse(method.invoke(api).await())
    }

    private fun <T> validateAPIResponse(response: ApiResponse<T>): ApiResponse<T> {
        validateBaseAPIResponse(response)
        return response
    }

    private fun <T> validateBaseResponse(response: Response<T>): Response<T> {
        if (!response.isSuccessful) {
            throw BaseAPIException()
        }
        return response
    }
}