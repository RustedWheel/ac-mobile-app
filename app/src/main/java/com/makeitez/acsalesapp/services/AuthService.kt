package com.makeitez.acsalesapp.services

import android.util.Base64
import com.makeitez.acsalesapp.models.ApiResponse
import com.makeitez.acsalesapp.models.ACUser
import com.makeitez.acsalesapp.utils.helper.AuthHelper
import com.makeitez.acsalesapp.utils.helper.OneSignalHelper
import com.makeitez.acsalesapp.utils.helper.StorageHelper
import io.realm.Realm

class AuthService(
    private val authHelper: AuthHelper,
    private val networkService: NetworkService
) {

    suspend fun login(salesId: String, password: String) {
        val token = "Basic " + Base64.encodeToString("$salesId:$password".toByteArray(), Base64.NO_WRAP)
        val loginResponse: ApiResponse<ACUser> = networkService.call {
            it.login(token)
        }
        loginResponse.data?.let { user ->
            authHelper.authToken = token
            authHelper.sessionId = user.sessionId

            // Probably shouldn't store the session ID in the database. Should encrypt it and store
            // it separately
            user.clearSessionId()

            Realm.getDefaultInstance().executeTransaction { realm ->
                realm.copyToRealmOrUpdate(user)
            }

            OneSignalHelper.setUserTag(user)
        }
    }

    suspend fun logout() {
        networkService.callBase {
            it.logout()
        }
        // Call the logout API and clear all the user data
        clearUserData()
        OneSignalHelper.clearUserTag()
    }

    fun clearUserData() {
        StorageHelper.clear()
        Realm.getDefaultInstance().executeTransaction { realm ->
            realm.deleteAll()
        }
    }
}