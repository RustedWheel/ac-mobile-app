package com.makeitez.acsalesapp.utils

import android.text.InputFilter
import android.text.Spanned
import com.makeitez.acsalesapp.utils.extensions.buildResultingString
import java.util.regex.Matcher
import java.util.regex.Pattern

class DecimalInputFilter(dps: Int) : InputFilter {
    var correctPattern: Pattern = Pattern.compile("^\\d*\\.?\\d{0," + dps + "}\$")

    override fun filter(
        source: CharSequence,
        sstart: Int,
        send: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val resultingString = buildResultingString(source, sstart, send, dest, dstart, dend)

        val matcher: Matcher = correctPattern.matcher(resultingString)

        return if (matcher.matches()) null else ""
    }
}