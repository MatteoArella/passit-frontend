package com.github.passit.core.extension

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String?.fromISO8601UTC(): Date? {
    return this?.let {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        try {
            df.parse(it)
        } catch (e: ParseException) {
            null
        }
    }
}

fun Date?.toISO8601UTC(): String? {
    return this?.let {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        df.format(it)
    }
}
