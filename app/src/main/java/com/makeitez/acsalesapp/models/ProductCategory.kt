package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where

open class ProductCategory : RealmObject() {

    @PrimaryKey
    @field:Json(name = "type")
    var type: String = ""
        private set

    @field:Json(name = "description")
    var description: String = ""
        private set

    var lastUpdated: Long = System.currentTimeMillis()

    companion object {

        fun fromId(type: String, realm: Realm): ProductCategory {
            val category = realm.where<ProductCategory>().equalTo("type", type).findFirst()
            return category ?: realm.createObject(ProductCategory::class.java, type)
        }

        fun updateFromCategoryList(untrackedCategories: List<ProductCategory>, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedCategories.forEach {
                updateFromUntracked(it, lastUpdatedTime, realm)
            }
            deleteAllObsolete(lastUpdatedTime, realm)
        }

        fun updateFromUntracked(untrackedCategory: ProductCategory, updateTime: Long = System.currentTimeMillis(), realm: Realm): ProductCategory {
            val category = realm.copyToRealmOrUpdate(untrackedCategory)
            category.lastUpdated = updateTime
            return category
        }

        fun deleteAllObsolete(timeInMillis: Long, realm: Realm) {
            realm.where<ProductCategory>().lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }
    }
}