package com.makeitez.acsalesapp.screens.salesorder.previousorder

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.DatePickerFragment
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.show
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.dialog_search_sales_order.*
import java.util.*

class SearchSalesOrderDialog : ACDialogFragment.WithViewModel<SearchSalesOrderViewModel>(R.layout.dialog_search_sales_order, SearchSalesOrderViewModel::class.java),
    DatePickerFragment.Listener {

    interface Listener {
        fun onSearchSalesOrder(customerName: String, fromDate: Date?, toDate: Date?)
        fun onResetSalesOrderSearch()
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    fun withData(searchTerm: String, fromDate: Date?, toDate: Date?): SearchSalesOrderDialog {
        val args = arguments ?: Bundle()
        args.putString(SEARCH_TERM_KEY, searchTerm)
        fromDate?.let { args.putLong(FROM_DATE_KEY, it.time) }
        toDate?.let { args.putLong(TO_DATE_KEY, it.time) }
        this.arguments = args
        return this
    }

    override fun getDialogTheme() = R.style.AC_Dialog_ActionSheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setup(
            arguments?.getString(SEARCH_TERM_KEY, "") ?: "",
            arguments?.getLong(FROM_DATE_KEY)?.let { Date(it) },
            arguments?.getLong(TO_DATE_KEY)?.let { Date(it) })

        viewModel.state.observe(viewLifecycleOwner, Observer { syncData(it) })

        searchSalesOrderCustomerInputText.doOnTextChanged { text, _, _, _ ->
            viewModel.setCustomerSearch(text.toString())
        }

        searchSalesOrderFromDate.setOnClickListener {
            DatePickerFragment().withData(viewModel.state.value?.fromDate, FROM_DATE_REQUEST_CODE).listen(this).show(this)
        }

        searchSalesOrderToDate.setOnClickListener {
            DatePickerFragment().withData(viewModel.state.value?.toDate, TO_DATE_REQUEST_CODE).listen(this).show(this)
        }

        searchSalesOrderConfirmButton.setOnClickListener {
            viewModel.state.value?.let {
                listener?.onSearchSalesOrder(it.customer, it.fromDate, it.toDate)
            }
            dismiss()
        }

        searchSalesOrderResetButton.setOnClickListener {
            listener?.onResetSalesOrderSearch()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onDateSelected(requestCode: Int?, day: Int, month: Int, year: Int) {
        when(requestCode) {
            FROM_DATE_REQUEST_CODE -> {
                viewModel.setFromDate(day, month, year)
            }
            TO_DATE_REQUEST_CODE -> {
                viewModel.setToDate(day, month, year)
            }
        }
    }

    private fun syncData(state: SearchSalesOrderViewModel.State) {
        if (state.customer != searchSalesOrderCustomerInputText.text.toString()) {
            searchSalesOrderCustomerInputText.setText(state.customer)
        }
        searchSalesOrderFromDate.setText(state.fromDate?.let {
            FormattingHelper.formatDate(
                it,
                FormattingHelper.dateFormatLong
            )
        } ?: "")
        searchSalesOrderToDate.setText(state.toDate?.let {
            FormattingHelper.formatDate(
                it,
                FormattingHelper.dateFormatLong
            )
        } ?: "")
    }

    companion object {
        private const val SEARCH_TERM_KEY = "searchTerm"
        private const val FROM_DATE_KEY = "fromDate"
        private const val TO_DATE_KEY = "toDate"
        private const val FROM_DATE_REQUEST_CODE = 0
        private const val TO_DATE_REQUEST_CODE = 1
    }

}