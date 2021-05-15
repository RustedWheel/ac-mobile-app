package com.makeitez.acsalesapp.screens.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.AuthService
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.extensions.logI

class LoginViewModel(application: Application) : ACViewModel(application) {

    data class State(
        val salesId: String = "",
        val password: String = "",
        val isLoginButtonEnabled: Boolean = false
    )

    private lateinit var authService: AuthService

    val state = MutableLiveData<State>()
    val onLoggedInEvent = MutableLiveData<Event<Unit>>()

    init {
        state.value = State()
    }

    fun init(authService: AuthService) {
        this.authService = authService
    }

    fun onSalesIdChange(salesId: String) {
        state.value = state.value?.copy(
            salesId = salesId
        )
        validateLoginFields()
    }

    fun onPasswordChange(password: String) {
        state.value = state.value?.copy(
            password = password
        )
        validateLoginFields()
    }

    fun login() {
        if (inProgress.value == true) return
        val state = state.value?: return
        logI("Login with [${state.salesId}] and [${state.password}]")
        withProgress {
            try {
                authService.login(state.salesId, state.password)
                onLoggedInEvent.value = Event(Unit)
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    private fun validateLoginFields() {
        val currentState = state.value ?: return
        state.value = currentState.copy(
            isLoginButtonEnabled = !(inProgress.value ?: false) && currentState.salesId.isNotBlank() && currentState.password.isNotBlank()
        )
    }
}