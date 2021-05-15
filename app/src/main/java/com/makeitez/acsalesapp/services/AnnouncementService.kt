package com.makeitez.acsalesapp.services

import com.makeitez.acsalesapp.models.GeneralAnnouncement
import com.makeitez.acsalesapp.models.NewProductAnnouncement
import com.makeitez.acsalesapp.models.api.AnnouncementListResponseData
import com.makeitez.acsalesapp.models.api.request.CreateAnnouncementRequestData
import com.makeitez.acsalesapp.models.enums.AnnouncementType
import io.realm.Realm
import io.realm.kotlin.where

class AnnouncementService(private val networkService: NetworkService) {
    suspend fun fetchAnnouncements() {
        val response = networkService.call {
            it.getAnnouncementList()
        }
        response.data?.let {
            saveAnnouncementListResponseData(it)
        }
    }

    private fun saveAnnouncementListResponseData(announcementListResponse: AnnouncementListResponseData) {
        Realm.getDefaultInstance().executeTransaction { realm ->
            GeneralAnnouncement.updateFromAnnouncementList(announcementListResponse.generalAnnouncements, realm)
            NewProductAnnouncement.updateFromAnnouncementList(announcementListResponse.newProductAnnouncements, realm)
        }
    }

    suspend fun createGeneralAnnouncement(title: String, message: String) {
        val response = networkService.call {
            it.createAnnouncement(CreateAnnouncementRequestData
                .createGeneralRequestData(title, message, true))
        }
        response.data?.let {
            saveAnnouncementListResponseData(it)
        }
    }

    suspend fun createNewProductAnnouncement(itemCode: String, remarks: String) {
        val response = networkService.call {
            it.createAnnouncement(CreateAnnouncementRequestData
                .createNewProductRequestData(itemCode, remarks, true))
        }
        response.data?.let {
            saveAnnouncementListResponseData(it)
        }
    }

    suspend fun deleteAnnouncement(announcementType: AnnouncementType, announcementId: String) {
        networkService.callBase {
            it.deleteAnnouncement(announcementId)
        }

        Realm.getDefaultInstance().executeTransaction { realm ->
            when(announcementType) {
                AnnouncementType.General -> {
                    realm.where<GeneralAnnouncement>()
                        .equalTo("id", announcementId).findAll().deleteAllFromRealm()
                }
                AnnouncementType.NewProduct -> {
                    realm.where<NewProductAnnouncement>()
                        .equalTo("id", announcementId).findAll().deleteAllFromRealm()
                }
            }

        }
    }
}