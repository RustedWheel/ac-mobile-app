package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.tableview.ACTableViewAdapter
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import com.makeitez.acsalesapp.utils.ListContentState
import kotlinx.android.synthetic.main.component_customer_id_header.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_overdue_doc_details.*

class OverdueDocDetailsFragment: ACFragment.WithViewModel<OverdueDocDetailsViewModel>(R.layout.fragment_overdue_doc_details, OverdueDocDetailsViewModel::class.java) {
    private val viewSalesOrderViewModel: ViewSalesOrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar,R.string.overdue_doc_details_title)
        viewSalesOrderViewModel.init(ACService.salesOrderService)
        viewSalesOrderViewModel.salesOrderViolationSummary?.exceededOverdueLimitSummary?.let { exceededOverdueLimitSummary ->
            viewModel.init(exceededOverdueLimitSummary)
            viewSalesOrderViewModel.customer?.let {
                syncCustomerDetails(it)
            }
            viewModel.overdueDocTableData.observe(viewLifecycleOwner, Observer {tableData ->
                tableData?.let { ACTableViewAdapter.setAdapterForTable(overdueDocDetailsTable, it) }
            })
            viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        }
    }

    private fun syncCustomerDetails(customer: Customer) {
        customerCompanyName.text = customer.companyName
        customerCompanyAccountNumber.text = customer.accountNumber
    }

    private fun syncContentState(state: ListContentState?) {
        overdueDocDetailsTable.isVisible = state == ListContentState.NotEmpty
        overdueDocDetailsNoRecordsText.isVisible = state == ListContentState.EmptyData
    }
}