package com.makeitez.acsalesapp.models.dao

import com.makeitez.acsalesapp.models.Product
import io.realm.Realm
import io.realm.kotlin.where

interface ProductDAO {
    fun get(productCode: String): Product?
}

class RealmProductDAO(private val realm: Realm): ProductDAO {

    override fun get(productCode: String): Product? = realm.where<Product>().equalTo("itemCode", productCode).findFirst()

}