package com.makeitez.acsalesapp

import android.app.Application
import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceId
import com.makeitez.acsalesapp.screens.login.LoginActivity
import com.makeitez.acsalesapp.utils.extensions.makeRootActivity
import com.makeitez.acsalesapp.utils.helper.StorageHelper
import com.makeitez.acsalesapp.utils.helper.StringProvider
import com.onesignal.OneSignal
import io.realm.Realm
import io.realm.RealmConfiguration
import java.security.MessageDigest

interface ACApp {
    val stringProvider: StringProvider
    fun relogin()
}

class ACApplication : Application(), ACApp {

    override val stringProvider: StringProvider by lazy {
        StringProvider(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Servies
        ACService.init(this)
        // Storage
        initStorage()
        // Realm database
        initRealm()
        // Push notification
        initOneSignal()
    }

    override fun relogin() {
        ACService.relogin()
        startActivity(Intent(this, LoginActivity::class.java).makeRootActivity())
    }

    private fun initStorage() {
        StorageHelper.init(this)
    }

    private fun initRealm() {
        val digest = MessageDigest.getInstance("SHA512")
        digest.update(FirebaseInstanceId.getInstance().id.toByteArray())

        Realm.init(this)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("acsalesdb")
                .schemaVersion(1)
                .encryptionKey(digest.digest())
                .build()
        )
    }

    private fun initOneSignal() {
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}