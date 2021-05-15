package com.makeitez.acsalesapp.screens.announcement

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.AnnouncementService
import com.makeitez.acsalesapp.models.enums.AnnouncementType
import com.makeitez.acsalesapp.utils.ListContentState
import kotlinx.coroutines.launch

open class BaseAnnouncementsViewModel(application: Application) : ACViewModel(application) {
    val contentState = MediatorLiveData<ListContentState>()
    val deleteAnnouncementProgress = MutableLiveData<Boolean>()

    private lateinit var announcementService: AnnouncementService

    fun init(announcementService: AnnouncementService) {
        this.announcementService = announcementService
    }

    fun fetchAnnouncements() {
        withProgress {
            try {
                announcementService.fetchAnnouncements()
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    protected fun deleteAnnouncement(announcementType: AnnouncementType, announcementId: String) {
        viewModelScope.launch {
            deleteAnnouncementProgress.value = true
            try {
                announcementService.deleteAnnouncement(announcementType, announcementId)
            } catch (e: Exception) {
                handleGenericException(e)
            }
            deleteAnnouncementProgress.value = false
        }
    }
}