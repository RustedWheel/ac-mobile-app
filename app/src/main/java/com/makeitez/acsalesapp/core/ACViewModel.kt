package com.makeitez.acsalesapp.core

import android.app.Application
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.ACApp
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.services.ResultCode
import com.makeitez.acsalesapp.services.api.ACAPIException
import com.makeitez.acsalesapp.utils.extensions.logE
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

open class ACViewModel(application: Application) : AndroidViewModel(application) {

    val inProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    val message: MutableLiveData<ErrorMessage> = MutableLiveData()

    protected val app by lazy { application as ACApp }

    protected fun setMessage(errorMessage: ErrorMessage) {
        message.value = errorMessage
    }

    open fun clearMessage() {
        message.value = null
    }

    protected fun setProgress(progress: Boolean) {
        inProgress.value = progress
    }

    protected fun withProgress(func: suspend () -> Unit) {
        viewModelScope.launch {
            setProgress(true)

            func.invoke()

            setProgress(false)
        }
    }

    protected fun handleGenericException(e: Exception) {
        logE(e)

        when (e) {
            is ACAPIException -> {
                if (e.code == ResultCode.SESSION_TIMEOUT.value) {
                    app.relogin()
                }
                setMessage(ErrorMessage(e.message.toString()))
            }
            is IOException -> {
                setMessage(ErrorMessage(getString(R.string.generic_error_no_internet_connection)))
            }
            is HttpException -> {
                setMessage(ErrorMessage(getString(R.string.generic_error_something_went_wrong_connecting_server)))
            }
            else -> {
                setMessage(ErrorMessage(getString(R.string.generic_error_something_went_wrong)))
            }
        }
    }

    protected fun getStringList(@ArrayRes resId: Int): List<String> {
        return app.stringProvider.getStringList(resId)
    }

    protected fun getString(@StringRes resId: Int): String {
        return app.stringProvider.getString(resId)
    }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return app.stringProvider.getString(resId, *formatArgs)
    }
}