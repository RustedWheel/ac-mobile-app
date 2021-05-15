package com.makeitez.acsalesapp.screens.salesorder

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACActivity
import com.makeitez.acsalesapp.screens.salesorder.previousorder.PreviousOrdersViewType

class SalesOrderActivity : ACActivity.WithViewModel<CreateSalesOrderViewModel>(R.layout.activity_sales_order, CreateSalesOrderViewModel::class.java) {

    enum class SalesOrderViewType {
        NewOrder,
        SavedOrder,
        PreviousOrder,
        PendingOrder
    }

    private val salesOrderViewType: SalesOrderViewType by lazy {
        intent.getStringExtra(SALES_ORDER_VIEW_TYPE_KEY)?.let { SalesOrderViewType.valueOf(it) } ?: SalesOrderViewType.NewOrder
    }

    private val navController: NavController by lazy {
        this.findNavController(R.id.salesOrderNavHost)
    }

    override fun onConfirmedCreate(savedInstanceState: Bundle?) {
        super.onConfirmedCreate(savedInstanceState)
        viewModel.init(ACService.salesOrderService)
        setupNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun setupNavigation() {
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_sales_order)
        var startDestinationArgs: Bundle? = null
        navGraph.startDestination = when (salesOrderViewType) {
            SalesOrderViewType.NewOrder -> {
                R.id.customerListFragment
            }
            SalesOrderViewType.SavedOrder -> {
                R.id.savedOrdersFragment
            }
            SalesOrderViewType.PreviousOrder -> {
                startDestinationArgs = bundleOf("viewType" to PreviousOrdersViewType.OrderHistory)
                R.id.previousOrderListFragment
            }
            SalesOrderViewType.PendingOrder -> {
                startDestinationArgs = bundleOf("viewType" to PreviousOrdersViewType.PendingApprovals)
                R.id.previousOrderListFragment
            }
        }
        navController.setGraph(navGraph, startDestinationArgs)
    }

    companion object {
        private const val SALES_ORDER_VIEW_TYPE_KEY = "salesOrderViewType"

        fun show(activity: ACActivity, viewType: SalesOrderViewType) {
            val intent = Intent(activity, SalesOrderActivity::class.java).apply {
                putExtra(SALES_ORDER_VIEW_TYPE_KEY, viewType.toString())
            }
            activity.startActivity(intent)
        }
    }
}