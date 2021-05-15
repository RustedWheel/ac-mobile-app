package com.makeitez.acsalesapp.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACActivity
import com.makeitez.acsalesapp.screens.home.HomeActivity
import com.makeitez.acsalesapp.screens.login.LoginActivity
import com.makeitez.acsalesapp.screens.splash.SplashViewModel.SplashDestination
import com.makeitez.acsalesapp.utils.extensions.setIndeterminateTint
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : ACActivity.WithViewModel<SplashViewModel>(R.layout.activity_splash, SplashViewModel::class.java) {

    override val requiresAuth
        get() = false

    override val handlesOwnLoadingIndicator: Boolean
        get() = true

    override fun onConfirmedCreate(savedInstanceState: Bundle?) {
        super.onConfirmedCreate(savedInstanceState)
        splashProgress.setIndeterminateTint(R.color.white)
        viewModel.init(ACService.authHelper)
        viewModel.destination.observe(this, Observer { destination ->
            navigateToDestination(destination)
        })
    }

    private fun navigateToDestination(destination: SplashDestination) {
        when (destination) {
            SplashDestination.Login -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            SplashDestination.Home -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }
}