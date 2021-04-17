package com.github.passit.core.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
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

fun Date?.isSameDay(date: Date?): Boolean {
    if (this == null || date == null) return false
    val calendar1 = Calendar.getInstance()
    calendar1.time = this
    val calendar2 = Calendar.getInstance()
    calendar2.time = date
    return calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR] &&
            calendar1[Calendar.MONTH] == calendar2[Calendar.MONTH] &&
            calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH]
}
