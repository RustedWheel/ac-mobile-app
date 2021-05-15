package com.makeitez.acsalesapp.utils

import android.view.View
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.utils.extensions.setDisabledWithScroll

@BindingAdapter("app:visible")
fun show(view: View, show: Boolean) {
    view.isVisible = show
}

@BindingAdapter("visible")
fun show(view: View, showLiveData: LiveData<Boolean>) {
    showLiveData.value?.let { show ->
        view.isVisible = show
    }
}

@BindingAdapter("app:activated")
fun activate(view: View, activate: Boolean) {
    view.isActivated = activate
}

@BindingAdapter(
    value = ["disableWithScroll", "enabledBackgroundSrc", "disabledBackgroundSrc"],
    requireAll = false
)
fun enableWithScroll(
    editText: EditText,
    disabledLiveData: LiveData<Boolean>,
    @DrawableRes enabledBackgroundSrc: Int = R.drawable.outlined_border_red,
    @DrawableRes disabledBackgroundSrc: Int = R.drawable.outlined_border_disabled_red
) {
    disabledLiveData.value?.let { disabled ->
        editText.setDisabledWithScroll(disabled)
    }
}