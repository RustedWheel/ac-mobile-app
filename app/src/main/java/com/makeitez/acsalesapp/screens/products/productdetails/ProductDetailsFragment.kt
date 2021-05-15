package com.makeitez.acsalesapp.screens.products.productdetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.utils.extensions.setNonEmptyText
import com.makeitez.acsalesapp.utils.extensions.setToolbarTitle
import com.makeitez.acsalesapp.utils.helper.FileSystemHelper
import kotlinx.android.synthetic.main.component_generic_try_again.*
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_product_details.*

class ProductDetailsFragment: ACFragment.WithViewModel<ProductDetailsViewModel>(R.layout.fragment_product_details, ProductDetailsViewModel::class.java) {

    private val args: ProductDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(toolbar)
        setHasOptionsMenu(true)
        viewModel.init(args.productCode, ACService.productService)
        viewModel.product.observe(viewLifecycleOwner, Observer {
            syncProductDetails(it)
        })
        viewModel.showProductDetails.observe(viewLifecycleOwner, Observer {
            productDetailsErrorLayout.isVisible = !it
            productDetailsLayout.isVisible = it
            requireAcActivity().invalidateOptionsMenu()
        })
        viewModel.downloadedPdfFileUri.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { productDetailUri ->
                FileSystemHelper.openPdf(this, productDetailUri)
            }
        })
        genericErrorRefreshButton.setOnClickListener {
            viewModel.getProductDetails()
        }
        productDetailsBackToHomeButton.setOnClickListener {
            activity?.finish()
        }
    }

    private fun syncProductDetails(product: Product) {
        with(product) {
            setToolbarTitle(itemCode)
            context?.let {
                Glide.with(it).load(imageUrl).placeholder(R.drawable.app_ac_logo).into(productDetailsProductImage)
            }
            productDetailsProductName.setNonEmptyText(description)
            getDefaultUom().let {  uom ->
                productDetailsUom.setNonEmptyText(uom.description)
                productDetailsCartonSize.setNonEmptyText(uom.cartonSize)
                productDetailsM3.setNonEmptyText(uom.m3)
                productDetailsNetWeight.setNonEmptyText("${uom.weight}${uom.weightUom}")
                productDetailsPackingUnit.setNonEmptyText(uom.packagingUnit)
                productDetailsBarcode.setNonEmptyText(uom.barcode)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_product_details, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.setGroupVisible(R.id.productDetailsOptionsGroup, viewModel.showProductDetails.value ?: false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.productDetailsProductHistoryItem -> {
                findNavController().navigate(ProductDetailsFragmentDirections.actionProductDetailsFragmentToProductHistoryListFragment(args.productCode))
                true
            }
            R.id.productDetailsShareItem -> {
                attemptToDownloadProductInfo()
                true
            }
            else -> false
        }
    }

    private fun attemptToDownloadProductInfo() {
        val context = context?.applicationContext ?: return
        val hasWritePermission = FileSystemHelper.isWriteAllowed(context)
        if (hasWritePermission) {
            viewModel.downloadProductDetailPdf(context)
        } else {
            FileSystemHelper.requestWritePermission(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (FileSystemHelper.checkWritePermissionGranted(requestCode, permissions, grantResults)) {
            val context = context?.applicationContext ?: return
            viewModel.downloadProductDetailPdf(context)
        }
    }
}