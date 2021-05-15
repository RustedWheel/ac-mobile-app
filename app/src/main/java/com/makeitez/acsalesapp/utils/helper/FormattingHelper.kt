package com.makeitez.acsalesapp.utils.helper

import com.makeitez.acsalesapp.utils.Config
import com.makeitez.acsalesapp.utils.extensions.toNdpString
import org.apache.commons.lang3.time.DateFormatUtils
import java.math.BigDecimal
import java.util.*

object FormattingHelper {

    const val dateFormatShort = "dd/MM/yy"
    const val dateFormatLong = "dd/MM/yyyy"
    const val requestDateFormat = "yyyy-MM-dd"
    const val requestTimeFormat = "HH:mm:ss"
    const val requestDatetimeFormat = "$requestDateFormat $requestTimeFormat"
    const val timeDateFormat = "hh:mm aa"
    const val datetimeFormatLong = "$dateFormatLong $timeDateFormat"

    fun formatPrice(price: Double, currency: String = "", dps: Int = Config.NORMAL_PRICE_DPS): String {
        return "$currency ${price.toNdpString(dps, true)}".trim()
    }

    fun formatPrice(price: BigDecimal, currency: String = "", dps: Int = Config.NORMAL_PRICE_DPS): String {
        return formatPrice(price.toDouble(), currency, dps)
    }

    fun formatPriceIfNotZero(price: Double): String
        = if(price == 0.0) "" else formatPrice(price)

    fun formatUnitPrice(price: Double, currency: String = ""): String {
        return formatPrice(price, currency, Config.UNIT_PRICE_DPS)
    }

    fun formatDate(date: Date?, format: String = dateFormatShort): String = date?.let {
        DateFormatUtils.format(date, format)
    } ?: ""
}