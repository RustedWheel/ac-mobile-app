package com.makeitez.acsalesapp.components

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ErrorMessage
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.utils.extensions.dpToPx
import com.makeitez.acsalesapp.utils.extensions.getDeviceScreenWidthPx
import com.makeitez.acsalesapp.utils.extensions.resolveDimenResIdDp

abstract class ACDialogFragment(@LayoutRes private val layoutResId: Int) : DialogFragment() {

    abstract class WithViewModel<T : ACViewModel>(layoutResId: Int, private val modelClass: Class<T>) : ACDialogFragment(layoutResId) {

        protected val viewModel: T by lazy {
            ViewModelProvider(this).get(modelClass)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel.message.observe(viewLifecycleOwner, Observer { showErrorMessage(it) })
        }

        protected open fun showErrorMessage(alertMessage: ErrorMessage?) {
            alertMessage?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    protected open fun getDialogMarginX(): Int = R.dimen.app_margin_1x
    protected open fun getDialogTheme(): Int = R.style.AC_Dialog

    private val contentView: RelativeLayout by lazy {
        RelativeLayout(activity).apply {
            layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, getDialogTheme())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AC_Theme)
        inflater.cloneInContext(contextThemeWrapper).inflate(layoutResId, contentView)
        return contentView
    }

    override fun onStart() {
        super.onStart()
        setDialogLayout()
    }

    open fun setDialogLayout() {
        context?.let {
            val width = getDeviceScreenWidthPx() - 2 * it.resolveDimenResIdDp(getDialogMarginX()).dpToPx
            dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    fun setBackground(backgroundDrawable: Drawable?) {
        contentView.background = backgroundDrawable
    }
}