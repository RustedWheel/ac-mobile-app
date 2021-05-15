package com.makeitez.acsalesapp.screens.products.productdetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.extensions.setToolbarTitle
import com.makeitez.acsalesapp.utils.extensions.toNdpString
import com.makeitez.acsalesapp.utils.extensions.toStringRemoveTrailingZeroes
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.component_generic_try_again.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_product_history_details.*

class ProductHistoryDetailsFragment: ACFragment.WithViewModel<ProductHistoryDetailsViewModel>(R.layout.fragment_product_history_details, ProductHistoryDetailsViewModel::class.java) {

    private val args: ProductHistoryDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)
        viewModel.init(args.productHistoryId, args.docNo, args.productCode, ACService.productService)
        viewModel.product.observe(viewLifecycleOwner, Observer {
            syncProductInfo(it)
        })
        viewModel.productHistory.observe(viewLifecycleOwner, Observer {
            syncProductHistory(it)
        })
        viewModel.showProductHistoryDetails.observe(viewLifecycleOwner, Observer { showDetails->
            productHistoryDetailsErrorLayout.isVisible = !showDetails
            productHistoryDetailsContent.isVisible = showDetails
        })
        genericErrorRefreshButton.setOnClickListener {
            viewModel.getProductHistoryDetails()
        }
    }

    private fun syncProductInfo(product: Product) {
        with(product) {
            setToolbarTitle(itemCode)
            productHistoryDetailsProductNameText.text = description
        }
    }

    private fun syncProductHistory(productHistory: ProductHistory) {
        with(productHistory) {
            val hasNewPrice = newPrice != null
            productHistoryDetailsDocNoText.text = docNo
            productHistoryDetailsTimeText.text = FormattingHelper.formatDate(docDate, FormattingHelper.datetimeFormatLong)
            productHistoryDetailsUomText.setText(getString(R.string.product_order_uom_desc, uom, rate.toStringRemoveTrailingZeroes(Config.UNIT_RATE_DPS)))
            productHistoryDetailsQuantityText.setText(quantity.toString())
            productHistoryDetailsUnitPriceDescription.text = getString(R.string.product_order_unit_price, currencyCode)
            productHistoryDetailsUnitPriceText.setText(unitPrice.toNdpString(Config.UNIT_PRICE_DPS))
            productHistoryDetailsPriceChangeDescription.setText(if (hasNewPrice) R.string.product_order_new_price else R.string.product_order_discount)
            productHistoryDetailsPriceChangeText.setText(
                if (hasNewPrice) {
                    (newPrice ?: 0.0).toNdpString(Config.UNIT_PRICE_DPS)
                } else {
                    discount.toNdpString(Config.DISCOUNT_DPS)
                }
            )
            productHistoryDetailsFOCText.setText(foc.toString())
            productHistoryDetailsDestinationText.setText(destination)
            productHistoryDetailsDeliveryDateText.setText(FormattingHelper.formatDate(deliveryDate))
            productHistoryDetailsRemarksText.setText(remarks)
        }
    }

}