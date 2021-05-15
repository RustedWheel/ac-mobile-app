package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.SalesOrderRemarks
import com.makeitez.acsalesapp.models.enums.SalesOrderStatus
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderMode
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import com.makeitez.acsalesapp.utils.extensions.*
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import kotlinx.android.synthetic.main.component_cart_customer_header.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_view_cart.*


class ViewCartFragment : ACFragment.WithViewModel<ViewSalesOrderViewModel>(
    R.layout.fragment_view_cart, ViewSalesOrderViewModel::class.java, true
), ProductOrderListener, ConfirmSubmissionDialog.Listener  {

    private val args: ViewCartFragmentArgs by navArgs()

    private val viewMode: ViewSalesOrderMode by lazy {
        args.viewMode
    }

    private val productOrderListAdapter: ProductOrderListAdapter by lazy {
        ProductOrderListAdapter(
            this, isReadOnly = true,
            indicateQuotations = viewMode == ViewSalesOrderMode.Reorder,
            indicateViolations = viewMode == ViewSalesOrderMode.PendingApproval
        )
    }

    private val createSalesOrderViewModel: CreateSalesOrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)

        setHasOptionsMenu(viewMode == ViewSalesOrderMode.PastOrder && viewModel.orderStatus == SalesOrderStatus.Success)

        viewModel.init(ACService.salesOrderService)
        viewModel.orderCustomerInfo.observe(viewLifecycleOwner, Observer {
            syncCustomerData(it)
        })

        viewModel.productOrderList.observe(viewLifecycleOwner, Observer {
            productOrderListAdapter.setList(it, viewModel.currency, viewModel.submissionRecord.value, viewModel.isCreditLimitExceeded, viewModel.isOverdueLimitExceeded)
        })

        cartProductOrdersRecycler.adapter = productOrderListAdapter

        viewModel.submissionStatusAndTotal.observe(viewLifecycleOwner, Observer { it?.let { statusAndTotalText ->
            cartTotalWithTaxText.text = statusAndTotalText
        }})

        viewModel.onReorderSalesOrderEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                createSalesOrderViewModel.startSalesOrder(it)
                findNavController()
                    .navigate(ViewCartFragmentDirections.actionViewCartFragmentToCartFragment())
            }
        })

        viewModel.downloadedPdfFileUri.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { statementFileUri ->
                FileSystemHelper.openPdf(this, statementFileUri)
            }
        })

        viewModel.onHasSOViolationEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { result ->
                ConfirmSubmissionDialog().withData(
                    viewModel.currency,
                    viewModel.confirmedTotalAmount,
                    viewModel.confirmedTaxAmount,
                    viewModel.customer?.creditLimit,
                    result
                ).listen(this).show(this)
            }
        })

        viewModel.onSOConfirmedMessageEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {orderConfirmationMessage ->
                view.showSnackbar(orderConfirmationMessage, Snackbar.LENGTH_INDEFINITE)
                onBackPressed()
            }
        })

        cartTotalAndRemarksCard.setOnClickListener {
            OrderRemarksDialog().withData(
                viewModel.salesOrderRemarks ?: SalesOrderRemarks(),
                isReadOnly = true
            ).show(this)
        }

        setUpCartMainAction()
    }

    private fun syncCustomerData(customerInfo: ViewSalesOrderViewModel.OrderCustomerInfo) {
        setToolbarTitle(customerInfo.accountNumber)

        cartCustomerCurrencyText.text = customerInfo.currencyCode
        cartCustomerNameText.textToShow = customerInfo.companyOrBranchName
    }

    private fun setUpCartMainAction() {
        when (viewMode) {
            ViewSalesOrderMode.PastOrder -> {
                cartMainButton.text = getString(R.string.cart_go_back)
                cartMainButton.setOnClickListener {
                    onBackPressed()
                }

            }
            ViewSalesOrderMode.Reorder -> {
                cartMainButton.text = getString(R.string.cart_reorder)
                cartMainButton.setOnClickListener {
                    viewModel.reorderSalesOrder()
                }
            }
            ViewSalesOrderMode.PendingApproval -> {
                cartMainButton.text = getString(R.string.cart_approve)
                cartMainButton.setOnClickListener {
                    viewModel.confirmPendingSalesOrder(true)
                }
                cartSecondaryButton.isVisible = true
                cartSecondaryButton.text = getString(R.string.cart_reject)
                cartSecondaryButton.setOnClickListener {
                    viewModel.confirmPendingSalesOrder(false)
                }
            }
        }
    }

    override fun onProductOrderClick(order: ProductOrder) {
        viewModel.viewProductOrder(order)

        findNavController()
            .navigate(ViewCartFragmentDirections.actionViewCartFragmentToViewProductOrderFragment(
                indicateViolations = viewMode == ViewSalesOrderMode.PendingApproval
            ))
    }

    override fun onCreditLimitExceededPressed() {
        findNavController().navigate(ViewCartFragmentDirections.actionViewCartFragmentToCreditLimitFragment())
    }

    override fun onOverdueLimitExceededPressed() {
        findNavController().navigate(ViewCartFragmentDirections.actionViewCartFragmentToOverdueLimitFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_past_sales_order, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.salesOrderPdfItem -> {
                attemptToDownloadStatement()
                true
            }
            else -> false
        }
    }

    override fun onProductOrderDelete(order: ProductOrder) {}
    override fun onProductOrderIncrement(order: ProductOrder) {}
    override fun onProductOrderDecrement(order: ProductOrder) {}

    private fun attemptToDownloadStatement() {
        val context = context?.applicationContext ?: return
        val hasWritePermission = FileSystemHelper.isWriteAllowed(context)
        if (hasWritePermission) {
            viewModel.downloadStatement(context)
        } else {
            FileSystemHelper.requestWritePermission(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (FileSystemHelper.checkWritePermissionGranted(requestCode, permissions, grantResults)) {
            val context = context?.applicationContext ?: return
            viewModel.downloadStatement(context)
        }
    }

    override fun onSubmissionConfirmed(docNo: String?) {
        viewModel.confirmPendingSalesOrderWithSOViolations()
    }
}