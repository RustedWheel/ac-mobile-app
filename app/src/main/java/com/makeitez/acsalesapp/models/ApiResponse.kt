package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json

open class BaseApiResponse {
    @field:Json(name = "resultCode") val resultCode: Int? = null
    @field:Json(name = "resultMsg") val resultMsg: String? = null
}
class ApiResponse<T>: BaseApiResponse() {
    @field:Json(name = "data") val data: T? = null
}