package com.makeitez.acsalesapp.models.api.request

import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.api.PayloadDate
import com.squareup.moshi.Json

open class CreateProductOrderRequestData : BaseApiProductOrder() {
    @field:Json(name = "deliveryDate")
    private var deliveryDate: String? = null

    companion object {
        fun mapFromProductOrder(productOrder: ProductOrder) =
            CreateProductOrderRequestData().apply {
                itemCode = productOrder.product?.itemCode ?: ""
                description = productOrder.product?.description ?: ""
                quantity = productOrder.quantity
                uom = productOrder.uom?.description ?: ""
                unitPrice = productOrder.unitPrice
                focQuantity = productOrder.foc
                newPrice = if (productOrder.hasNewPrice) productOrder.newPrice else null
                discount = if (!productOrder.hasNewPrice) productOrder.discount else null
                destination = productOrder.destination
                deliveryDate = productOrder.deliveryDate?.let { PayloadDate(it).toString() }
                remarks = productOrder.remarks
                minSalePrice = productOrder.uom?.minSalePrice
                rate = productOrder.uom?.rate
            }
    }
}