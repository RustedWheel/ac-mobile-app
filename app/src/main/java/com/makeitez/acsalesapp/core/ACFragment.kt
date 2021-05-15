package com.makeitez.acsalesapp.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.listener.ITableViewListener
import com.makeitez.acsalesapp.utils.extensions.Callback
import com.makeitez.acsalesapp.utils.extensions.showProgressDialog
import com.makeitez.acsalesapp.utils.extensions.showSnackbar

abstract class ACFragment(@LayoutRes private val layoutResId: Int) : Fragment(), ITableViewListener {

    abstract class WithViewModel<T : ACViewModel>(layoutResId: Int, private val modelClass: Class<T>, isViewModelFromActivity: Boolean = false) : ACFragment(layoutResId) {

        protected val viewModel: T by lazy {
            if (isViewModelFromActivity) {
                ViewModelProvider(requireActivity()).get(modelClass)
            } else {
                ViewModelProvider(this).get(modelClass)
            }
        }

        protected open val handlesOwnLoadingIndicator
            get() = false

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel.message.observe(viewLifecycleOwner, Observer { showErrorMessage(it) })
            if (!handlesOwnLoadingIndicator) {
                viewModel.inProgress.observe(viewLifecycleOwner, Observer { showProgressDialog(it) })
            }
        }

        protected open fun showErrorMessage(alertMessage: ErrorMessage?) {
            alertMessage?.let {
                view?.showSnackbar(it.message)
                viewModel.clearMessage()
            }
        }
    }

    abstract class WithDataBinding<D : ViewDataBinding, T : ACViewModel>(private val layoutResId: Int, modelClass: Class<T>, isViewModelFromActivity: Boolean = false)
        : WithViewModel<T>(layoutResId, modelClass, isViewModelFromActivity) {

        protected lateinit var binding: D

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
            binding.lifecycleOwner = viewLifecycleOwner

            onBindDataToCreatedView(binding)

            return binding.root
        }

        abstract fun onBindDataToCreatedView(binding: D)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
    }

    protected open fun onBackPressed() {
        if (!findNavController().navigateUp()) {
            requireActivity().finish()
        }
    }

    protected fun requireAcActivity(): ACActivity = requireActivity() as ACActivity

    protected fun setActionBar(toolbar: View, @StringRes titleRes: Int, navigationAction: Callback = this::onBackPressed) {
        setActionBar(toolbar, getString(titleRes), navigationAction)
    }

    protected fun setActionBar(toolbar: View, title: String = "", navigationAction: Callback = this::onBackPressed) {
        (toolbar as? Toolbar)?.let {
            it.title = title
            requireAcActivity().setSupportActionBar(it)
            it.setNavigationOnClickListener { navigationAction() }
        }
    }

    protected fun hideActionBar() {
        requireAcActivity().setSupportActionBar(null)
    }

    // TableView overrides
    override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) = Unit
    override fun onCellDoubleClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) = Unit
    override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) = Unit
    override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) = Unit
    override fun onColumnHeaderDoubleClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) = Unit
    override fun onColumnHeaderLongPressed(columnHeaderView: RecyclerView.ViewHolder, column: Int) = Unit
    override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) = Unit
    override fun onRowHeaderDoubleClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) = Unit
    override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) = Unit
}