package com.makeitez.acsalesapp.screens.salesorder.cart

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Customer
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.SalesOrderRemarks
import com.makeitez.acsalesapp.screens.salesorder.BaseSalesOrderViewModel
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.utils.extensions.listen
import com.makeitez.acsalesapp.utils.extensions.setToolbarTitle
import com.makeitez.acsalesapp.utils.extensions.show
import kotlinx.android.synthetic.main.component_cart_customer_header.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_cart.*
import java.util.*

class CartFragment :
    ACFragment.WithViewModel<CreateSalesOrderViewModel>(R.layout.fragment_cart, CreateSalesOrderViewModel::class.java, true),
    ProductOrderListener, OrderRemarksDialog.Listener, ConfirmOrderDeletionDialog.Listener,
    AbandonSalesOrderDialog.Listener, AdapterView.OnItemSelectedListener, ConfirmSubmissionDialog.Listener {

    private val productOrderListAdapter: ProductOrderListAdapter by lazy {
        ProductOrderListAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)

        viewModel.customer?.let { syncCustomerData(it) }
        viewModel.branchSelection.observe(viewLifecycleOwner, Observer {syncBranchSelection(it)})

        viewModel.productOrderList.observe(viewLifecycleOwner, Observer {
            productOrderListAdapter.setList(it, viewModel.currency)
        })

        viewModel.cartTotalData.observe(viewLifecycleOwner, Observer {
            syncCartTotalData(it)
        })

        viewModel.cartActionState.observe(viewLifecycleOwner, Observer {
            syncCartActionData(it)
        })

        cartProductOrdersRecycler.adapter = productOrderListAdapter

        viewModel.onConfirmOrderDeletionEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                ConfirmOrderDeletionDialog().withData(it).listen(this).show(this)
            }
        })

        viewModel.onExitCartEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { super.onBackPressed() }
        })

        viewModel.onHasViolationEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { result ->
                val cartTotalData = viewModel.cartTotalData.value
                ConfirmSubmissionDialog().withData(
                    viewModel.currency,
                    cartTotalData?.total,
                    cartTotalData?.totalTax,
                    viewModel.customer?.creditLimit,
                    result
                ).listen(this).show(this)
            }
        })

        viewModel.onSalesOrderConfirmedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToOrderConfirmationScreen(it.docNo, !it.isPendingApproval, it.isDuplicateOrder))
            }
        })

        cartRemarksCard.setOnClickListener {
            val salesOrderRemarks = viewModel.salesOrderRemarks ?: SalesOrderRemarks()
            OrderRemarksDialog().withData(salesOrderRemarks).listen(this).show(this)
        }

        cartAddProductCard.setOnClickListener {
            findNavController()
                .navigate(CartFragmentDirections.actionCartFragmentToCategoryListFragment())
        }

        cartConfirmOrderCard.setOnClickListener {
            val cartTotalData = viewModel.cartTotalData.value
            ConfirmSubmissionDialog().withData(viewModel.currency, cartTotalData?.total, cartTotalData?.totalTax).listen(this).show(this)
        }
    }

    private fun syncCustomerData(customer: Customer) {
        setToolbarTitle(customer.accountNumber)

        cartCustomerCurrencyText.text = customer.currencyCode

        viewModel.customerBranchOptions?.let { branchOptions ->
            context?.let { ctx ->
                cartCustomerBranchSpinnerLayout.isVisible = true
                cartCustomerBranchSpinner.adapter = BranchSpinnerAdapter(ctx, branchOptions)
                cartCustomerBranchSpinner.onItemSelectedListener = this
            }
        }
    }

    private fun syncBranchSelection(branchSelection: Int)
        = cartCustomerBranchSpinner.setSelection(branchSelection)

    private fun syncCartTotalData(data: BaseSalesOrderViewModel.CartTotalData) {
        cartTotalText.text = data.totalLabel
        cartTotalTaxText.text = data.totalTaxLabel
    }

    private fun syncCartActionData(state: CreateSalesOrderViewModel.CartActionState) {
        cartRemarksFilledIndicatorImage.setImageResource(
            if (state.isRemarksFilled) R.drawable.ic_description else R.drawable.ic_insert_drive_file
        )

        cartConfirmOrderCard.isEnabled = state.canContinue
        cartConfirmOrderArrowImage.isVisible = state.canContinue
    }

    override fun onProductOrderClick(order: ProductOrder) {
        viewModel.editProductOrder(order)

        findNavController()
            .navigate(CartFragmentDirections.actionCartFragmentToProductOrderFragment())
    }

    override fun onProductOrderDelete(order: ProductOrder) = viewModel.confirmProductOrderDeletion(order)

    override fun onProductOrderIncrement(order: ProductOrder) = viewModel.incrementProductOrder(order)

    override fun onProductOrderDecrement(order: ProductOrder) = viewModel.decrementProductOrder(order)

    override fun onConfirmOrderDeletion(productOrderPos: Int) = viewModel.deleteProductOrder(productOrderPos)

    override fun onSaveOrderRemarks(deliveryDate: Date, po: String, remarks: String) = viewModel.setRemarks(deliveryDate, po, remarks)

    override fun onBackPressed() {
        if(viewModel.isOrderSubmittable) {
            AbandonSalesOrderDialog().listen(this).show(this)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveSalesOrder() = viewModel.saveSalesOrder()

    override fun onDeleteSalesOrder() = viewModel.deleteSalesOrder()

    override fun onSubmissionConfirmed(docNo: String?) {
        if (docNo == null) {
            viewModel.createSalesOrder()
        } else {
            viewModel.confirmSalesOrder(docNo)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
        = viewModel.selectBranch(position)

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}