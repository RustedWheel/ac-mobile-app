package com.makeitez.acsalesapp.screens.salesorder.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.extensions.textToShow
import com.makeitez.acsalesapp.utils.extensions.toStringRemoveTrailingZeroes
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import kotlinx.android.synthetic.main.item_product_order_list.view.*


class ProductOrderViewHolder(
    listItemView: View, productOrderListListener: Listener,
    private val isReadOnly: Boolean,
    private val indicateQuotations: Boolean,
    private val indicateViolations: Boolean
) :
    RecyclerView.ViewHolder(listItemView) {

    interface Listener {
        fun onProductOrderClick(order: ProductOrder)
        fun onProductOrderDelete(order: ProductOrder)
        fun onProductOrderIncrement(order: ProductOrder)
        fun onProductOrderDecrement(order: ProductOrder)
    }

    private val productOrderCard = itemView.productOrderCard
    private val productOrderDeleteButton = itemView.productOrderDeleteButton
    private val productOrderQuotationIconImage = itemView.productOrderQuotationIconImage

    private val productOrderViolationCountCard = itemView.productOrderViolationCountCard
    private val productOrderViolationCountText = itemView.productOrderViolationCountText

    private val productNameText = itemView.productNameText
    private val productCodeText = itemView.productCodeText
    private val productOrderDiscountText = itemView.productOrderDiscountText
    private val productOrderFocText = itemView.productOrderFocText

    private val productOrderUnitPriceText = itemView.productOrderUnitPriceText
    private val productOrderUomText = itemView.productOrderUomText

    private val productOrderDynamicContentLayout = itemView.productOrderDynamicContentLayout
    private val productOrderQuantityText = itemView.productOrderQuantityText
    private val productOrderIncrementButton = itemView.productOrderIncrementButton
    private val productOrderDecrementButton = itemView.productOrderDecrementButton

    private val productOrderTotalPriceText = itemView.productOrderTotalPriceText


    private var productOrder: ProductOrder? = null

    init {
        productOrderCard.setOnClickListener {
            productOrder?.let(productOrderListListener::onProductOrderClick)
        }
        if(!isReadOnly) {
            productOrderDynamicContentLayout.setOnClickListener {
                // disable click on the plus minus button area
            }
        }
        productOrderDeleteButton.setOnClickListener {
            productOrder?.let(productOrderListListener::onProductOrderDelete)
        }
        productOrderIncrementButton.setOnClickListener {
            productOrder?.let(productOrderListListener::onProductOrderIncrement)
        }
        productOrderDecrementButton.setOnClickListener {
            productOrder?.let(productOrderListListener::onProductOrderDecrement)
        }
    }

    fun bind(boundProductOrder: ProductOrder, currency: String) {
        productOrder = boundProductOrder
        val product = boundProductOrder.product
        val productIsActive = product?.productStatus?.isActive() ?: false

        val context = productOrderCard.context

        productNameText.text = product?.description
        productCodeText.text = product?.itemCode

        productOrderDiscountText.textToShow = if (boundProductOrder.hasDiscount) context.getString(
            R.string.cart_item_discount,
            boundProductOrder.discount.toString()
        ) else ""

        productOrderFocText.textToShow = if (boundProductOrder.hasFoc) context.getString(
            R.string.cart_item_foc,
            boundProductOrder.foc.toString()
        ) else ""

        productOrderUnitPriceText.text =
            FormattingHelper.formatUnitPrice(boundProductOrder.finalUnitPrice)

        productOrderUnitPriceText.setTextColor(
            ContextCompat.getColor(
                context,
                if (boundProductOrder.hasNewPrice) R.color.ac_red_900 else R.color.ac_green
            )
        )

        val uomName = if (isReadOnly) boundProductOrder.confirmedUomDescription else boundProductOrder.uom?.description ?: ""
        val uomRate = if (isReadOnly) boundProductOrder.confirmedUomRate else boundProductOrder.uom?.rate ?: 0.0
        productOrderUomText.text = context.getString(R.string.product_order_uom_desc, uomName, uomRate.toStringRemoveTrailingZeroes(
            Config.UNIT_RATE_DPS))

        productOrderQuantityText.text = boundProductOrder.quantity.toString()

        val productOrderPrice = if (isReadOnly) boundProductOrder.getConfirmedPrice() else boundProductOrder.getPrice()
        productOrderTotalPriceText.text = FormattingHelper.formatPrice(productOrderPrice, currency)

        productOrderDeleteButton.isVisible = !isReadOnly
        productOrderIncrementButton.isVisible = !isReadOnly
        productOrderDecrementButton.isVisible = !isReadOnly

        productOrderCard.setCardBackgroundColor(ContextCompat.getColor(context, (
                if(productIsActive) R.color.ac_card_surface else R.color.ac_inactive_card_surface)))

        val showQuotationIndication = productIsActive && indicateQuotations && product?.hasQuotationOnUom == true
        productOrderCard.foreground = if (showQuotationIndication) {
            ContextCompat.getDrawable(context, R.drawable.outlined_border_red)
        } else {
            null
        }
        productOrderQuotationIconImage.isVisible = showQuotationIndication

        productOrderViolationCountCard.isVisible = indicateViolations && boundProductOrder.violationCount > 0
        productOrderViolationCountText.text = boundProductOrder.violationCount.toString()
    }

    companion object {
        fun newInstance(parent: ViewGroup, listener: Listener, isReadOnly: Boolean, indicateQuotations: Boolean, indicateViolations: Boolean) =
            ProductOrderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_product_order_list, parent, false),
                listener,
                isReadOnly,
                indicateQuotations,
                indicateViolations
            )
    }
}