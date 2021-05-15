package com.makeitez.acsalesapp.screens.customers.aging

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.tableview.ACTableViewAdapter
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.utils.ListContentState
import kotlinx.android.synthetic.main.component_customer_id_header.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_aging_details.*

class AgingDetailsFragment : ACFragment.WithViewModel<AgingDetailsViewModel>(R.layout.fragment_aging_details, AgingDetailsViewModel::class.java) {

    private val args: AgingDetailsFragmentArgs by navArgs()

    private val customerAccountNumber: String by lazy {
        args.customerAccountNumber
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar,R.string.detailed_aging_title)
        viewModel.init(customerAccountNumber)
        viewModel.customer.observe(viewLifecycleOwner, Observer {
            syncCustomerDetails(it)
        })
        viewModel.agingTableData.observe(viewLifecycleOwner, Observer {tableData ->
            tableData?.let { ACTableViewAdapter.setAdapterForTable(agingTable, it) }
        })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
    }

    private fun syncCustomerDetails(customer: Customer) {
        with(customer) {
            customerCompanyName.text = companyName
            customerCompanyAccountNumber.text = accountNumber
        }
    }

    private fun syncContentState(state: ListContentState?) {
        agingTable.isVisible = state == ListContentState.NotEmpty
        agingNoRecordsText.isVisible = state == ListContentState.EmptyData
    }
}