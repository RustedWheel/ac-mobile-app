package com.makeitez.acsalesapp.screens.announcement.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.GeneralAnnouncement
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_general_announcement_list.view.*

class GeneralAnnouncementViewHolder(listItemView: View, isEditing: Boolean, listener: Listener) :
    RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onGeneralAnnouncementDelete(announcementId: String, description: String)
    }

    private val generalAnnouncementDeleteButton = itemView.generalAnnouncementDeleteButton
    private val generalAnnouncementDateText = itemView.generalAnnouncementDateText

    private val generalAnnouncementTitleText = itemView.generalAnnouncementTitleText
    private val generalAnnouncementContentText = itemView.generalAnnouncementContentText

    private var announcement: GeneralAnnouncement? = null

    init {
        generalAnnouncementDeleteButton.isVisible = isEditing
        generalAnnouncementDeleteButton.setOnClickListener {
            announcement?.let {announcement ->
                listener.onGeneralAnnouncementDelete(
                    announcement.id, announcement.title
                )
            }
        }
    }

    fun bind(announcement: GeneralAnnouncement) {
        this.announcement = announcement

        generalAnnouncementDateText.text = FormattingHelper.formatDate(announcement.date)
        generalAnnouncementTitleText.text = announcement.title
        generalAnnouncementContentText.text = announcement.message
    }

    companion object {
        fun newInstance(parent: ViewGroup, isEditing: Boolean, listener: Listener) =
            GeneralAnnouncementViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_general_announcement_list, parent, false),
                isEditing,
                listener
            )
    }
}