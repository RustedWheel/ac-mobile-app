package com.makeitez.acsalesapp.screens.home

import android.os.Bundle
import android.view.View
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningActivity
import kotlinx.android.synthetic.main.dialog_confirmation.*

class ConfirmLogoutDialog: ACDialogFragment(R.layout.dialog_confirmation) {

    interface Listener {
        fun onLogoutConfirmed()
    }

    private val listener: Listener?
        get() = getListeningActivity<Listener>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmationTitle.setText(R.string.home_confirm_logout_title)

        confirmationMessage.setText(R.string.home_confirm_logout_message)

        confirmationCancelButton.setOnClickListener {
            dismiss()
        }

        confirmationConfirmButton.setOnClickListener {
            listener?.onLogoutConfirmed()
            dismiss()
        }
    }
}