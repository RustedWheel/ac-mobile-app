package com.makeitez.acsalesapp.screens.customers.aging

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.CustomerAgingSummary
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.component_customer_id_header.*
import kotlinx.android.synthetic.main.component_generic_try_again.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_aging_summary.*

class AgingSummaryFragment: ACFragment.WithViewModel<AgingSummaryViewModel>(R.layout.fragment_aging_summary, AgingSummaryViewModel::class.java) {

    private val args: AgingSummaryFragmentArgs by navArgs()

    private val customerAccountNumber: String by lazy {
        args.customerAccountNumber
    }

    override val handlesOwnLoadingIndicator = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar,R.string.aging_title)
        viewModel.init(customerAccountNumber, ACService.customerService)
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            syncCustomerDetails(it)
        })
        viewModel.agingSummary.observe(viewLifecycleOwner, Observer { agingSummary ->
            agingSummary?.let { syncAgingSummary(it) }
        })
        viewModel.progressStatus.observe(viewLifecycleOwner, Observer {
            syncProgressStatus(it)
        })
        genericErrorRefreshButton.setOnClickListener {
            viewModel.fetchAgingSummary()
        }
        agingSummarySeeMore.setOnClickListener {
            findNavController().navigate(
                AgingSummaryFragmentDirections.actionAgingSummaryFragmentToAgingDetailsFragment(
                    customerAccountNumber
                )
            )
        }
    }

    private fun syncCustomerDetails(customer: Customer) {
        with(customer) {
            customerCompanyName.text = companyName
            customerCompanyAccountNumber.text = accountNumber
        }
    }

    private fun syncAgingSummary(summary: CustomerAgingSummary) {
        with(summary) {
            agingSummaryCurrentOutstandingAmount.text = FormattingHelper.formatPrice(currentOutstanding)
            agingSummaryOneMonthOutstandingAmount.text = FormattingHelper.formatPrice(oneMonthOutstanding)
            agingSummaryTwoMonthOutstandingAmount.text = FormattingHelper.formatPrice(twoMonthOutstanding)
            agingSummaryThreeMonthOutstandingAmount.text = FormattingHelper.formatPrice(threeMonthOutstanding)
            agingSummaryFourMonthOutstandingAmount.text = FormattingHelper.formatPrice(fourMonthOutstanding)
            agingSummaryFiveMonthOverOutstandingAmount.text = FormattingHelper.formatPrice(fiveMonthAndOverOutstanding)
            agingSummaryBalance.text = FormattingHelper.formatPrice(balance)
            agingSummaryTotalOverdue.text = FormattingHelper.formatPrice(totalOverdue)
        }
    }

    private fun syncProgressStatus(status: AgingSummaryViewModel.ProgressStatus) {
        with(status) {
            agingSummaryLoadingProgress.isVisible = inProgress
            agingSummaryDetails.isVisible = !inProgress && !hasError
            agingSummaryErrorLayout.isVisible = hasError
        }
    }
}