package com.makeitez.acsalesapp.utils.extensions

import com.makeitez.acsalesapp.utils.Config
import java.math.BigDecimal

fun BigDecimal.setMaxACScale(): BigDecimal = this.setScale(Config.DISCOUNTED_UNIT_PRICE_DPS)

operator fun BigDecimal.times(factor: Int): BigDecimal
        = this * factor.toBigDecimal()

operator fun BigDecimal.times(factor: Float): BigDecimal
        = this * factor.toBigDecimal()

operator fun BigDecimal.times(factor: Double): BigDecimal
        = this * factor.toBigDecimal()

operator fun BigDecimal.div(factor: Int): BigDecimal
        = setMaxACScale() / factor.toBigDecimal()

operator fun BigDecimal.div(factor: Float): BigDecimal
        = setMaxACScale() / factor.toBigDecimal()

operator fun BigDecimal.div(factor: Double): BigDecimal
        = setMaxACScale() / factor.toBigDecimal()
