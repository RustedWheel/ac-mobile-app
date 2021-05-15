package com.makeitez.acsalesapp.utils.helper

import android.content.Context

class AuthHelper(context: Context) {

    private val encryptionHelper: EncryptionHelper = EncryptionHelper(context)

    var authToken: String?
        get() = encryptionHelper.decrypt(StorageHelper.getString(AUTH_TOKEN), AUTH_TOKEN_ALIAS)
        set(value) {
            StorageHelper.setString(AUTH_TOKEN, encryptionHelper.encrypt(value, AUTH_TOKEN_ALIAS))
        }

    var sessionId: String?
        get() = encryptionHelper.decrypt(StorageHelper.getString(SESSION_ID), SESSION_ID_ALIAS)
        set(value) {
            StorageHelper.setString(SESSION_ID, encryptionHelper.encrypt(value, SESSION_ID_ALIAS))
        }

    fun isLoggedIn() = authToken != null && sessionId != null

    companion object {
        private const val AUTH_TOKEN = "ACAuthToken"
        private const val AUTH_TOKEN_ALIAS = "auth"
        private const val SESSION_ID = "ACSessionId"
        private const val SESSION_ID_ALIAS = "session"
    }
}