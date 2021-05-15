package com.makeitez.acsalesapp.screens.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.models.ACUser
import com.makeitez.acsalesapp.utils.helper.AuthHelper
import com.makeitez.acsalesapp.utils.helper.OneSignalHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : ACViewModel(application) {

    enum class SplashDestination {
        Login,
        Home
    }

    val destination = MutableLiveData<SplashDestination>()

    fun init(authHelper: AuthHelper) {
        viewModelScope.launch {
            val currentUser = ACUser.getCurrentUser()

            OneSignalHelper.setUserTag(currentUser)

            delay(300L)
            destination.value = when {
                authHelper.isLoggedIn() && currentUser != null -> SplashDestination.Home
                else -> SplashDestination.Login
            }
        }
    }
}