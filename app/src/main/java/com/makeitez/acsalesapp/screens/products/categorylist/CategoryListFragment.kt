package com.makeitez.acsalesapp.screens.products.categorylist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACFragment
import com.makeitez.acsalesapp.models.ProductCategory
import com.makeitez.acsalesapp.utils.ListContentState
import kotlinx.android.synthetic.main.fragment_search_list.*

class CategoryListFragment : ACFragment.WithViewModel<CategoryListViewModel>(R.layout.fragment_search_list, CategoryListViewModel::class.java), CategoryViewHolder.Listener {

    private val categoryListAdapter: CategoryListAdapter by lazy {
        CategoryListAdapter(this)
    }

    override val handlesOwnLoadingIndicator
        get() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBar(searchToolbar, R.string.select_category_title)
        viewModel.init(ACService.productService)
        viewModel.categoryList.observe(viewLifecycleOwner, Observer { syncData(it) })
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.contentState.observe(viewLifecycleOwner, Observer { syncContentState(it) })
        viewModel.onCategorySelectedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                goToProductList(it)
            }
        })

        searchListRecycler.adapter = categoryListAdapter

        searchInputText.doOnTextChanged { searchTerm, _, _, _ ->
            viewModel.onSearchTermChanged(searchTerm.toString())
        }

        searchListRefreshLayout.setOnRefreshListener {
            viewModel.fetchCategoryList()
        }
    }

    override fun onCategoryClick(category: ProductCategory) {
        viewModel.onCategoryClick(category)
    }

    private fun syncData(data: List<ProductCategory>) {
        categoryListAdapter.setList(data)
    }

    private fun goToProductList(categoryType: String) {
        findNavController()
            .navigate(CategoryListFragmentDirections.actionCategoryListFragmentToProductListFragment(categoryType))
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