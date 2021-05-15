package com.makeitez.acsalesapp.screens.customers.customerdetails

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
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.ACUser
import com.makeitez.acsalesapp.screens.customers.CustomerCreditDialog
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.screens.salesorder.previousorder.PreviousOrdersViewType
import com.makeitez.acsalesapp.utils.extensions.setNonEmptyText
import com.makeitez.acsalesapp.utils.extensions.setToolbarTitle
import com.makeitez.acsalesapp.utils.extensions.show
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import kotlinx.android.synthetic.main.component_generic_try_again.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_customer_details.*

class CustomerDetailsFragment: ACFragment.WithViewModel<CustomerDetailsViewModel>(R.layout.fragment_customer_details, CustomerDetailsViewModel::class.java) {

    private val args: CustomerDetailsFragmentArgs by navArgs()

    private val customerAccountNumber: String by lazy {
        args.customerAccountNumber;
    }

    private val sharedViewModel: CreateSalesOrderViewModel by activityViewModels()

    private val customerBranchListAdapter = CustomerBranchListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)

        setHasOptionsMenu(true)

        viewModel.init(customerAccountNumber, ACService.customerService)

        viewModel.customer.observe(viewLifecycleOwner, Observer {
            syncCustomerDetails(it)
        })

        viewModel.shouldShowDetails.observe(viewLifecycleOwner, Observer {
            customerDetailsErrorLayout.isVisible = !it
            customerDetailsLayout.isVisible = it
            requireAcActivity().invalidateOptionsMenu()
        })

        viewModel.downloadedPdfFileUri.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { statementFileUri ->
                FileSystemHelper.openPdf(this, statementFileUri)
            }
        })

        genericErrorRefreshButton.setOnClickListener {
            viewModel.getCustomerDetails()
        }

        customerDetailsPreviousOrdersButton.setOnClickListener {
            viewModel.customer.value?.let {
                findNavController()
                    .navigate(CustomerDetailsFragmentDirections.actionCustomerDetailsFragmentToPreviousOrderListFragment
                        (PreviousOrdersViewType.CustomerPreviousOrders, it.accountNumber))
            }
        }

        customerDetailsOrderButton.setOnClickListener {
            viewModel.customer.value?.let {
                sharedViewModel.startSalesOrder(it)
                findNavController()
                    .navigate(CustomerDetailsFragmentDirections.actionCustomerDetailsFragmentToCartFragment())
            }
        }

        customerDetailsBranchesRecycler.adapter = customerBranchListAdapter
    }

    private fun syncCustomerDetails(customer: Customer) {
        with(customer) {
            setToolbarTitle(accountNumber)
            customerDetailsCompanyName.text = companyName

            customerDetailsSalesAgentLayout.isVisible = ACUser.isAdmin()
            customerDetailsSalesAgentText.setNonEmptyText(salesAgent)

            customerDetailsCurrencyText.text = currencyCode
            customerDetailsAddressText.setNonEmptyText(address)
            customerDetailsPhoneNoText.setNonEmptyText(phoneNumber)
            customerDetailsFaxNoText.setNonEmptyText(faxNumber)
            customerDetailsContactPersonText.setNonEmptyText(contactPerson)
            customerDetailsEmailText.setNonEmptyText(email)

            customerDetailsBranchesLayout.isVisible = hasBranches()
            if(hasBranches()) customerBranchListAdapter.setList(branches)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_customer_details, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.setGroupVisible(R.id.customerDetailsOptionsGroup, viewModel.shouldShowDetails.value ?: false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.customerMonthlyStatement -> {
                attemptToDownloadMonthlyStatement()
                true
            }
            R.id.customerCreditCheck -> {
                CustomerCreditDialog().withData(customerAccountNumber, canContinue = false, fetchApiDetails = false).show(this)
                true
            }
            R.id.customerAging -> {
                findNavController().navigate(CustomerDetailsFragmentDirections.actionCustomerDetailsFragmentToAgingSummaryFragment(customerAccountNumber))
                true
            }
            R.id.customerPaymentInfo -> {
                findNavController().navigate(CustomerDetailsFragmentDirections.actionCustomerDetailsFragmentToPaymentListFragment(customerAccountNumber))
                true
            }
            else -> false
        }
    }

    private fun attemptToDownloadMonthlyStatement() {
        val context = context?.applicationContext ?: return
        val hasWritePermission = FileSystemHelper.isWriteAllowed(context)
        if (hasWritePermission) {
            viewModel.downloadMonthlyStatement(context)
        } else {
            FileSystemHelper.requestWritePermission(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (FileSystemHelper.checkWritePermissionGranted(requestCode, permissions, grantResults)) {
            val context = context?.applicationContext ?: return
            viewModel.downloadMonthlyStatement(context)
        }
    }
}