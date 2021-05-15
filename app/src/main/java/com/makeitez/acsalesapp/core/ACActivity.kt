package com.makeitez.acsalesapp.core

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.makeitez.acsalesapp.BuildConfig
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.screens.login.LoginActivity
import com.makeitez.acsalesapp.utils.extensions.makeRootActivity
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import com.makeitez.acsalesapp.utils.extensions.showSnackbar


abstract class ACActivity(@LayoutRes private val layoutResId: Int) : AppCompatActivity() {

    abstract class WithViewModel<T : ACViewModel>(layoutResId: Int, private val modelClass: Class<T>) : ACActivity(layoutResId) {

        protected val viewModel: T by lazy {
            ViewModelProvider(this).get(modelClass)
        }

        protected open val handlesOwnLoadingIndicator
            get() = false

        override fun onConfirmedCreate(savedInstanceState: Bundle?) {
            super.onConfirmedCreate(savedInstanceState)
            viewModel.message.observe(this, Observer { showErrorMessage(it) })
            if(!handlesOwnLoadingIndicator) {
                viewModel.inProgress.observe(this, Observer { showProgressDialog(it) })
            }
        }

        protected open fun showErrorMessage(alertMessage: ErrorMessage?) {
            alertMessage?.let {
                findViewById<View>(android.R.id.content)?.showSnackbar(it.message)
                viewModel.clearMessage()
            }
        }
    }

    protected open val requiresAuth
        get() = true

    protected open fun onConfirmedCreate(savedInstanceState: Bundle?) {

    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        if (BuildConfig.SECURE_UI_ENABLED) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onConfirmedCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (requiresAuth && !ACService.authHelper.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java).makeRootActivity())
        }
    }
}