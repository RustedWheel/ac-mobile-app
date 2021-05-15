package com.makeitez.acsalesapp.screens.customers.paymentlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.tableview.ACTableViewAdapter
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import kotlinx.android.synthetic.main.component_customer_id_header.*
import kotlinx.android.synthetic.main.component_generic_try_again.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_payment_list.*

class PaymentListFragment: ACFragment.WithViewModel<PaymentListViewModel>(R.layout.fragment_payment_list, PaymentListViewModel::class.java) {

    private val args: PaymentListFragmentArgs by navArgs()

    private val customerAccountNumber: String by lazy {
        args.customerAccountNumber
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar,R.string.payment_info_title)
        viewModel.init(customerAccountNumber, ACService.customerService)
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            syncCustomerDetails(it)
        })
        viewModel.paymentListData.observe(viewLifecycleOwner, Observer {tableData ->
            tableData?.let { ACTableViewAdapter.setAdapterForTable(paymentListTable, it) }
        })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.hasError.observe(viewLifecycleOwner, Observer { syncErrorState(it) })
        viewModel.paymentDetailProgress.observe(viewLifecycleOwner, Observer { showProgressDialog(it) })
        viewModel.downloadedPdfFileUri.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { statementFileUri ->
                FileSystemHelper.openPdf(this, statementFileUri)
            }
        })

        paymentListTable.tableViewListener = this
        genericErrorRefreshButton.setOnClickListener {
            viewModel.fetchPaymentList()
        }
    }

    private fun syncCustomerDetails(customer: Customer) {
        with(customer) {
            customerCompanyName.text = companyName
            customerCompanyAccountNumber.text = accountNumber
        }
    }

    private fun syncContentState(state: ListContentState?) {
        paymentListTable.isVisible = state == ListContentState.NotEmpty
        paymentListNoRecordsText.isVisible = state == ListContentState.EmptyData
    }

    private fun syncErrorState(hasError: Boolean?) {
        paymentListErrorLayout.isVisible = hasError == true
    }

    override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        viewModel.onPaymentRowClick(row)
        attemptToDownloadPaymentDetailStatement()
    }

    private fun attemptToDownloadPaymentDetailStatement() {
        val context = context?.applicationContext ?: return
        val hasWritePermission = FileSystemHelper.isWriteAllowed(context)
        if (hasWritePermission) {
            viewModel.downloadPaymentDetailStatement(context)
        } else {
            FileSystemHelper.requestWritePermission(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (FileSystemHelper.checkWritePermissionGranted(requestCode, permissions, grantResults)) {
            val context = context?.applicationContext ?: return
            viewModel.downloadPaymentDetailStatement(context)
        }
    }

}