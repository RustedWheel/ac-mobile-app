package com.makeitez.acsalesapp.screens.products.pricereference

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_price_reference.*

class PriceReferenceFragment : ACFragment.WithViewModel<PriceReferenceViewModel>(
    R.layout.fragment_price_reference,
    PriceReferenceViewModel::class.java
) {

    private val sharedViewModel: CreateSalesOrderViewModel by activityViewModels()

    private val uomMinPriceListAdapter: UomMinPriceListAdapter by lazy {
        UomMinPriceListAdapter()
    }

    private val priceHistoryListAdapter: PriceHistoryListAdapter by lazy {
        PriceHistoryListAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar, R.string.price_reference_title)

        with(sharedViewModel) {
            currency?.let {
                viewModel.setPriceReferenceData(it, currentProductOrder)
            }
        }

        viewModel.uomList.observe(viewLifecycleOwner, Observer { (list, currency) ->
            uomMinPriceListAdapter.setList(list, currency)
        })

        viewModel.priceHistoryList.observe(viewLifecycleOwner, Observer { priceHistoryListAdapter.setList(it) })

        productUomMinPriceRecycler.adapter = uomMinPriceListAdapter
        productPriceHistoryRecycler.adapter = priceHistoryListAdapter
    }
}