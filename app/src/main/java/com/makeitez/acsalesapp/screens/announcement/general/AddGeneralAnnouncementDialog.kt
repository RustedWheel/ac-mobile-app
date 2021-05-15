package com.makeitez.acsalesapp.screens.announcement.general

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.makeitez.acsalesapp.ACService
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import com.makeitez.acsalesapp.utils.extensions.hideKeyboard
import kotlinx.android.synthetic.main.dialog_add_general_announcement.*

class AddGeneralAnnouncementDialog : ACDialogFragment.WithViewModel<AddGeneralAnnouncementViewModel>(R.layout.dialog_add_general_announcement, AddGeneralAnnouncementViewModel::class.java) {

    interface Listener {
        fun onGeneralAnnouncementAdded()
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        viewModel.init(ACService.announcementService)
        viewModel.inProgress.observe(viewLifecycleOwner, Observer { syncProgress(it) })
        viewModel.isConfirmButtonEnabled.observe(viewLifecycleOwner, Observer {
            addAnnouncementConfirmButton.isEnabled = it
        })
        viewModel.onAnnouncementAddedEvent.observe(viewLifecycleOwner, Observer { event ->
            event.handleIfNotHandled {
                listener?.onGeneralAnnouncementAdded()
                dismiss()
            }
        })

        addAnnouncementTitleInputText.doOnTextChanged { title, _, _, _ ->
            viewModel.onTitleChange(title.toString())
        }
        addAnnouncementMessageInputText.doOnTextChanged { message, _, _, _ ->
            viewModel.onMessageChange(message.toString())
        }
        addAnnouncementConfirmButton.setOnClickListener {
            hideKeyboard(addAnnouncementConfirmButton)
            viewModel.addAnnouncement()
        }
        addAnnouncementCancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun syncProgress(inProgress: Boolean) {
        addAnnouncementContentLayout.isVisible = !inProgress
        addAnnouncementLoadingProgress.isVisible = inProgress
    }
}