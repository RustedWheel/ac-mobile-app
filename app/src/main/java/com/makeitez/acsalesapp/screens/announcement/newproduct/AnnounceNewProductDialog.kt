package com.makeitez.acsalesapp.screens.announcement.newproduct

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import com.makeitez.acsalesapp.utils.extensions.hideKeyboard
import kotlinx.android.synthetic.main.dialog_announce_new_product.*


class AnnounceNewProductDialog : ACDialogFragment.WithViewModel<AnnounceNewProductViewModel>(
    R.layout.dialog_announce_new_product, AnnounceNewProductViewModel::class.java
) {

    interface Listener {
        fun onNewProductAnnouncementAdded()
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        viewModel.init(ACService.productService, ACService.announcementService)
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.isSearchButtonEnabled.observe(viewLifecycleOwner, Observer {
            announceNewProductSearchButton.isEnabled = it
        })
        viewModel.verifiedProduct.observe(viewLifecycleOwner, Observer { syncVerifiedProduct(it) })
        viewModel.onAnnouncementAddedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                listener?.onNewProductAnnouncementAdded()
                dismiss()
            }
        })

        announceNewProductCodeInputText.doOnTextChanged { code, _, _, _ ->
            viewModel.onProductCodeChange(code.toString())
        }
        announceNewProductRemarksInputText.doOnTextChanged { remarks, _, _, _ ->
            viewModel.onProductRemarksChange(remarks.toString())
        }
        announceNewProductSearchButton.setOnClickListener {
            hideKeyboard(announceNewProductSearchButton)
            viewModel.verifyProductCode()
        }
        announceNewProductCancelCheckButton.setOnClickListener { dismiss() }

        announceNewProductAnnounceButton.setOnClickListener {
            viewModel.confirmNewProductAnnouncement()
        }
        announceNewProductCancelAnnouncementButton.setOnClickListener { dismiss() }
    }

    private fun syncVerifiedProduct(verifiedProduct: Product) {
        announceNewProductCheckLayout.isVisible = false
        announceNewProductConfirmLayout.isVisible = true

        announceNewProductVerifiedCodeText.text = getString(
            R.string.announce_new_product_dialog_verified_code_label,
            verifiedProduct.itemCode
        )
        announceNewProductVerifiedNameText.text = getString(
            R.string.announce_new_product_dialog_verified_name_label,
            verifiedProduct.description
        )
        announceNewProductRemarksInputText.text.let { remarks ->
            if(!remarks.isNullOrEmpty()) {
                announceNewProductVerifiedRemarksText.text =
                    getString(R.string.announce_new_product_dialog_verified_remarks_label, remarks)
            } else {
                announceNewProductVerifiedRemarksText.text =
                    getString(R.string.announce_new_product_dialog_verified_empty_remarks)
            }
        }
    }

    private fun syncProgress(inProgress: Boolean) {
        announceNewProductContentLayout.isVisible = !inProgress
        announceNewProductLoadingProgress.isVisible = inProgress
    }
}