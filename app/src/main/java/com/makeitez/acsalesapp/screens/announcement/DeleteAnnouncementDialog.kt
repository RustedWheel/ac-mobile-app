package com.makeitez.acsalesapp.screens.announcement

import android.os.Bundle
import android.view.View
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.components.ACDialogFragment
import com.makeitez.acsalesapp.utils.extensions.getListeningFragment
import kotlinx.android.synthetic.main.dialog_confirmation.*

class DeleteAnnouncementDialog : ACDialogFragment(R.layout.dialog_confirmation) {
    interface Listener {
        fun onDeleteAnnouncementConfirmed(announcementId: String)
    }

    private val listener: Listener?
        get() = getListeningFragment<Listener>()


    private val announcementId: String by lazy {
        arguments?.getString(ANNOUNCEMENT_ID_KEY) ?: ""
    }

    private val announcementName: String by lazy {
        arguments?.getString(ANNOUNCEMENT_NAME_KEY) ?: ""
    }


    fun withData(announcementId: String, announcementName: String): DeleteAnnouncementDialog {
        val args = arguments ?: Bundle()
        args.putString(ANNOUNCEMENT_ID_KEY, announcementId)
        args.putString(ANNOUNCEMENT_NAME_KEY, announcementName)
        arguments = args
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmationTitle.setText(R.string.delete_announcement_dialog_title)
        confirmationMessage.text = getString(R.string.delete_announcement_dialog_message, announcementName)

        confirmationCancelButton.setOnClickListener {
            dismiss()
        }

        confirmationConfirmButton.setOnClickListener {
            listener?.onDeleteAnnouncementConfirmed(announcementId)
            dismiss()
        }
    }

    companion object {
        private const val ANNOUNCEMENT_ID_KEY = "announcementId"
        private const val ANNOUNCEMENT_NAME_KEY = "announcementName"
    }
}