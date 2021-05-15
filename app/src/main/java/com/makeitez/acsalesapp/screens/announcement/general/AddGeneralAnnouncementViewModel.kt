package com.makeitez.acsalesapp.screens.announcement.general

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.services.AnnouncementService
import com.makeitez.acsalesapp.utils.Event

class AddGeneralAnnouncementViewModel(application: Application): ACViewModel(application) {

    private lateinit var announcementService: AnnouncementService
    private var announcementTitle: String = ""
    private var announcementMessage: String = ""

    val isConfirmButtonEnabled = MutableLiveData<Boolean>(false)
    val onAnnouncementAddedEvent = MutableLiveData<Event<Unit>>()

    fun init(announcementService: AnnouncementService) {
        this.announcementService = announcementService
    }

    fun addAnnouncement() {
        withProgress {
            try {
                announcementService.createGeneralAnnouncement(announcementTitle, announcementMessage)
                onAnnouncementAddedEvent.value = Event(Unit)
            } catch (e: Exception) {
                handleGenericException(e)
            }
        }
    }

    fun onTitleChange(title: String) {
        announcementTitle = title

        isConfirmButtonEnabled.value = announcementTitle.isNotBlank()
    }

    fun onMessageChange(message: String) {
        announcementMessage = message
    }
}