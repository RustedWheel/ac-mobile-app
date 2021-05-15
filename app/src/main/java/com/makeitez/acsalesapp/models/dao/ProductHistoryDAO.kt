package com.makeitez.acsalesapp.models.dao

import com.makeitez.acsalesapp.models.ProductHistory
import io.realm.Realm
import io.realm.kotlin.where

interface ProductHistoryDAO {
    fun get(productHistoryId: String): ProductHistory?
}

class RealmProductHistoryDAO(private val realm: Realm): ProductHistoryDAO {
    override fun get(productHistoryId: String): ProductHistory? = realm.where<ProductHistory>().equalTo("uuid", productHistoryId).findFirst()
}