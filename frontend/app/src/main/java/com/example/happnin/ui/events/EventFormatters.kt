package com.example.happnin.ui.events

import com.example.happnin.data.Event
import kotlinx.datetime.LocalDateTime

internal fun Event.toDateRangeLabel(): String {
    val timeRange = if (endsAt == startsAt) startsAt.toTimeLabel()
                    else "${startsAt.toTimeLabel()}–${endsAt.toTimeLabel()}"
    return "${startsAt.toDateLabel()}, $timeRange"
}

internal fun LocalDateTime.toDateLabel(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$month $dayOfMonth"
}

internal fun LocalDateTime.toTimeLabel(): String {
    val suffix = if (hour >= 12) "pm" else "am"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minutePadded = minute.toString().padStart(2, '0')
    return "$hour12:$minutePadded$suffix"
}
