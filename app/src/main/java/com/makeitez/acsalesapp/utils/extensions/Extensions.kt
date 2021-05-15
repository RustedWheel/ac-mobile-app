package com.makeitez.acsalesapp.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.makeitez.acsalesapp.utils.RealmObjectLiveData
import com.makeitez.acsalesapp.utils.RealmResultsLiveData
import com.makeitez.acsalesapp.utils.moshiadapter.ACDateAdapter
import com.makeitez.acsalesapp.utils.moshiadapter.RealmListJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import org.apache.commons.lang3.time.DateUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

typealias Callback = () -> Unit

fun postDelayed(delayMs: Long = 0, callback: Callback) {
    Handler(Looper.getMainLooper()).postDelayed({ callback.invoke() }, delayMs)
}

fun post(callback: Callback) {
    Handler(Looper.getMainLooper()).post(callback)
}

fun getDeviceScreenWidthPx() = Resources.getSystem().displayMetrics.widthPixels

fun getDeviceScreenHeightPx() = Resources.getSystem().displayMetrics.heightPixels

fun Calendar.setDate(year: Int, monthOfYear: Int, dayOfMonth: Int): Calendar {
    this.set(year, monthOfYear, dayOfMonth)
    return this
}

fun getDateFromPicker(day: Int, month: Int, year: Int): Date = Calendar.getInstance().setDate(year, month, day).time

fun Date.toCalendar(): Calendar {
    return Calendar.getInstance().also { calendar -> calendar.time = this }
}

fun getDisplayDate(day: Int, month: Int, year: Int) = "$day/$month/$year"

fun getDisplayDate(c: Calendar = Calendar.getInstance()): String {
    val day = c.get(Calendar.DAY_OF_MONTH)
    val month = c.get(Calendar.MONTH)
    val year = c.get(Calendar.YEAR)

    return getDisplayDate(day, month + 1, year)
}

fun buildACMoshi() : Moshi = Moshi.Builder()
    .add(RealmListJsonAdapterFactory())
    .add(ACDateAdapter())
    .build()

fun Context.resolveDimenResIdPx(@DimenRes dimenResId: Int): Int =
    resources.getDimensionPixelSize(dimenResId)

fun Context.resolveDimenResIdDp(@DimenRes dimenResId: Int): Int =
    resources.getDimension(dimenResId).toInt()

val Int.dpToPx: Int
    get() = ((this * Resources.getSystem().displayMetrics.density) + 0.5).toInt()

val Int.pxToDp: Int
    get() = ((this / Resources.getSystem().displayMetrics.density) + 0.5).toInt()

fun <T> Any.cast(): T {
    @Suppress("UNCHECKED_CAST")
    return this as T
}

fun <T> MutableCollection<T>.clearAndAddAll(newCollection: Collection<T>){
    clear()
    addAll(newCollection)
}

fun String.containsIgnoreCase(searchStr: String): Boolean {
    return toLowerCase().contains(searchStr.toLowerCase())
}

fun String?.isNotNullOrBlank(): Boolean {
    return !isNullOrBlank()
}

fun String?.isInt(): Boolean {
    return this?.toIntOrNull() != null
}

fun String?.isDouble(): Boolean {
    return this?.toDoubleOrNull() != null
}

fun String?.isPercentage(): Boolean {
    return this != null && this.isDouble() && this.toDouble() in 0.0 .. 100.0
}

fun Double.toStringRemoveTrailingZeroes(maxDps: Int): String =
    DecimalFormat("0.${"#".repeat(maxDps)}").format(this)

fun Double.toNdpString(n: Int, commaSeparateThousands: Boolean = false): String =
    DecimalFormat("${if(commaSeparateThousands) "#,#" else "" }#0.${"0".repeat(n)}").format(this)

fun BigDecimal.toNdpString(n: Int, commaSeparateThousands: Boolean = false): String =
    toDouble().toNdpString(n)

fun Intent.makeRootActivity(): Intent {
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    return this
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.setToolbarTitle(title: String) {
    (this as? AppCompatActivity)?.supportActionBar?.title = title
}

fun Fragment.setToolbarTitle(title: String) {
    requireActivity().setToolbarTitle(title)
}

fun FragmentActivity.findFragmentByTag(tag: String): Fragment? {
    return supportFragmentManager.findFragmentByTag(tag)
}

fun Fragment.findFragmentByTag(tag: String, useChildFragmentManager: Boolean = false): Fragment? {
    val manager = if (useChildFragmentManager) childFragmentManager else fragmentManager
    return manager?.findFragmentByTag(tag)
}

fun Fragment.hideKeyboard(view: View) {
    requireActivity().hideKeyboard(view)
}

fun InputFilter.buildResultingString(
    source: CharSequence,
    sStart: Int,
    sEnd: Int,
    destination: Spanned,
    dStart: Int,
    dEnd: Int
) : String {
    val prefix = destination.subSequence(0, dStart)
    val insertedText = source.subSequence(sStart, sEnd)
    val suffix = destination.subSequence(dEnd, destination.length)

    return TextUtils.concat(prefix, insertedText, suffix).toString()
}

fun <T: RealmModel> RealmResults<T>.asLiveData() = RealmResultsLiveData<T>(this)

fun <T: RealmObject> T.asLiveData() = RealmObjectLiveData<T>(this)

fun Date.startOfDay(): Date {
    return DateUtils.truncate(this, java.util.Calendar.DATE)
}

fun Date.endOfDay(): Date {
    return DateUtils.addSeconds(DateUtils.truncate(DateUtils.addDays(this, 1), java.util.Calendar.DATE), -1)
}