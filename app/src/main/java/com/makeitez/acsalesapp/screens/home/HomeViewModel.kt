package com.makeitez.acsalesapp.screens.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.BuildConfig
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.AuthService
import com.makeitez.acsalesapp.services.GeneralService
import com.makeitez.acsalesapp.models.ACUser
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import com.makeitez.acsalesapp.utils.extensions.logE
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): ACViewModel(application) {

    data class State(
        val appVersion: String = "",
        val generalAnnouncementCount: Int = 0,
        val newProductAnnouncementCount: Int = 0,
        val pendingApprovalCount: Int = 0
    )

    private lateinit var generalService: GeneralService
    private lateinit var authService: AuthService

    val state = MutableLiveData<State>()
    val user: RealmObjectLiveData<ACUser> = (ACUser.getCurrentUser() ?: ACUser()).asLiveData()
    val logoutProgress = MutableLiveData<Boolean>()

    init {
        state.value = State(
            appVersion = BuildConfig.VERSION_NAME
        )

        storeOneSignalPlayerID()
    }

    private fun storeOneSignalPlayerID() {
        viewModelScope.launch {
            try {
                generalService.storeOneSignalPlayerID()
            } catch (e: Exception) {
                logE(e)
            }
        }
    }

    fun init(generalService: GeneralService, authService: AuthService) {
        this.generalService = generalService
        this.authService = authService
    }

    fun fetchNotificationCount() {
        withProgress {
            try {
                generalService.fetchHomeNotificationSummary().let {
                    state.value = state.value?.copy(
                        generalAnnouncementCount = it.generalAnnouncement,
                        newProductAnnouncementCount = it.newProductAnnouncement,
                        pendingApprovalCount = it.pendingApproval
                    )
                }
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutProgress.value = true

            try {
                authService.logout()
                app.relogin()
            } catch (e: Exception) {
                handleGenericException(e)
            }

            logoutProgress.value = false
        }
    }
}