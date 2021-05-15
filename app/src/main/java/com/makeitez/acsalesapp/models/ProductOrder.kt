package com.makeitez.acsalesapp.models

import com.makeitez.acsalesapp.models.api.ProductOrderResponseData
import com.makeitez.acsalesapp.utils.extensions.div
import com.makeitez.acsalesapp.utils.extensions.times
import io.realm.Realm
import io.realm.RealmObject
import java.math.BigDecimal
import java.util.*

open class ProductOrder : RealmObject() {

    var isFilled: Boolean = false
        private set

    var product: Product? = null

    var uom: Uom? = null
        private set

    var quantity: Int = 0
        private set

    var unitPrice: Double = 0.0
        private set

    var newPrice: Double? = null
        private set

    var discount: Double = 0.0
        private set

    var foc: Int = 0
        private set

    var destination: String? = null
        private set

    var deliveryDate: Date? = null
        private set

    var remarks: String? = null
        private set

    // UOM can be deleted/have rate changed over time, so PO will have a confirmed UOM desc and rate
    var confirmedUomDescription: String = ""; private set
    var confirmedUomRate: Double = 0.0; private set

    val hasDiscount: Boolean
        get() = discount > 0.0

    val hasNewPrice: Boolean
        get() = newPrice != null

    val hasFoc: Boolean
        get() = foc > 0

    val violationCount: Int
        get() = arrayOf(hasDiscount, hasNewPrice, hasFoc).count { it }

    val finalUnitPrice: Double
        get() = newPrice ?: unitPrice

    fun offset(offsetValue: Int) {
        this.quantity = this.quantity + offsetValue
    }

    fun setUom(uom: Uom) {
        this.uom = uom
        this.unitPrice = uom.defaultUnitPrice
    }

    fun setDeliveryDate(deliveryDate: Date) {
        this.deliveryDate = deliveryDate
    }

    fun fill(uom: Uom, quantity: Int, unitPrice: Double, newPrice: Double?, discount: Double, foc: Int, destination: String?, deliveryDate: Date?, remarks: String?) {
        this.uom = uom
        this.quantity = quantity
        this.unitPrice = unitPrice
        this.newPrice = newPrice
        this.discount = discount
        this.foc = foc
        this.destination = destination
        this.deliveryDate = deliveryDate
        this.remarks = remarks

        this.isFilled = true
    }

    fun isOrderValid(): Boolean = isFilled && quantity > 0 && finalUnitPrice > 0

    fun getPrice(): BigDecimal {
        uom?.let { uom ->
            return calculatePrice(uom.rate)
        }

        return BigDecimal.ZERO
    }

    fun getConfirmedPrice(): BigDecimal = calculatePrice(confirmedUomRate)

    private fun calculatePrice(uomRate: Double): BigDecimal {
        val units = uomRate.toBigDecimal() * quantity
        val total = units * finalUnitPrice

        val toDiscount =  total / 100 * discount

        return total - toDiscount
    }

    companion object {

        fun createUntracked(product: Product) : ProductOrder {
            val productOrder = ProductOrder()
            productOrder.product = product
            return productOrder
        }

        fun mapFromPreviousProductOrder(previousPO: ProductOrderResponseData) : ProductOrder {
            var trackedProduct: Product? = null
            Realm.getDefaultInstance().executeTransaction { realm ->
                trackedProduct = Product.updateFromUntracked(previousPO.productDetails, realm = realm)
            }

            return ProductOrder().apply {
                isFilled = true
                trackedProduct?.let {trackedProduct ->
                    product = trackedProduct
                    // uom cannot be deleted after an order is made with it, so the Product will have the uom
                    uom = trackedProduct.uomDetailList.firstOrNull {
                        it.description == previousPO.uom
                    } ?: trackedProduct.getDefaultUom()
                }
                quantity = previousPO.quantity ?: 0
                unitPrice = previousPO.unitPrice ?: 0.0
                newPrice = previousPO.newPrice
                discount = previousPO.discount ?: 0.0
                foc = previousPO.focQuantity ?: 0
                destination = previousPO.destination ?: ""
                deliveryDate = previousPO.deliveryDate
                remarks = previousPO.remarks ?: ""
                confirmedUomDescription = previousPO.uom ?: ""
                confirmedUomRate = previousPO.rate ?: 0.0
            }
        }

        fun mapForReordering(previousPO: ProductOrder): ProductOrder {
            return ProductOrder().apply {
                isFilled = true
                product = previousPO.product
                uom = previousPO.uom
                quantity = previousPO.quantity
                unitPrice = uom?.defaultUnitPrice ?: 0.0
                destination = previousPO.destination
                deliveryDate = Date()
                remarks = previousPO.remarks
            }
        }
    }
}