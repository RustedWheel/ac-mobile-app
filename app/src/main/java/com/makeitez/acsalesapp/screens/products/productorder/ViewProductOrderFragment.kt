package com.makeitez.acsalesapp.screens.products.productorder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.databinding.FragmentProductOrderBinding
import com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderViewModel
import com.makeitez.acsalesapp.utils.extensions.setToolbarTitle
import kotlinx.android.synthetic.main.component_toolbar.*

class ViewProductOrderFragment : ACFragment.WithDataBinding<FragmentProductOrderBinding, ProductOrderViewModel>(
    R.layout.fragment_product_order, ProductOrderViewModel::class.java) {

    private val args: ViewProductOrderFragmentArgs by navArgs()

    private val sharedViewModel: ViewSalesOrderViewModel by activityViewModels()

    override fun onBindDataToCreatedView(binding: FragmentProductOrderBinding) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)
        sharedViewModel.init(ACService.salesOrderService)
        with(sharedViewModel) {
            viewModel.initProductOrder(currency, currencyRate, currentProductOrder, true, args.indicateViolations)
        }

        viewModel.productInfo.observe(viewLifecycleOwner, Observer { info ->
            setToolbarTitle(info.itemCode)
        })
    }
}