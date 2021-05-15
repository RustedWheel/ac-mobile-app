package com.makeitez.acsalesapp.services.api

import com.makeitez.acsalesapp.models.BaseApiResponse

open class BaseAPIException : RuntimeException()

class ACAPIException(response: BaseApiResponse) : BaseAPIException() {
    val code = response.resultCode
    override val message = response.resultMsg
}
