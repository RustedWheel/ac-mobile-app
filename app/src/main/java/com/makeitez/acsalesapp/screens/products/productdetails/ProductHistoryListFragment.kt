package com.makeitez.acsalesapp.screens.products.productdetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.ProductHistory
import com.makeitez.acsalesapp.utils.ListContentState
import kotlinx.android.synthetic.main.fragment_search_list.*

class ProductHistoryListFragment : ACFragment.WithViewModel<ProductHistoryListViewModel>(R.layout.fragment_search_list, ProductHistoryListViewModel::class.java), ProductHistoryViewHolder.Listener {

    private val args: ProductHistoryListFragmentArgs by navArgs()

    private val productHistoryAdapter: ProductHistoryAdapter by lazy {
        ProductHistoryAdapter(this)
    }

    override val handlesOwnLoadingIndicator
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(args.productCode, ACService.productService)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(searchToolbar, R.string.product_history_title)
        viewModel.productHistoryList.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.onProductHistorySelectedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled { selectedProductHistorySummary ->
                findNavController().navigate(
                    ProductHistoryListFragmentDirections.actionProductHistoryListFragmentToProductHistoryDetailsFragment(
                        selectedProductHistorySummary.productHistoryId,
                        selectedProductHistorySummary.docNo,
                        selectedProductHistorySummary.productCode
                    )
                )
            }
        })

        searchListRecycler.adapter = productHistoryAdapter

        searchInputText.doOnTextChanged { searchText, _, _, _ ->
            viewModel.onSearchTermChanged(searchText.toString())
        }

        searchListRefreshLayout.setOnRefreshListener {
            viewModel.fetchProductHistory()
        }
    }

    override fun onProductHistoryClicked(productHistory: ProductHistory) {
        viewModel.onProductHistoryClick(productHistory)
    }

    private fun syncData(productHistoryList: List<ProductHistory>) {
        productHistoryAdapter.setList(productHistoryList)
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