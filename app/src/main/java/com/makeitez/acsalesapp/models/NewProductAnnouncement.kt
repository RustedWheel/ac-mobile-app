package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import java.util.*

open class NewProductAnnouncement: RealmObject() {
    @PrimaryKey
    @field:Json(name = "id")
    var id: String = ""; private set

    @field:Json(name = "date")
    var date: Date = Date(); private set

    @field:Json(name = "productDetail")
    var productDetails: Product? = null; private set

    @field:Json(name = "remark")
    var remarks: String? = null; private set

    var lastUpdated: Long = System.currentTimeMillis()

    companion object {
        fun updateFromAnnouncementList(untrackedAnnouncements: List<NewProductAnnouncement>, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedAnnouncements.forEach {
                updateFromUntracked(it, lastUpdatedTime, realm)
            }
            deleteAllObsolete(lastUpdatedTime, realm)
        }

        private fun updateFromUntracked(untrackedAnnouncement: NewProductAnnouncement, updateTime: Long, realm: Realm): NewProductAnnouncement {
            val announcement = realm.copyToRealmOrUpdate(untrackedAnnouncement)
            announcement.lastUpdated = updateTime
            return announcement
        }

        fun deleteAllObsolete(timeInMillis: Long, realm: Realm) {
            realm.where<NewProductAnnouncement>().lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }
    }
}