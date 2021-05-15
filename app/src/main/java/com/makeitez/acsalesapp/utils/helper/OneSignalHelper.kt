package com.makeitez.acsalesapp.utils.helper

import com.makeitez.acsalesapp.BuildConfig
import com.makeitez.acsalesapp.models.ACUser
import com.onesignal.OneSignal

object OneSignalHelper {
    private const val USER_TYPE_TAG = "user_type"

    private const val ADMIN_USER = "admin"
    private const val NORMAL_USER = "normal"

    fun setUserTag(user: ACUser?) {
        if(user != null){
            val userType = if (user.isAdmin) ADMIN_USER else NORMAL_USER
            val userTag = "$userType-${BuildConfig.FLAVOR}"
            OneSignal.sendTag(USER_TYPE_TAG, userTag)
        } else {
            clearUserTag()
        }
    }

    fun clearUserTag() {
        OneSignal.deleteTag(USER_TYPE_TAG)
    }

    fun getPlayerId(): String? = OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId
}