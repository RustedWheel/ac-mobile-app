package com.makeitez.acsalesapp.models.dao

import com.makeitez.acsalesapp.models.Customer
import io.realm.Realm
import io.realm.kotlin.where

interface CustomerDAO {
    fun get(accountNumber: String): Customer?
}

class RealmCustomerDAO(private val realm: Realm): CustomerDAO {

    override fun get(accountNumber: String): Customer? = realm.where<Customer>().equalTo("accountNumber", accountNumber).findFirst()

}