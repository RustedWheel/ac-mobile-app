package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import java.util.*

open class GeneralAnnouncement: RealmObject() {
    @PrimaryKey
    @field:Json(name = "id")
    var id: String = ""; private set

    @field:Json(name = "date")
    var date: Date = Date(); private set

    @field:Json(name = "title")
    var title: String = ""; private set

    @field:Json(name = "message")
    var message: String = ""; private set

    var lastUpdated: Long = System.currentTimeMillis()

    companion object {
        fun updateFromAnnouncementList(untrackedAnnouncements: List<GeneralAnnouncement>, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedAnnouncements.forEach {
                updateFromUntracked(it, lastUpdatedTime, realm)
            }
            deleteAllObsolete(lastUpdatedTime, realm)
        }

        private fun updateFromUntracked(untrackedAnnouncement: GeneralAnnouncement, updateTime: Long, realm: Realm): GeneralAnnouncement {
            val announcement = realm.copyToRealmOrUpdate(untrackedAnnouncement)
            announcement.lastUpdated = updateTime
            return announcement
        }

        fun deleteAllObsolete(timeInMillis: Long, realm: Realm) {
            realm.where<GeneralAnnouncement>().lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }

    }
}