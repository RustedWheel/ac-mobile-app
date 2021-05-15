package com.makeitez.acsalesapp.screens.announcement.general

import android.app.Application
import com.makeitez.acsalesapp.models.GeneralAnnouncement
import com.makeitez.acsalesapp.models.enums.AnnouncementType
import com.makeitez.acsalesapp.screens.announcement.BaseAnnouncementsViewModel
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class GeneralAnnouncementsViewModel(application: Application) : BaseAnnouncementsViewModel(application) {

    val announcements = Realm.getDefaultInstance().where<GeneralAnnouncement>()
        .sort("date", Sort.DESCENDING).findAll().asLiveData()

    init {
        contentState.addSource(announcements) { checkContentState() }
        contentState.addSource(inProgress) { checkContentState() }

        fetchAnnouncements()
    }

    fun onGeneralAnnouncementAdded() {
    }

    fun onDeleteAnnouncementConfirmed(announcementId: String) {
        deleteAnnouncement(AnnouncementType.General, announcementId)
    }

    private fun checkContentState() {
        contentState.value = when {
            announcements.value?.isNotEmpty() == true -> {
                ListContentState.NotEmpty
            }
            inProgress.value != true -> {
                ListContentState.EmptyData
            }
            else -> {
                ListContentState.Empty
            }
        }
    }
}