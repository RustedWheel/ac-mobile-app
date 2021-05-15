package com.makeitez.acsalesapp.components

import android.os.Bundle
import android.view.View
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.utils.extensions.setIndeterminateTint
import kotlinx.android.synthetic.main.fragment_loading_dialog.*

class LoadingDialogFragment : ACDialogFragment(R.layout.fragment_loading_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgress.setIndeterminateTint(R.color.ac_red)
        isCancelable = false
    }

    companion object {
        val tag: String
            get() = LoadingDialogFragment::class.java.simpleName
    }
}