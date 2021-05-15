package com.makeitez.acsalesapp.screens.products.productorder

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.DatePickerFragment
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.databinding.FragmentProductOrderBinding
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.DecimalInputFilter
import com.makeitez.acsalesapp.utils.extensions.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_product_order.*


class ProductOrderFragment: ACFragment.WithDataBinding<FragmentProductOrderBinding, ProductOrderViewModel>(R.layout.fragment_product_order, ProductOrderViewModel::class.java),
    DatePickerFragment.Listener, AdapterView.OnItemSelectedListener, AbandonProductOrderDialog.Listener {

    private val sharedViewModel: CreateSalesOrderViewModel by activityViewModels()

    override fun onBindDataToCreatedView(binding: FragmentProductOrderBinding) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)

        setHasOptionsMenu(true)

        with(sharedViewModel) {
            viewModel.initProductOrder(currency, currencyRate, currentProductOrder)
        }

        viewModel.productInfo.observe(viewLifecycleOwner, Observer { syncNonDataBoundProductInfo(it) })
        viewModel.orderPriceChangeSelection.observe(viewLifecycleOwner, Observer { syncPriceChangeSelection(it) })
        viewModel.orderUomSelection.observe(viewLifecycleOwner, Observer { syncUomSelection(it) })
        viewModel.onSaveToCartEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                onSaveToCart(it)
            }
        })
        viewModel.onConfirmOrderAbandonmentEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                AbandonProductOrderDialog().withData(it).listen(this).show(this)
            }
        })
        viewModel.onExitProductOrderEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { super.onBackPressed() }
        })

        binding.productUnitPriceInputText.setInputFilter(DecimalInputFilter(Config.UNIT_PRICE_DPS))
        binding.productPriceChangeSpinner.adapter = LabelSpinnerAdapter(requireContext(), viewModel.priceChangeOptions)

        binding.productPriceChangeSpinner.onItemSelectedListener = this
        binding.productUomSpinner.onItemSelectedListener = this

        binding.productDeliveryDateInputText.setOnClickListener {
            DatePickerFragment().withData(viewModel.orderDeliveryDate.value).listen(this).show(this)
        }
    }

    private fun syncNonDataBoundProductInfo(info: ProductOrderViewModel.ProductInfo) {
        setToolbarTitle(info.itemCode)

        context?.let {
            binding.productUomSpinner.adapter = UomSpinnerAdapter(it, info.uomOptions)
        }
    }

    private fun syncPriceChangeSelection(optionSelection: Int) {
        binding.productPriceChangeSpinner.setSelection(optionSelection)
        binding.productPriceChangeInputText.setInputFilter(DecimalInputFilter(
            when(optionSelection) {
                ProductOrderViewModel.PRICE_CHANGE_DISCOUNT_SELECTION -> Config.DISCOUNT_DPS
                else -> Config.UNIT_PRICE_DPS
            }))
    }

    private fun syncUomSelection(uomSelection: Int) =
        binding.productUomSpinner.setSelection(uomSelection)

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.productPriceChangeSpinner -> viewModel.onPriceChangeOptionSelected(position)
            R.id.productUomSpinner -> viewModel.onUomSelected(position)
        }
    }

    override fun onDateSelected(requestCode: Int?, day: Int, month: Int, year: Int) =
        viewModel.onDeliveryDateSelected(day, month, year)

    private fun onSaveToCart(productOrder: ProductOrder) {
        hideKeyboard(productAddToOrderButton)
        sharedViewModel.confirmCurrentProductOrder(productOrder)
        findNavController().popBackStack(R.id.cartFragment, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_product_order, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.productPriceReferenceItem -> {
                hideKeyboard(productAddToOrderButton)
                findNavController()
                    .navigate(ProductOrderFragmentDirections.actionProductOrderFragmentToPriceReferenceFragment())

                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        viewModel.confirmAbandonProductOrder()
    }

    override fun onAbandonProductOrder() = viewModel.abandonProductOrder()

    override fun onNothingSelected(parent: AdapterView<*>?) { }
}