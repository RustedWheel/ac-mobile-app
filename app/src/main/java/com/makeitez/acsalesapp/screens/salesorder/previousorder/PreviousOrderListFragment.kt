package com.makeitez.acsalesapp.screens.salesorder.previousorder

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.SalesOrder
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderMode
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.show
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_customer_previous_order_list.*
import java.util.*

class PreviousOrderListFragment: ACFragment.WithViewModel<PreviousOrderListViewModel>(R.layout.fragment_customer_previous_order_list, PreviousOrderListViewModel::class.java),
    PreviousOrderViewHolder.Listener, SearchSalesOrderDialog.Listener {

    override val handlesOwnLoadingIndicator
        get() = true

    private val args: PreviousOrderListFragmentArgs by navArgs()

    private val viewType: PreviousOrdersViewType by lazy {
        args.viewType
    }

    private val previousOrderListAdapter = PreviousOrderListAdapter(this)

    private val viewSalesOrderViewModel: ViewSalesOrderViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewSalesOrderViewModel.init(ACService.salesOrderService)
        viewModel.init(viewType, args.customerAccountNumber, ACService.salesOrderService)
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.previousOrderList.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.salesOrderDetailsProgress.observe(viewLifecycleOwner, Observer { showProgressDialog(it) })
        viewModel.orderSearchInfo.observe(viewLifecycleOwner, Observer { syncOrderSearchInfo(it) })
        viewModel.onSalesOrderDetailsLoadedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                onSalesOrderDetailsLoaded(it)
            }
        })

        setUpActionBar()

        previousOrderListRecycler.adapter = previousOrderListAdapter

        previousOrderListRefreshLayout.setOnRefreshListener {
            viewModel.fetchPreviousOrders()
        }

        setUpSearchLayout(viewModel.isUserSearchEnabled)
    }

    private fun setUpActionBar() {
        setActionBar(toolbar, when (viewType) {
            PreviousOrdersViewType.CustomerPreviousOrders -> R.string.previous_orders_title
            PreviousOrdersViewType.OrderHistory -> R.string.order_history_title
            PreviousOrdersViewType.PendingApprovals -> R.string.pending_approval_title
        })
    }

    private fun setUpSearchLayout(isSearchEnabled: Boolean) {
        previousOrderSearchInfoLayout.isVisible = isSearchEnabled
        previousOrderSearchButton.isVisible = isSearchEnabled
        previousOrderBottomShadow.isVisible = isSearchEnabled

        previousOrderSearchButton.setOnClickListener {
            SearchSalesOrderDialog().withData(
                viewModel.customerNameSearch,
                viewModel.fromDate,
                viewModel.toDate
            ).listen(this).show(this)
        }
    }

    override fun onPreviousOrderClick(previousOrder: SalesOrder) {
        viewModel.onPreviousOrderSelected(previousOrder)
    }

    override fun onSearchSalesOrder(customerName: String, fromDate: Date?, toDate: Date?) {
        viewModel.setSearch(customerName, fromDate, toDate)
    }

    override fun onResetSalesOrderSearch() {
        viewModel.resetSearch()
    }

    private fun syncOrderSearchInfo(orderSearchInfo: PreviousOrderListViewModel.OrderSearchInfo) {
        with(orderSearchInfo) {
            previousOrderCustomerSearchText.text = searchedCustomerName
            previousOrderDateSearchText.text = searchedDateRange
        }
    }

    private fun syncData(data: List<SalesOrder>) {
        previousOrderListAdapter.setList(data)
    }

    private fun syncProgress(inProgress: Boolean) {
        previousOrderListRefreshLayout.isRefreshing = inProgress
    }

    private fun syncContentState(state: ListContentState) {
        previousOrderListRecycler.isVisible = state == ListContentState.NotEmpty
        previousOrderNoDataText.isVisible = state == ListContentState.EmptyData
    }

    private fun onSalesOrderDetailsLoaded(salesOrder: SalesOrder) {
        val viewMode = when(viewType) {
            PreviousOrdersViewType.CustomerPreviousOrders -> ViewSalesOrderMode.Reorder
            PreviousOrdersViewType.OrderHistory -> ViewSalesOrderMode.PastOrder
            PreviousOrdersViewType.PendingApprovals -> ViewSalesOrderMode.PendingApproval
        }

        viewSalesOrderViewModel.viewSalesOrder(salesOrder)
        findNavController().navigate(PreviousOrderListFragmentDirections.actionPreviousOrderListFragmentToViewCartFragment(viewMode))
    }
}