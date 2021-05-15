package com.makeitez.acsalesapp.utils.extensions

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.makeitez.acsalesapp.components.LoadingDialogFragment


fun FragmentActivity.showProgressDialog(show: Boolean) {
    if(show) {
        if(findFragmentByTag(LoadingDialogFragment.tag) == null) {
            LoadingDialogFragment().show(this, LoadingDialogFragment.tag)
        }
    } else {
        dismissDialog(LoadingDialogFragment.tag)
    }
}

fun Fragment.showProgressDialog(show: Boolean) {
    if(show) {
        if(findFragmentByTag(LoadingDialogFragment.tag, useChildFragmentManager = true) == null) {
            LoadingDialogFragment().show(this, LoadingDialogFragment.tag, useChildFragmentManager = true)
        }
    } else {
        dismissDialog(LoadingDialogFragment.tag, useChildFragmentManager = true)
    }
}

fun FragmentActivity.dismissDialog(dialogTag: String) {
    findFragmentByTag(dialogTag)?.let { dialog ->
        (dialog as DialogFragment).dismiss()
    }
}

fun Fragment.dismissDialog(dialogTag: String, useChildFragmentManager: Boolean = false) {
    findFragmentByTag(dialogTag, useChildFragmentManager = useChildFragmentManager)?.let { dialog ->
        (dialog as DialogFragment).dismiss()
    }
}

fun DialogFragment.show(activity: FragmentActivity?, dialogTag: String = javaClass.simpleName) {
    activity?.supportFragmentManager?.let {
        show(it, dialogTag)
        it.executePendingTransactions()
    }
}

fun DialogFragment.show(fragment: Fragment?, dialogTag: String = javaClass.simpleName, useChildFragmentManager: Boolean = false) {
    val manager = if(useChildFragmentManager) fragment?.childFragmentManager else fragment?.fragmentManager
    manager?.let {
        show(it, dialogTag)
        it.executePendingTransactions()
    }
}

fun DialogFragment.listen(targetFragment: Fragment): DialogFragment {
    setTargetFragment(targetFragment, 888)
    return this
}

inline fun <reified T> DialogFragment.getListeningActivity(): T? {
    activity?.let {
        return it as? T
    }
    return null
}

inline fun <reified T> DialogFragment.getListeningFragment(): T? {
    targetFragment?.let {
        return it as? T
    }
    return null
}
