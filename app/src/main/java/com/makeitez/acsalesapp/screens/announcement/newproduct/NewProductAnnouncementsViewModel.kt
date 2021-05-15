package com.makeitez.acsalesapp.screens.announcement.newproduct

import android.app.Application
import com.makeitez.acsalesapp.models.NewProductAnnouncement
import com.makeitez.acsalesapp.models.enums.AnnouncementType
import com.makeitez.acsalesapp.screens.announcement.BaseAnnouncementsViewModel
import com.makeitez.acsalesapp.utils.ListContentState
import com.makeitez.acsalesapp.utils.extensions.asLiveData
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

class NewProductAnnouncementsViewModel(application: Application) : BaseAnnouncementsViewModel(application) {

    val announcements = Realm.getDefaultInstance().where<NewProductAnnouncement>()
        .sort("date", Sort.DESCENDING).findAll().asLiveData()

    init {
        contentState.addSource(announcements) { checkContentState() }
        contentState.addSource(inProgress) { checkContentState() }

        fetchAnnouncements()
    }

    fun onNewProductAnnouncementAdded() {
    }

    fun onDeleteAnnouncementConfirmed(announcementId: String) {
        deleteAnnouncement(AnnouncementType.NewProduct, announcementId)
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