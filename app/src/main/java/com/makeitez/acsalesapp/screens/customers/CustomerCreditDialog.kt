package com.makeitez.acsalesapp.screens.customers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.dialog_customer_credit.*

class CustomerCreditDialog : ACDialogFragment.WithViewModel<CustomerCreditViewModel>(R.layout.dialog_customer_credit, CustomerCreditViewModel::class.java) {

    interface Listener {
        fun onCustomerConfirmed(customer: Customer)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    private val customerAccountNumber: String by lazy {
        arguments?.getString(CUSTOMER_ACCOUNT_NUMBER_KEY) ?: ""
    }
    private val canContinue: Boolean by lazy {
        arguments?.getBoolean(CAN_CONTINUE_KEY) ?: false
    }
    private val fetchApiDetails: Boolean by lazy {
        arguments?.getBoolean(FETCH_API_DETAILS_KEY) ?: true
    }

    // Usually I like to use a function to pass data since there can be cases where you don't want to pass data at all
    fun withData(customerAccountNumber: String, canContinue: Boolean = true, fetchApiDetails: Boolean = true): CustomerCreditDialog {
        val args = arguments ?: Bundle()
        args.putString(CUSTOMER_ACCOUNT_NUMBER_KEY, customerAccountNumber)
        args.putBoolean(CAN_CONTINUE_KEY, canContinue)
        args.putBoolean(FETCH_API_DETAILS_KEY, fetchApiDetails)
        arguments = args
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(customerAccountNumber, fetchApiDetails, ACService.customerService)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.customer.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.shouldDismiss.observe(viewLifecycleOwner, Observer { dismiss() })
        customerCreditCancelButton.isVisible = canContinue
        customerCreditCancelButton.setOnClickListener {
            dismiss()
        }
        customerCreditConfirmButton.setText(if (canContinue) R.string.generic_continue else R.string.generic_ok )
        customerCreditConfirmButton.setOnClickListener {
            viewModel.customer.value?.let {
                listener?.onCustomerConfirmed(it)
            }
            dismiss()
        }
    }

    private fun syncData(customer: Customer) {
        val creditLimit = FormattingHelper.formatPrice(customer.creditLimit, customer.currencyCode)
        val availableCredit = FormattingHelper.formatPrice(customer.availableLimit, customer.currencyCode)
        val overdue = FormattingHelper.formatPrice(customer.overdue, customer.currencyCode)
        customerCreditTitle.text = customer.companyName
        customerCreditLimitText.text = getString(R.string.customer_credit_limit, creditLimit)
        customerCreditTermText.text = getString(R.string.customer_credit_terms, customer.displayTerm)
        customerCreditAvailableCreditText.text = availableCredit
        customerCreditOverdueText.text = overdue
    }

    private fun syncProgress(inProgress: Boolean) {
        customerCreditLoadingProgress.isVisible = inProgress
        customerCreditContent.isVisible = !inProgress
        customerCreditConfirmButton.isVisible = !inProgress
    }

    companion object {
        private const val CUSTOMER_ACCOUNT_NUMBER_KEY = "customerAccountNumber"
        private const val CAN_CONTINUE_KEY = "canContinue"
        private const val FETCH_API_DETAILS_KEY = "fetchApiDetails"
    }
}