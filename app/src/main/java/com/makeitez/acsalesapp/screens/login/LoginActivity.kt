package com.makeitez.acsalesapp.screens.login

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACActivity
import com.makeitez.acsalesapp.screens.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : ACActivity.WithViewModel<LoginViewModel>(R.layout.activity_login, LoginViewModel::class.java) {

    override val requiresAuth
        get() = false

    override val handlesOwnLoadingIndicator: Boolean
        get() = true

    override fun onConfirmedCreate(savedInstanceState: Bundle?) {
        super.onConfirmedCreate(savedInstanceState)
        viewModel.init(ACService.authService)
        viewModel.state.observe(this, Observer { syncLayout(it) })
        viewModel.inProgress.observe(this, Observer { syncProgress(it) })
        viewModel.onLoggedInEvent.observe(this, Observer { event ->
            event.handleIfNotHandled {
                onLoggedIn()
            }
        })

        // We can use data binding here but it is not necessary
        loginSalesIdInputText.doOnTextChanged { salesId, _, _, _ ->
            viewModel.onSalesIdChange(salesId.toString())
        }

        loginPasswordInputText.doOnTextChanged { password, _, _, _ ->
            viewModel.onPasswordChange(password.toString())
        }

        loginButton.setOnClickListener {
            viewModel.login()
        }
    }

    private fun syncLayout(state: LoginViewModel.State) {
        loginButton.isEnabled = state.isLoginButtonEnabled
    }

    private fun syncProgress(inProgress: Boolean) {
        loginProgress.isVisible = inProgress
        loginButton.isVisible = !inProgress
        loginSalesIdInputText.isEnabled = !inProgress
        loginPasswordInputText.isEnabled = !inProgress
    }

    private fun onLoggedIn() {
        startActivity(Intent(this, HomeActivity::class.java))
    }

}