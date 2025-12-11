package de.devdudes.aoc.core

import java.text.SimpleDateFormat
import java.util.Calendar

object Utils {
    fun formatDuration(durationInMillis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = durationInMillis }
        return SimpleDateFormat("mm:ss:SSS").format(calendar.time)
    }
}
