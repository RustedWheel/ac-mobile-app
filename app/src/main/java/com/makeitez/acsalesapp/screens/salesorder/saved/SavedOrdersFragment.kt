package com.makeitez.acsalesapp.screens.salesorder.saved

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.show
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_saved_orders.*

class SavedOrdersFragment : ACFragment.WithViewModel<SavedOrdersViewModel>(R.layout.fragment_saved_orders, SavedOrdersViewModel::class.java),
    SavedOrdersViewHolder.Listener, DeleteSavedOrderDialog.Listener {

    private val sharedViewModel: CreateSalesOrderViewModel by activityViewModels()

    private val savedOrdersAdapter = SavedOrdersListAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar, R.string.saved_order_title)
        savedOrderRecycler.adapter = savedOrdersAdapter
        viewModel.init(ACService.salesOrderService)
        viewModel.savedOrders.observe(viewLifecycleOwner, Observer {
            syncData(it)
        })
        viewModel.onSalesOrderVerifiedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                startSalesOrderFromSaved(it)
            }
        })
    }

    override fun onSalesOrderPressed(savedOrder: SalesOrder) {
        viewModel.verifySavedOrder(savedOrder)
    }

    override fun onDeleteSalesOrder(savedOrder: SalesOrder) {
        DeleteSavedOrderDialog().withData(savedOrder.docNo).listen(this).show(this)
    }

    override fun onDeleteSavedOrderConfirmed(savedOrderId: String) {
        viewModel.deleteSavedOrder(savedOrderId)
    }

    private fun syncData(list: List<SalesOrder>) {
        savedOrdersAdapter.setList(list)
        savedOrderRecycler.isVisible = list.isNotEmpty()
        savedOrderEmptyState.isVisible = list.isEmpty()
    }

    private fun startSalesOrderFromSaved(savedOrder: SalesOrder) {
        sharedViewModel.startSalesOrder(savedOrder)
        findNavController().navigate(SavedOrdersFragmentDirections.actionSavedOrdersFragment2ToCartFragment())
    }
}