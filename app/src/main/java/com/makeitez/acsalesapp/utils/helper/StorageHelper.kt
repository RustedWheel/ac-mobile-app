package com.makeitez.acsalesapp.utils.helper

import android.content.Context
import android.content.SharedPreferences

object StorageHelper {

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        val tag = context.packageName.replace(".", "_")
        preferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE)
    }

    fun getInt(key: String, defValue: Int = 0): Int {
        return preferences.getInt(key, defValue)
    }

    fun setInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getLong(key: String, defValue: Long = 0): Long {
        return preferences.getLong(key, defValue)
    }

    fun setLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getFloat(key: String, defValue: Float = 0f): Float {
        return preferences.getFloat(key, defValue)
    }

    fun setFloat(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun getString(key: String, defValue: String? = null): String? {
        return preferences.getString(key, defValue)
    }

    fun setString(key: String, value: String?) {
        preferences.edit().putString(key, value).apply()
    }

    fun getBool(key: String, defValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun setBool(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

}