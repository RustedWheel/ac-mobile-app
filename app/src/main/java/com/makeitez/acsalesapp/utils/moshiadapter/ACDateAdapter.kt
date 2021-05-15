package com.makeitez.acsalesapp.utils.moshiadapter

import com.makeitez.acsalesapp.utils.helper.FormattingHelper
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.apache.commons.lang3.time.DateUtils
import java.util.*


class ACDateAdapter {
    @ToJson
    fun toJson(date: Date): String {
        return FormattingHelper.formatDate(date, FormattingHelper.requestDatetimeFormat)
    }

    @FromJson
    fun fromJson(dateString: String): Date {
        return DateUtils.parseDateStrictly(dateString, FormattingHelper.requestDatetimeFormat, FormattingHelper.requestDateFormat)
    }
}