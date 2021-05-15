package com.makeitez.acsalesapp.models

import com.makeitez.acsalesapp.models.api.ProductListResponseData
import com.makeitez.acsalesapp.models.enums.EntityStatus
import com.squareup.moshi.Json
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where

open class Product : RealmObject() {

    @PrimaryKey
    @field:Json(name = "itemCode")
    var itemCode: String = ""; private set

    @field:Json(name = "description")
    var description: String = ""; private set

    @field:Json(name = "itemGroup")
    var itemGroup: String = ""; private set

    @field:Json(name = "itemType")
    var itemType: String = ""; private set

    @field:Json(name = "hasQuotation")
    var hasQuotationOnUom: Boolean = false; private set

    @field:Json(name = "defaultUom")
    var defaultUomDescription: String = ""; private set

    @field:Json(name = "uomDetailList")
    var uomDetailList:  RealmList<Uom> = RealmList(); private set

    @field:Json(name = "priceHistory")
    var priceHistory: RealmList<PriceHistory> = RealmList(); private set

    @field:Json(name = "status")
    var status: Int = 0; private set

    @field:Json(name = "imageUrl")
    var imageUrl: String = ""; private set

    var lastUpdated: Long = System.currentTimeMillis()

    val productStatus: EntityStatus
        get() = EntityStatus.valueOfOrInactive(status)

    fun getUom(uomSelection: Int): Uom {
        return uomDetailList.getOrNull(uomSelection) ?: Uom()
    }

    fun getUomSelection(uom: Uom?): Int {
        return uomDetailList.indexOfFirst { listUom -> listUom.description == (uom?.description ?: "") }
    }

    fun getDefaultUom(): Uom = uomDetailList.find { uom -> uom.description == defaultUomDescription }
            ?: getUom(0)

    fun getDefaultUomSelection(): Int {
        return getUomSelection(getDefaultUom())
    }

    fun isActive() = productStatus == EntityStatus.Active

    companion object {
        const val ALL_ITEM_TYPE = "All"

        fun fromId(itemCode: String, realm: Realm): Product {
            val product = realm.where<Product>().equalTo("itemCode", itemCode).findFirst()
            return product ?: realm.createObject(Product::class.java, itemCode)
        }

        fun updateFromProductList(untrackedProducts: List<ProductListResponseData>, category: String, realm: Realm) {
            val lastUpdatedTime = System.currentTimeMillis()
            untrackedProducts.forEach {
                updateFromUntracked(it, lastUpdatedTime, realm)
            }
            deleteObsoleteByCategory(category, lastUpdatedTime, realm)
        }

        fun updateFromUntracked(untrackedProduct: ProductListResponseData, updateTime: Long, realm: Realm): Product =
            fromId(untrackedProduct.itemCode, realm).apply {
                description = untrackedProduct.description
                itemGroup = untrackedProduct.itemGroup
                itemType = untrackedProduct.itemType
                hasQuotationOnUom = untrackedProduct.hasQuotation
                imageUrl = untrackedProduct.imageUrl
                lastUpdated = updateTime
            }

        fun updateFromUntracked(untrackedProduct: Product, updateTime: Long = System.currentTimeMillis(), realm: Realm): Product {
            val product = realm.copyToRealmOrUpdate(untrackedProduct)
            product.lastUpdated = updateTime
            return product
        }

        fun deleteAllObsolete(timeInMillis: Long, realm: Realm) {
            realm.where<Product>().lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }

        fun deleteObsoleteByCategory(category: String, timeInMillis: Long, realm: Realm) {
            realm.where<Product>().apply {
                    if (category != ALL_ITEM_TYPE) {
                        equalTo("itemType", category)
                    }
                }
                .lessThan("lastUpdated", timeInMillis)
                .findAll()
                .deleteAllFromRealm()
        }
    }
}