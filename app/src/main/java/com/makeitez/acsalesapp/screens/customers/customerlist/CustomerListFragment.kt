package com.makeitez.acsalesapp.screens.customers.customerlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.screens.customers.CustomerCreditDialog
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.show
import kotlinx.android.synthetic.main.fragment_search_list.*

class CustomerListFragment : ACFragment.WithViewModel<CustomerListViewModel>(R.layout.fragment_search_list, CustomerListViewModel::class.java),
    CustomerViewHolder.Listener, CustomerCreditDialog.Listener {

    private val sharedViewModel: CreateSalesOrderViewModel by activityViewModels()

    private val customerListAdapter: CustomerListAdapter by lazy {
        CustomerListAdapter(this)
    }

    override val handlesOwnLoadingIndicator
        get() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(searchToolbar, R.string.select_customer_title)
        viewModel.init(ACService.customerService)
        viewModel.customerList.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.onCustomerSelectedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                startSalesOrder(it)
            }
        })

        searchListRecycler.adapter = customerListAdapter

        searchInputText.doOnTextChanged { searchTerm, _, _, _ ->
            viewModel.onSearchTermChanged(searchTerm.toString())
        }

        searchListRefreshLayout.setOnRefreshListener {
            viewModel.fetchCustomers()
        }
    }

    override fun onCustomerClick(customer: Customer) {
        viewModel.onCustomerClick(customer)
    }

    override fun onCustomerInfoClick(customer: Customer) {
        findNavController()
            .navigate(CustomerListFragmentDirections.actionCustomerListFragmentToCustomerDetailsFragment(customer.accountNumber))
    }

    private fun syncData(customers: List<Customer>) {
        customerListAdapter.setList(customers)
    }

    override fun onCustomerConfirmed(customer: Customer) {
        sharedViewModel.startSalesOrder(customer)
        findNavController()
            .navigate(CustomerListFragmentDirections.actionCustomerListFragmentToCartFragment())
    }

    private fun startSalesOrder(customer: Customer) {
        CustomerCreditDialog().withData(customer.accountNumber).listen(this).show(this)
    }

    private fun syncProgress(inProgress: Boolean) {
        searchListRefreshLayout.isRefreshing = inProgress
    }

    private fun syncContentState(state: ListContentState) {
        searchListRecycler.isVisible = state == ListContentState.NotEmpty
        searchNoResultText.isVisible = state == ListContentState.EmptySearch
        searchNoDataText.isVisible = state == ListContentState.EmptyData
    }
}