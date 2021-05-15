package com.makeitez.acsalesapp.utils.helper

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

class StringProvider(private val context: Context) {

    fun getStringList(@ArrayRes resId: Int): List<String> {
        return context.resources.getStringArray(resId).toList()
    }

    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}