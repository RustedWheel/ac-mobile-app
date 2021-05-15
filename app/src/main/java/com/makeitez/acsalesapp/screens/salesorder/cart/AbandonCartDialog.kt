package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import kotlinx.android.synthetic.main.dialog_confirmation.*

class AbandonSalesOrderDialog: ACDialogFragment(R.layout.dialog_confirmation) {

    interface Listener {
        fun onSaveSalesOrder()
        fun onDeleteSalesOrder()
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmationTitle.setText(R.string.cart_leaving_title)
        confirmationMessage.setText(R.string.cart_leaving_message)

        confirmationCancelButton.setOnClickListener {
            dismiss()
        }

        confirmationDiscardButton.isVisible = true
        confirmationDiscardButton.setText(R.string.generic_discard)
        confirmationDiscardButton.setOnClickListener {
            listener?.onDeleteSalesOrder()
            dismiss()
        }

        confirmationConfirmButton.setText(R.string.generic_save)
        confirmationConfirmButton.setOnClickListener {
            listener?.onSaveSalesOrder()
            dismiss()
        }
    }
}