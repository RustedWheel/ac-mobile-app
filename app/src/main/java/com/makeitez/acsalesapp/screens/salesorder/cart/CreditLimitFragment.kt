package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_credit_limit.*

class CreditLimitFragment : ACFragment.WithViewModel<ViewSalesOrderViewModel>(R.layout.fragment_credit_limit, ViewSalesOrderViewModel::class.java, true) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar, R.string.credit_limit_exceeded_title)
        viewModel.init(ACService.salesOrderService)
        syncOverCreditLimitDetails(viewModel.salesOrderViolationSummary)
        syncCustomerInfo(viewModel.customer)
    }

    private fun syncCustomerInfo(customer: Customer?) {
        customer?.let {
            customerCompanyName.text = it.companyName
            customerCompanyAccountNumber.text = it.accountNumber
        }
    }

    private fun syncOverCreditLimitDetails(salesOrderViolationSummary: SalesOrderViolationSummary?) {
        salesOrderViolationSummary?.let { violationSummary ->
            creditLimitRequestedUserId.text = violationSummary.requestedUserId
            creditLimitRequestedDateTime.text = FormattingHelper.formatDate(violationSummary.requestedDateTime, dateFormatLong)
            creditLimitNetTotal.text = violationSummary.netTotal.toNdpString(Config.UNIT_PRICE_DPS)

            violationSummary.exceededCreditLimitSummary?.let { creditLimitSummary ->
                creditLimitOutstandingInAR.text = creditLimitSummary.arOutstanding.toNdpString(Config.NORMAL_PRICE_DPS)
                creditLimitOutstandingInDO.text = creditLimitSummary.doOutstanding.toNdpString(Config.NORMAL_PRICE_DPS)
                creditLimitOutstandingInSO.text = creditLimitSummary.soOutstanding.toNdpString(Config.NORMAL_PRICE_DPS)
                creditLimitPDCheque.text = creditLimitSummary.pdCheque.toNdpString(Config.NORMAL_PRICE_DPS)

                creditLimitTotalOutstanding.text = creditLimitSummary.totalOutstanding.toNdpString(Config.NORMAL_PRICE_DPS)
                creditLimitOriginalCreditLimit.text = creditLimitSummary.originalCreditLimit.toNdpString(Config.NORMAL_PRICE_DPS)
                creditLimitIncrementPercentage.text = creditLimitSummary.creditLimitIncrement.toNdpString(Config.PERCENTAGE_DPS)
                creditLimitIncreasedCreditLimit.text = creditLimitSummary.increasedCreditLimit.toNdpString(Config.NORMAL_PRICE_DPS)
                creditLimitExceededCredit.text = creditLimitSummary.exceededCredit.toNdpString(Config.NORMAL_PRICE_DPS)
            }
        }
    }
}