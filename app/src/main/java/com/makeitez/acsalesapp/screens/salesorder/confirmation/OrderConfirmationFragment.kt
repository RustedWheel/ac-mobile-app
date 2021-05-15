package com.makeitez.acsalesapp.screens.salesorder.confirmation

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderMode
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import kotlinx.android.synthetic.main.fragment_order_confirmation.*

class OrderConfirmationFragment : ACFragment.WithViewModel<OrderConfirmationViewModel>(
    R.layout.fragment_order_confirmation,
    OrderConfirmationViewModel::class.java
) {

    private val args: OrderConfirmationFragmentArgs by navArgs()

    private val viewSalesOrderViewModel: ViewSalesOrderViewModel by activityViewModels()

    private val isSuccessful: Boolean by lazy {
        args.isOrderSuccessful
    }

    private val isDuplicate: Boolean by lazy {
        args.isDuplicateOrder
    }

    private val docNo: String by lazy {
        args.salesOrderDocNo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActionBar()
        displayOrderConfirmationStatus()
        viewSalesOrderViewModel.init(ACService.salesOrderService)
        viewModel.init(ACService.salesOrderService)
        viewModel.onViewSalesOrderEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                viewSalesOrderViewModel.viewSalesOrder(it)

                findNavController().navigate(OrderConfirmationFragmentDirections
                    .actionOrderConfirmationFragmentToViewCartFragment(ViewSalesOrderMode.PastOrder))
            }
        })

        orderConfirmationViewSalesOrderCard.setOnClickListener {
            viewModel.getSalesOrder(docNo, isSuccessful)
        }

        orderConfirmationBackToHomeCard.setOnClickListener {
            navigateBackToHome()
        }
    }

    private fun displayOrderConfirmationStatus() {
        orderConfirmationLayout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (isSuccessful) R.color.ac_red_300 else R.color.ac_orange_400
            )
        )
        orderConfirmationCheckmarkImage.setImageResource(if (!isDuplicate) R.drawable.ic_checkmark_circle_outline else R.drawable.ic_alert_circle_outline)
        orderConfirmationStatusText.setText(if (isSuccessful) {
            if(!isDuplicate) R.string.order_confirmation_submitted else R.string.order_confirmation_submitted_already
        } else {
            if(!isDuplicate) R.string.order_confirmation_pending else R.string.order_confirmation_pending_already
        })
        orderConfirmationDocNoText.text = docNo
    }

    private fun navigateBackToHome() {
        requireActivity().finish()
    }

    override fun onBackPressed() {
        // disable back press
    }
}