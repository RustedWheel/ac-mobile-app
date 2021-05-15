package com.makeitez.acsalesapp.models

import com.squareup.moshi.Json
import io.realm.RealmList
import io.realm.RealmObject
import java.util.*

open class Uom : RealmObject() {

    @field:Json(name = "uom")
    var description: String = ""; private set

    @field:Json(name = "rate")
    var rate: Double = 0.0; private set

    @field:Json(name = "price")
    var price: Double? = null; private set

    @field:Json(name = "minSalePrice")
    var minSalePrice: Double = 0.0; private set

    @field:Json(name = "balance")
    var balance: Int? = null; private set

    @field:Json(name = "weight")
    var weight: Double? = null; private set

    @field:Json(name = "weightUom")
    var weightUom: String =""; private set

    @field:Json(name = "isBaseUom")
    var isBaseUom: Boolean = false; private set

    @field:Json(name = "hasQuotation")
    var hasQuotation: Boolean = false; private set

    @field:Json(name = "quotationPrice")
    var quotationPrice: Double = 0.0; private set

    @field:Json(name = "isQuotationEffective")
    var isQuotationEffective: Boolean = false; private set

    @field:Json(name = "quotationDate")
    var quotationDate: Date? = null; private set

    @field:Json(name = "hasPreviousPrice")
    var hasPreviousPrice: Boolean = false; private set

    @field:Json(name = "previousPrice")
    var previousPrice: Double = 0.0; private set

    @field:Json(name = "diffPrice")
    var diffPriceList: RealmList<DiffPrice>? = null; private set

    @field:Json(name = "cartonSize")
    var cartonSize: String? = null; private set

    @field:Json(name = "m3")
    var m3: String? = null; private set

    @field:Json(name = "packagingUnit")
    var packagingUnit: String? = null; private set

    @field:Json(name = "barcode")
    var barcode: String? = null; private set

    val hasPreviousDiffPrice: Boolean
        get() = !diffPriceList.isNullOrEmpty()

    val latestPreviousDiffPrice: DiffPrice?
        get() = diffPriceList?.getOrNull(0)

    val defaultUnitPrice: Double
        get() {
                return when {
                    hasQuotation && isQuotationEffective -> quotationPrice
                    hasPreviousPrice -> previousPrice
                    else -> minSalePrice
                }
        }

    val isDefaultUnitPriceValid: Boolean
        get() = defaultUnitPrice > 0.0
}