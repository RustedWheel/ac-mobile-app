package com.makeitez.acsalesapp.utils.extensions

import android.util.Log
import com.makeitez.acsalesapp.BuildConfig


const val defaultLogPriority = Log.ASSERT

fun Any.logA(message: Any, optionalTag: String = "") {
    logPrintln(defaultLogPriority, message, optionalTag)
}

fun Any.logV(message: Any, optionalTag: String = "") {
    logPrintln(Log.VERBOSE, message, optionalTag)
}

fun Any.logD(message: Any, optionalTag: String = "") {
    logPrintln(Log.DEBUG, message, optionalTag)
}

fun Any.logI(message: Any, optionalTag: String = "") {
    logPrintln(Log.INFO, message, optionalTag)
}

fun Any.logW(message: Any, optionalTag: String = "") {
    logPrintln(Log.WARN, message, optionalTag)
}

fun Any.logE(message: Any, optionalTag: String = "") {
    logPrintln(Log.ERROR, message, optionalTag)
}

fun Any.logWtf(message: Any, optionalTag: String = "") {
    logPrintln(Log.ASSERT, message, optionalTag)
}

fun Any.logPrintln(priority: Int, message: Any, optionalTag: String) {
    if (BuildConfig.DEBUG) {
        val tag =
            "AC_${this::class.java.simpleName}${if (optionalTag.isNotEmpty()) " [$optionalTag]" else ""}";

        Log.println(priority, tag,
            if (message is Exception) {
                Log.getStackTraceString(message)
            } else {
                message.toString()
            }
        )
    }
}