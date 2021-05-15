package com.makeitez.acsalesapp.screens.salesorder.saved

import android.os.Bundle
import android.view.View
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import kotlinx.android.synthetic.main.dialog_confirmation.*

class DeleteSavedOrderDialog : ACDialogFragment(R.layout.dialog_confirmation) {

    interface Listener {
        fun onDeleteSavedOrderConfirmed(savedOrderId: String)
    }

    private val savedOrderId: String? by lazy {
        arguments?.getString(SAVED_ORDER_ID_KEY)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    fun withData(savedOrderId: String): DeleteSavedOrderDialog {
        val args = arguments ?: Bundle()
        args.putString(SAVED_ORDER_ID_KEY, savedOrderId)
        arguments = args
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmationTitle.setText(R.string.saved_order_delete_saved_order_title)
        confirmationMessage.setText(R.string.saved_order_delete_saved_order_message)
        confirmationCancelButton.setOnClickListener {
            dismiss()
        }
        confirmationConfirmButton.setOnClickListener {
            savedOrderId?.let {
                listener?.onDeleteSavedOrderConfirmed(it)
            }
            dismiss()
        }
    }

    companion object {
        private const val SAVED_ORDER_ID_KEY = "savedOrderId"
    }

}