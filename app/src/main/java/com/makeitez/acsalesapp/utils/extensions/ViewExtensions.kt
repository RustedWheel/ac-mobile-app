package com.makeitez.acsalesapp.utils.extensions

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.makeitez.acsalesapp.R

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    @StringRes actionResId: Int = R.string.generic_okay
) {
    Snackbar.make(this, message, duration)
        .setAction(actionResId) {}
        .show()
}

val TextView.trimmedText: String
    get() = text.toString().trim()

var TextView.textToShow: String
    get() = text.toString()
    set(value) {
        this.isVisible = value.isNotBlank()
        this.text = value
    }

fun TextView.setNonEmptyText(textToSet: String?, defaultText: String = "-") {
    text = if(textToSet.isNotNullOrBlank()) textToSet else defaultText
}

fun EditText.clear() {
    setText("")
}

fun EditText.replaceText(replacementText: String) {
    setText(replacementText)
    setSelection(replacementText.length)
}

fun EditText.setInputFilter(filter: InputFilter) {
    filters = arrayOf(filter)
}

fun EditText.setDisabled(disabled: Boolean = true) {
    isEnabled = !disabled
}

fun EditText.setDisabledWithScroll(
    disabled: Boolean = true,
    @DrawableRes enabledBackgroundRes: Int = R.drawable.outlined_border_red,
    @DrawableRes disabledBackgroundRes: Int = R.drawable.outlined_border_disabled_red
) {
    setBackgroundResource(if (!disabled) enabledBackgroundRes else disabledBackgroundRes)
    isFocusableInTouchMode = !disabled
    isCursorVisible = !disabled
}

fun ProgressBar.setIndeterminateTint(@ColorRes colorResId: Int) {
    val colorInt: Int = ContextCompat.getColor(context, colorResId)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        indeterminateDrawable.colorFilter = BlendModeColorFilter(colorInt, BlendMode.SRC_IN)
    } else {
        indeterminateDrawable.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
    }
}