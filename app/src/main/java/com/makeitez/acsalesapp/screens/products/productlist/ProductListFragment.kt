package com.makeitez.acsalesapp.screens.products.productlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.screens.salesorder.CreateSalesOrderViewModel
import com.makeitez.acsalesapp.screens.salesorder.SalesOrderActivity
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import kotlinx.android.synthetic.main.fragment_search_list.*


class ProductListFragment : ACFragment.WithViewModel<ProductListViewModel>(R.layout.fragment_search_list, ProductListViewModel::class.java), ProductViewHolder.Listener {

    private val isSalesOrderFlow by lazy {
        requireAcActivity() is SalesOrderActivity
    }

    private val sharedViewModel: CreateSalesOrderViewModel by activityViewModels()

    private val args: ProductListFragmentArgs by navArgs()

    private val productListAdapter: ProductListAdapter by lazy {
        ProductListAdapter(this)
    }

    override val handlesOwnLoadingIndicator
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(
            if (isSalesOrderFlow) sharedViewModel.customer?.accountNumber else null,
            args.categoryType,
            isSalesOrderFlow,
            ACService.productService
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(searchToolbar, R.string.search_product_title)

        viewModel.productList.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.productDetailsProgress.observe(viewLifecycleOwner, Observer { showProgressDialog(it) })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.onProductSelectedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                if (isSalesOrderFlow) {
                    goToProductOrder(it)
                } else {
                    goToProductDetails(it)
                }
            }
        })

        sharedViewModel.onViewExistingProductOrderEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                goToExistingProductOrder(it)
            }
        })

        searchListRecycler.adapter = productListAdapter

        searchInputText.doOnTextChanged { searchText, _, _, _ ->
            viewModel.onSearchTermChanged(searchText.toString())
        }

        searchListRefreshLayout.setOnRefreshListener {
            viewModel.fetchProductList()
        }
    }

    override fun onProductClick(product: Product) {
        if(isSalesOrderFlow && sharedViewModel.checkProductOrderExists(product)) {
            sharedViewModel.viewExistingProductOrder(product)
        } else {
            viewModel.onProductClick(product)
        }
    }

    private fun syncData(data: List<Product>) {
        productListAdapter.setList(data)
    }

    private fun goToProductOrder(product: Product) {
        sharedViewModel.createNewProductOrder(product)
        findNavController()
            .navigate(ProductListFragmentDirections.actionProductListFragmentToProductOrderFragment())
    }

    private fun goToProductDetails(product: Product) {
        findNavController()
            .navigate(ProductListFragmentDirections.actionProductListFragmentToProductDetailsFragment(product.itemCode))
    }

    private fun goToExistingProductOrder(productOrder: ProductOrder) {
        sharedViewModel.editProductOrder(productOrder)
        findNavController()
            .navigate(ProductListFragmentDirections.actionProductListFragmentToProductOrderFragment())
    }

    private fun syncProgress(inProgress: Boolean) {
        searchListRefreshLayout.isRefreshing = inProgress
    }

    private fun syncContentState(state: ListContentState) {
        searchListRecycler.isVisible = state == ListContentState.NotEmpty
        searchNoResultText.isVisible = state == ListContentState.EmptySearch
        searchNoDataText.isVisible = state == ListContentState.EmptyData
    }
}