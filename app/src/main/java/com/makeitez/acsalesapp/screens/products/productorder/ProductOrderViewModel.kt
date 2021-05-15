package com.makeitez.acsalesapp.screens.products.productorder

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.makeitez.acsalesapp.R
import com.makeitez.acsalesapp.core.ACViewModel
import com.makeitez.acsalesapp.models.Product
import com.makeitez.acsalesapp.models.ProductOrder
import com.makeitez.acsalesapp.models.Uom
import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.Event
import com.makeitez.acsalesapp.utils.extensions.*
import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import java.util.*

class ProductOrderViewModel(application: Application) : ACViewModel(application) {
    companion object {
        const val PRICE_CHANGE_DISCOUNT_SELECTION = 0
        const val PRICE_CHANGE_NEW_PRICE_SELECTION = 1
    }

    val priceChangeOptions = getStringList(R.array.product_order_price_change_options)

    private var productOrder: ProductOrder? = null
    private var currency: String = ""
    private var currencyRate: Double = 0.0

    private val isFilled: Boolean
        get() = productOrder?.isFilled ?: false

    data class ProductInfo(
        val itemCode: String = "",
        val description: String = "",
        val uomOptions: List<Uom> = listOf(),
        val currency: String = "",
        val displayCurrencyRate: Boolean = false,
        val currencyRate: String = ""
    )

    data class ProductUomInfo(
        val hasQuotation: Boolean = false,
        val quotationLabel: String = "",
        val quotationPrice: String = "",
        val isQuotationEffective: Boolean = false,
        val previousPrice: String = "",
        val hasPreviousDiffPrice: Boolean = false,
        val previousDiffLabel: String = "",
        val previousDiffPrice: String = "",
        val stockDescription: String = ""
    )

    data class OrderValidationState(
        val showQuantityHint: Boolean,
        val showDiscountHint: Boolean,
        val showUnitPriceHint: Boolean,
        val showNewPriceHint: Boolean,
        val showFocHint: Boolean,
        val canContinue: Boolean
    )

    data class OrderViolationState(
        val indicatePriceChange: Boolean = false,
        val indicateFoc: Boolean = false
    )

    val isReadOnly = MutableLiveData<Boolean>(false)
    val productInfo = MutableLiveData<ProductInfo>(ProductInfo())
    val productUomInfo = MutableLiveData<ProductUomInfo>(ProductUomInfo())
    val confirmedOrderUomDescription = MutableLiveData<String>("")

    // Form fields
    val orderUomSelection = MutableLiveData<Int>()
    val orderQuantity = MutableLiveData<String>("")
    val orderUnitPrice = MutableLiveData<String>("")
    val orderPriceChangeSelection = MutableLiveData<Int>()
    val orderPriceChange = MutableLiveData<String>("0")
    val orderFoc = MutableLiveData<String>("0")
    val orderDestination = MutableLiveData<String>("")
    val orderDeliveryDate = MutableLiveData<Date>()
    val orderDeliveryDateText: LiveData<String> = Transformations.map(orderDeliveryDate) {
        FormattingHelper.formatDate(it)
    }
    val orderRemarks = MutableLiveData<String>("")

    // Form field mappings
    private val orderUom: LiveData<Uom> = Transformations.map(orderUomSelection) { selection ->
        (productOrder?.product?.getUom(selection) ?: Uom()).also {
            productUomInfo.value = mapToProductUomInfo(it)
            if(isReadOnly.value != true) orderUnitPrice.value = it.defaultUnitPrice.toNdpString(Config.UNIT_PRICE_DPS)
        }
    }
    private val orderHasNewPrice: LiveData<Boolean> = Transformations.map(orderPriceChangeSelection) { selection ->
        selection == PRICE_CHANGE_NEW_PRICE_SELECTION
    }
    val orderPriceChangeDescription: LiveData<String> = Transformations.map(orderPriceChangeSelection) {selection ->
        priceChangeOptions.getOrNull(selection) ?: ""
    }

    val continueButtonName = MutableLiveData<String>()
    val orderValidationState = MediatorLiveData<OrderValidationState>()
    val orderViolationState = MutableLiveData<OrderViolationState>(OrderViolationState())

    val onSaveToCartEvent = MutableLiveData<Event<ProductOrder>>()
    val onConfirmOrderAbandonmentEvent = MutableLiveData<Event<Boolean>>()
    val onExitProductOrderEvent = MutableLiveData<Event<Unit>>()


    fun initProductOrder(currency: String?, currencyRate: Double?, currentProductOrder: ProductOrder?,
                         orderIsReadOnly: Boolean = false, indicateViolationsInReadOnlyMode: Boolean = false) {
        if (this.productOrder == null && currentProductOrder != null && currency != null && currencyRate != null) {
            this.productOrder = currentProductOrder
            this.currency = currency
            this.currencyRate = currencyRate
            this.isReadOnly.value = orderIsReadOnly

            setUpProductInfo(currentProductOrder.product)
            setUpOrderEntries(currentProductOrder)

            if(!orderIsReadOnly) {
                observeForOrderValidation(
                    orderUom,
                    orderQuantity,
                    orderHasNewPrice,
                    orderPriceChange,
                    orderFoc)
            } else {
                setUpReadOnlyInfo(indicateViolationsInReadOnlyMode)
            }
        }
    }

    private fun setUpProductInfo(currentProduct: Product?) {
        val product = currentProduct ?: return
        with(product) {
            productInfo.value = ProductInfo(
                itemCode = itemCode,
                description = description,
                uomOptions = uomDetailList.toList(),
                currency = currency,
                displayCurrencyRate = currency != Config.BASE_CURRENCY,
                currencyRate = currencyRate.toNdpString(Config.EXCHANGE_RATE_DPS)
            )
        }
    }

    private fun setUpOrderEntries(productOrder: ProductOrder) {
        val product = productOrder.product ?: return

        if (isFilled) {
            with(productOrder) {
                orderUomSelection.value = product.getUomSelection(uom)
                orderQuantity.value = quantity.toString()
                orderUnitPrice.value = unitPrice.toNdpString(Config.UNIT_PRICE_DPS)
                orderPriceChangeSelection.value = if (hasNewPrice) PRICE_CHANGE_NEW_PRICE_SELECTION else PRICE_CHANGE_DISCOUNT_SELECTION
                orderPriceChange.value = if (hasNewPrice) {
                    (newPrice?: 0.0).toNdpString(Config.UNIT_PRICE_DPS)
                } else {
                    discount.toNdpString(Config.DISCOUNT_DPS)
                }
                orderFoc.value = foc.toString()
                orderDestination.value = destination
                orderDeliveryDate.value = deliveryDate
                orderRemarks.value = remarks
            }

            continueButtonName.value = getString(R.string.product_order_update_order)
        } else {
            val isDefaultUnitPriceValid = product.getDefaultUom().isDefaultUnitPriceValid
            orderUomSelection.value = product.getDefaultUomSelection()
            orderPriceChangeSelection.value = if(isDefaultUnitPriceValid) PRICE_CHANGE_DISCOUNT_SELECTION else PRICE_CHANGE_NEW_PRICE_SELECTION
            resetPriceChangeField(newPriceSelected = !isDefaultUnitPriceValid)
            orderDeliveryDate.value = Date()

            continueButtonName.value = getString(R.string.product_order_add_to_order)
        }
    }

    private fun setUpReadOnlyInfo(indicateViolations: Boolean) {
        productOrder?.let {
            confirmedOrderUomDescription.value =
                getString(R.string.product_order_uom_desc, it.confirmedUomDescription, it.confirmedUomRate.toStringRemoveTrailingZeroes(Config.UNIT_RATE_DPS))

            if(indicateViolations) {
                orderViolationState.value = OrderViolationState(
                    indicatePriceChange = it.hasDiscount || it.hasNewPrice,
                    indicateFoc = it.hasFoc
                )
            }
        }
    }

    private fun mapToProductUomInfo(uom: Uom): ProductUomInfo {
        with(uom) {
            return ProductUomInfo(
                hasQuotation = hasQuotation,
                quotationLabel = if (hasQuotation) getString(R.string.product_order_quotation, FormattingHelper.formatDate(quotationDate)) else "",
                quotationPrice = if (hasQuotation) FormattingHelper.formatUnitPrice(quotationPrice, currency) else "",
                isQuotationEffective = isQuotationEffective,
                previousPrice = if (hasPreviousPrice) FormattingHelper.formatUnitPrice(previousPrice, currency) else getString(R.string.generic_dash),
                hasPreviousDiffPrice = hasPreviousDiffPrice,
                previousDiffLabel = if (hasPreviousDiffPrice) {
                    getString(R.string.product_order_previous_diff_price, FormattingHelper.formatDate(latestPreviousDiffPrice?.docDate ?: Date()))
                } else {
                    ""
                },
                previousDiffPrice = if (hasPreviousDiffPrice) {
                    FormattingHelper.formatUnitPrice(latestPreviousDiffPrice?.unitPrice ?: 0.0, currency)
                } else {
                    ""
                },
                stockDescription = "$balance $description"
            )
        }
    }

    fun onPriceChangeOptionSelected(optionPosition: Int) {
        if(orderPriceChangeSelection.value != optionPosition) {
            orderPriceChangeSelection.value = optionPosition

            if(isReadOnly.value != true) {
                resetPriceChangeField(newPriceSelected = optionPosition == PRICE_CHANGE_NEW_PRICE_SELECTION)
            }
        }
    }

    private fun resetPriceChangeField(newPriceSelected: Boolean) {
        orderPriceChange.value = if (newPriceSelected) "" else "0" // reset field on spinner change
    }

    fun onUomSelected(uomPosition: Int) {
        orderUomSelection.value = uomPosition
    }

    fun onDeliveryDateSelected(day: Int, month: Int, year: Int) {
        orderDeliveryDate.value = getDateFromPicker(day, month, year)
    }

    private fun observeForOrderValidation(vararg liveDatas: LiveData<out Any>) {
        for(ld in liveDatas) {
            orderValidationState.addSource(ld) { validateOrder() }
        }
    }

    private fun validateOrder() {
        val isUomUnitPriceValid = orderUom.value?.isDefaultUnitPriceValid ?: false
        val isNewPriceSelected = orderHasNewPrice.value == true

        val quantityIsValid = orderQuantity.value.isInt() && orderQuantity.value?.toInt() ?: 0 > 0
        val discountIsValid = isNewPriceSelected || orderPriceChange.value.isPercentage()
        val newPriceIsValid = !isNewPriceSelected || (orderPriceChange.value.isDouble() && orderPriceChange.value?.toDouble() ?: 0.0 > 0)
        val focIsValid = orderFoc.value.isInt()
        val hasHandledInvalidUnitPrice = isUomUnitPriceValid || (isNewPriceSelected && newPriceIsValid)

        orderValidationState.value = OrderValidationState(
            showQuantityHint = !quantityIsValid,
            showDiscountHint = !discountIsValid,
            showUnitPriceHint = !isUomUnitPriceValid && !isNewPriceSelected,
            showNewPriceHint = !newPriceIsValid,
            showFocHint = !focIsValid,
            canContinue = quantityIsValid && discountIsValid && newPriceIsValid && focIsValid && hasHandledInvalidUnitPrice
        )
    }


    fun fillOrder() {
        val productOrder = productOrder ?: return
        val productUom = orderUom.value ?: return

        val isNewPriceSelected = orderHasNewPrice.value == true
        val priceChangeValue = orderPriceChange.value?.toDouble() ?: 0.0

        productOrder.fill(
            productUom,
            orderQuantity.value?.toInt() ?: 0,
            orderUnitPrice.value?.toDouble() ?: 0.0,
            if (isNewPriceSelected) priceChangeValue else null,
            if (!isNewPriceSelected) priceChangeValue else 0.00,
            orderFoc.value?.toInt() ?: 0,
            orderDestination.value,
            orderDeliveryDate.value,
            orderRemarks.value
        )
        onSaveToCartEvent.value = Event(productOrder)
    }

    fun confirmAbandonProductOrder() {
        onConfirmOrderAbandonmentEvent.value = Event(isFilled)
    }

    fun abandonProductOrder() {
        onExitProductOrderEvent.value = Event(Unit)
    }
}