package com.makeitez.acsalesapp.screens.products.productorder

import android.os.Bundle
import android.view.View
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import kotlinx.android.synthetic.main.dialog_confirmation.*


class AbandonProductOrderDialog : ACDialogFragment(R.layout.dialog_confirmation) {

    interface Listener {
        fun onAbandonProductOrder()
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    private val isEditingProductOrder: Boolean by lazy {
        arguments?.getBoolean(IS_EDITING_PRODUCT_ORDER_KEY) ?: false
    }

    fun withData(isEditingProductOrder: Boolean): AbandonProductOrderDialog {
        val args = arguments ?: Bundle()
        args.putBoolean(IS_EDITING_PRODUCT_ORDER_KEY, isEditingProductOrder)
        arguments = args
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmationTitle.text = getString(if (isEditingProductOrder) R.string.product_order_discard_edits_title else R.string.product_order_discard_creation_title)
        confirmationMessage.text = getString(if (isEditingProductOrder) R.string.product_order_discard_edits_message else R.string.product_order_discard_creation_message)

        confirmationCancelButton.setOnClickListener {
            dismiss()
        }

        confirmationConfirmButton.setText(R.string.generic_confirm)
        confirmationConfirmButton.setOnClickListener {
            listener?.onAbandonProductOrder()
            dismiss()
        }
    }

    companion object {
        private const val IS_EDITING_PRODUCT_ORDER_KEY = "isEditingProductOrder"
    }
}