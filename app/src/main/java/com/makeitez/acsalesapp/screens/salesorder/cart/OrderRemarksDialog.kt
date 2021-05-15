package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.DatePickerFragment
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.models.SalesOrderRemarks
import com.makeitez.acsalesapp.utils.extensions.*
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.dialog_order_remarks.*
import java.util.*

class OrderRemarksDialog : ACDialogFragment.WithViewModel<OrderRemarksViewModel>(R.layout.dialog_order_remarks, OrderRemarksViewModel::class.java),
    DatePickerFragment.Listener{

    interface Listener {
        fun onSaveOrderRemarks(deliveryDate: Date, po: String, remarks: String)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()


    private val deliveryDate: Date by lazy {
        arguments?.getLong(DELIVERY_DATE_KEY)?.let { Date(it) } ?: Date()
    }

    private val po: String by lazy {
        arguments?.getString(PO_KEY) ?: ""
    }

    private val remarks: String by lazy {
        arguments?.getString(REMARKS_KEY) ?: ""
    }

    private val isReadOnly: Boolean by lazy {
        arguments?.getBoolean(IS_READ_ONLY_KEY) ?: false
    }


    fun withData(salesOrderRemarks: SalesOrderRemarks, isReadOnly: Boolean = false): OrderRemarksDialog {
        val args = arguments ?: Bundle()
        args.putLong(DELIVERY_DATE_KEY, salesOrderRemarks.deliveryDate.time)
        args.putString(PO_KEY, salesOrderRemarks.po)
        args.putString(REMARKS_KEY, salesOrderRemarks.remarks)
        args.putBoolean(IS_READ_ONLY_KEY, isReadOnly)
        arguments = args
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(deliveryDate, po, remarks)
        viewModel.state.observe(viewLifecycleOwner, Observer { syncData(it) })

        orderRemarksDeliveryDateInputText.setOnClickListener {
            DatePickerFragment().withData(viewModel.state.value?.deliveryDate, DELIVERY_DATE_REQUEST_CODE, Date())
                .listen(this).show(this)
        }

        orderRemarksPoInputText.doOnTextChanged { text, _, _, _ ->
            viewModel.setPO(text.toString())
        }

        orderRemarksInputText.doOnTextChanged { text, _, _, _ ->
            viewModel.setRemarks(text.toString())
        }

        orderRemarksCancelButton.setOnClickListener {
            dismiss()
        }

        orderRemarksSaveButton.setOnClickListener {
            viewModel.state.value?.let {
                listener?.onSaveOrderRemarks(
                    deliveryDate = it.deliveryDate,
                    po = it.po.trim(),
                    remarks = it.remarks.trim()
                )
            }
            dismiss()
        }

        orderRemarksDeliveryDateInputText.setDisabled(isReadOnly)
        orderRemarksPoInputText.setDisabled(isReadOnly)
        orderRemarksInputText.setDisabledWithScroll(isReadOnly)
        orderRemarksCancelButton.setText(if (!isReadOnly) R.string.generic_cancel else R.string.generic_ok)
        orderRemarksSaveButton.isVisible = !isReadOnly
    }

    override fun onDateSelected(requestCode: Int?, day: Int, month: Int, year: Int) {
        if(requestCode == DELIVERY_DATE_REQUEST_CODE) {
            viewModel.setDeliveryDate(day, month, year)
        }
    }

    private fun syncData(state: OrderRemarksViewModel.State) {
        with(state) {
            if(po != orderRemarksPoInputText.text.toString()) {
                orderRemarksPoInputText.setText(po)
            }
            if(remarks != orderRemarksInputText.text.toString()) {
                orderRemarksInputText.setText(remarks)
            }
            orderRemarksDeliveryDateInputText.setText(
                FormattingHelper.formatDate(
                    deliveryDate,
                    FormattingHelper.dateFormatLong
                ))
        }
    }

    companion object {
        private const val DELIVERY_DATE_KEY = "deliveryDate"
        private const val PO_KEY = "po"
        private const val REMARKS_KEY = "remarks"
        private const val IS_READ_ONLY_KEY = "isReadOnly"
        private const val DELIVERY_DATE_REQUEST_CODE = 0
    }
}