package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import kotlinx.android.synthetic.main.dialog_confirmation.*

class ConfirmOrderDeletionDialog: ACDialogFragment(R.layout.dialog_confirmation) {

    interface Listener {
        fun onConfirmOrderDeletion(productOrderPos: Int)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()


    private val message: String by lazy {
        arguments?.getString(MESSAGE_KEY) ?: ""
    }

    private val productOrderPos: Int by lazy {
        arguments?.getInt(PRODUCT_ORDER_POS_KEY) ?: 0
    }


    fun withData(data: CreateSalesOrderViewModel.ConfirmOrderDeletionData): ConfirmOrderDeletionDialog {
        val args = arguments ?: Bundle()
        args.putString(MESSAGE_KEY, data.message)
        args.putInt(PRODUCT_ORDER_POS_KEY, data.productOrderPos)
        arguments = args
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmationTitle.setText(R.string.cart_item_delete_confirmation_title)

        confirmationMessage.text = message

        confirmationCancelButton.setOnClickListener {
            dismiss()
        }

        confirmationConfirmButton.setOnClickListener {
            listener?.onConfirmOrderDeletion(productOrderPos)
            dismiss()
        }
    }

    companion object {
        private const val MESSAGE_KEY = "message"
        private const val PRODUCT_ORDER_POS_KEY = "productOrderPos"
    }
}