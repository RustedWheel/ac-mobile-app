package com.makeitez.acsalesapp.models.api

import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import java.util.*

class PayloadDate(val date: Date) {

    constructor() : this(Date())

    override fun toString(): String = FormattingHelper.formatDate(date, FormattingHelper.requestDateFormat)

}