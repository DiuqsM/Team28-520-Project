package com.example.happnin.data

import kotlinx.datetime.LocalDateTime

enum class EventStatus {
    OPEN, FULL, CANCELLED;

    val label: String get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

data class Event(
    val id: String,
    val createdBy: String?,
    val source: String,
    val title: String,
    val description: String,
    val location: String,
    val college: String,
    val price: Double,
    val ageLimit: Int?,
    val capacity: Int?,
    val registrationCount: Int,
    val status: EventStatus,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val createdAt: LocalDateTime,
)
