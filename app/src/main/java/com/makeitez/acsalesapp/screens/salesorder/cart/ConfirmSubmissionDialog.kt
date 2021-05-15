package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.models.api.SalesOrderResult
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.dialog_confirm_submission.*
import java.math.BigDecimal

class ConfirmSubmissionDialog: ACDialogFragment(R.layout.dialog_confirm_submission) {

    interface Listener {
        fun onSubmissionConfirmed(docNo: String?)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()

    private val salesOrderResult: SalesOrderResult? by lazy {
        arguments?.getParcelable<SalesOrderResult>(CONFIRM_SUBMISSION_RESULT_KEY)
    }

    fun withData(currency: String?, total: BigDecimal?, tax: BigDecimal?, creditLimit: Double? = null, result: SalesOrderResult? = null): ConfirmSubmissionDialog
        = withData(currency, total?.toDouble(), tax?.toDouble(), creditLimit, result)

    fun withData(currency: String?, total: Double?, tax: Double?, creditLimit: Double? = null, result: SalesOrderResult? = null): ConfirmSubmissionDialog {
        val args = arguments ?: Bundle()
        args.putString(CONFIRM_SUBMISSION_CURRENCY_KEY, currency)
        args.putDouble(CONFIRM_SUBMISSION_TOTAL_KEY, total ?: 0.0)
        args.putDouble(CONFIRM_SUBMISSION_TAX_KEY, tax ?: 0.0)
        args.putDouble(CONFIRM_SUBMISSION_CREDIT_LIMIT_KEY, creditLimit ?: 0.0)
        args.putParcelable(CONFIRM_SUBMISSION_RESULT_KEY, result)
        arguments = args
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        confirmSubmissionTitle.setText(if (salesOrderResult?.violations == null) R.string.order_confirm_submission else R.string.order_approval_required)
        confirmSubmissionConfirmButton.apply {
            setText(R.string.order_submit_order)
            setOnClickListener {
                listener?.onSubmissionConfirmed(salesOrderResult?.docNo)
                dismiss()
            }
        }
        confirmSubmissionCancelButton.setOnClickListener {
            dismiss()
        }
        syncOrderAmount()
        syncViolations()
    }

    private fun syncOrderAmount() {
        // Since we only need to set the text values once we don't need to make the argument values fields
        val currency = arguments?.getString(CONFIRM_SUBMISSION_CURRENCY_KEY) ?: ""
        val total = arguments?.getDouble(CONFIRM_SUBMISSION_TOTAL_KEY) ?: 0.0
        val tax = arguments?.getDouble(CONFIRM_SUBMISSION_TAX_KEY) ?: 0.0
        confirmSubmissionTotalText.text = getString(R.string.order_total, FormattingHelper.formatPrice(total, currency))
        confirmSubmissionTotalTaxText.text = getString(R.string.order_tax, FormattingHelper.formatPrice(tax, currency))
    }

    private fun syncViolations() {
        val violations = salesOrderResult?.violations ?: return
        val creditLimit = arguments?.getDouble(CONFIRM_SUBMISSION_CREDIT_LIMIT_KEY) ?: 0.0
        confirmSubmissionViolations.isVisible = true

        val hasProductViolations = violations.isNewPrice || violations.hasDiscount || violations.hasFOC
        confirmSubmissionViolationsProductHeader.isVisible = hasProductViolations
        confirmSubmissionViolationHasNewPrice.isVisible = violations.isNewPrice
        confirmSubmissionViolationDiscount.isVisible = violations.hasDiscount
        confirmSubmissionViolationFOC.isVisible = violations.hasFOC

        val hasOrderViolations = violations.hasExceededCreditLimit || violations.hasOverdue
        confirmSubmissionViolationsSpace.isVisible = hasProductViolations && hasOrderViolations
        confirmSubmissionViolationsOrderHeader.isVisible = hasOrderViolations
        confirmSubmissionViolationCreditLimit.isVisible = hasOrderViolations
        confirmSubmissionViolationCreditLimit.text = getString(R.string.order_violation_credit_limit, FormattingHelper.formatPrice(creditLimit))
        confirmSubmissionViolationExceededCredit.isVisible = violations.hasExceededCreditLimit
        confirmSubmissionViolationExceededCreditText.text = FormattingHelper.formatPrice(violations.exceededCreditLimitAmount)
        confirmSubmissionViolationOverdueAmount.isVisible = violations.hasOverdue
        confirmSubmissionViolationOverdueAmountText.text = FormattingHelper.formatPrice(violations.overdueAmount)
    }

    companion object {
        private const val CONFIRM_SUBMISSION_CURRENCY_KEY = "confirmSubmissionCurrencyKey"
        private const val CONFIRM_SUBMISSION_TOTAL_KEY = "confirmSubmissionTotalKey"
        private const val CONFIRM_SUBMISSION_TAX_KEY = "confirmSubmissionTaxKey"
        private const val CONFIRM_SUBMISSION_CREDIT_LIMIT_KEY = "confirmSubmissionCreditLimitKey"
        private const val CONFIRM_SUBMISSION_RESULT_KEY = "confirmSubmissionViolationsKey"
    }
}