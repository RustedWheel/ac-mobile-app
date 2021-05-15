package com.makeitez.acsalesapp.services.api

import com.makeitez.acsalesapp.BuildConfig
import com.makeitez.acsalesapp.utils.helper.AuthHelper
import okhttp3.Interceptor
import okhttp3.Request

class ACInterceptor(private val authHelper: AuthHelper): Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        return chain.proceed(authorise(request))
    }

    private fun authorise(request: Request): Request {
        return request.newBuilder().apply {
            // For the login request we don't need to add the auth header again because it is already passed in
            if (request.header(AUTH_HEADER_NAME) == null) {
                addHeader(AUTH_HEADER_NAME, authHelper.authToken ?: "")
                addHeader(SESSION_HEADER_NAME, authHelper.sessionId ?: "")
            }
            addHeader(CONTENT_TYPE_HEADER_NAME, "application/json;charset=UTF-8")
            addHeader(APP_VERSION_HEADER_NAME, BuildConfig.VERSION_NAME)
        }.build()
    }

    companion object {
        private const val AUTH_HEADER_NAME = "Authorization"
        private const val SESSION_HEADER_NAME = "SessionId"
        private const val CONTENT_TYPE_HEADER_NAME = "Content-Type"
        private const val APP_VERSION_HEADER_NAME = "AppVersion"
    }

}