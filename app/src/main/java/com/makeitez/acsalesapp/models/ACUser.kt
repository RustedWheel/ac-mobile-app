package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where

open class ACUser: RealmObject() {

    @PrimaryKey
    @field:Json(name = "userName")
    var userName: String = ""; private set

    @field:Json(name = "department")
    var department: String = ""; private set

    @field:Json(name = "email")
    var email: String = ""; private set

    @field:Json(name = "isAdmin")
    var isAdmin: Boolean = false; private set

    @field:Json(name = "sessionId")
    var sessionId: String? = null; private set

    fun clearSessionId() {
        sessionId = null
    }

    companion object {
        fun getCurrentUser(realm: Realm = Realm.getDefaultInstance()) =
            realm.where<ACUser>().findFirst()

        fun isAdmin(realm: Realm = Realm.getDefaultInstance()): Boolean =
            getCurrentUser(realm)?.isAdmin ?: false
    }
}
