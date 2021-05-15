package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.SalesOrderViolationSummary
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.extensions.toNdpString
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import com.makeitez.acsalesapp.utils.helper.FormattingHelper.dateFormatLong
import kotlinx.android.synthetic.main.component_customer_id_header.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_overdue_limit.*

class OverdueLimitFragment : ACFragment.WithViewModel<ViewSalesOrderViewModel>(R.layout.fragment_overdue_limit, ViewSalesOrderViewModel::class.java, true) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar, R.string.overdue_limit_exceeded_title)
        viewModel.init(ACService.salesOrderService)
        syncOverDueLimitDetails(viewModel.salesOrderViolationSummary)
        syncCustomerInfo(viewModel.customer)
        overdueDocDetailsButton.setOnClickListener {
            findNavController().navigate(
                OverdueLimitFragmentDirections.actionViewCartFragmentToOverdueDocDetailsFragment()
            )
        }
    }

    private fun syncCustomerInfo(customer: Customer?) {
        customer?.let {
            customerCompanyName.text = it.companyName
            customerCompanyAccountNumber.text = it.accountNumber
        }
    }

    private fun syncOverDueLimitDetails(salesOrderViolationSummary: SalesOrderViolationSummary?) {
        salesOrderViolationSummary?.let { violationSummary ->
            overdueLimitRequestedUserId.text = violationSummary.requestedUserId
            overdueLimitRequestedDateTime.text = FormattingHelper.formatDate(violationSummary.requestedDateTime, dateFormatLong)
            overdueLimitNetTotal.text = violationSummary.netTotal.toNdpString(Config.UNIT_PRICE_DPS)

            violationSummary.exceededOverdueLimitSummary?.let { overdueLimitSummary ->
                overdueLimitCurrentOverdue.text = overdueLimitSummary.currentOverdue.toNdpString(Config.NORMAL_PRICE_DPS)
                overdueLimitOriginalOverdueLimit.text = overdueLimitSummary.originalOverdueLimit.toNdpString(Config.NORMAL_PRICE_DPS)
                overdueLimitIncrementPercentage.text = overdueLimitSummary.overdueLimitIncrement.toNdpString(Config.PERCENTAGE_DPS)
                overdueLimitIncreasedOverdueLimit.text = overdueLimitSummary.increasedOverdueLimit.toNdpString(Config.NORMAL_PRICE_DPS)
                overdueLimitExceededOverdue.text = overdueLimitSummary.exceededOverdue.toNdpString(Config.NORMAL_PRICE_DPS)
            }
        }
    }
}