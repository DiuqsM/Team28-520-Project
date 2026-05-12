package com.example.happnin
import com.google.gson.annotations.SerializedName
import com.example.happnin.data.Event as DomainEvent
import com.example.happnin.data.EventStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Event(
    val id: String,

    @SerializedName("created_by")
    val createdBy: String?,

    val source: String?,

    val title: String,

    val description: String?,

    val location: String,

    val college: String?,

    val price: Float?,

    @SerializedName("age_limit")
    val ageLimit: Int?,

    val capacity: Int?,

    @SerializedName("registration_count")
    val registrationCount: Int?,

    val status: String?,

    @SerializedName("starts_at")
    val startsAt: String,

    @SerializedName("ends_at")
    val endsAt: String?,

    @SerializedName("created_at")
    val createdAt: String?,
)

data class EventResponse(
    val data: List<Event>
)

data class User(
    val id: String,

    val username: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val email: String,

    val bio: String?
)

data class UserResponse(
    val data: List<User>
)

data class Registration(
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("event_id")
    val eventId: String
)

data class RegistrationResponse(
    val data: List<Registration>
)

data class RegistrationCreateRequest(
    @SerializedName("event_id")
    val eventId: String
)

data class RegistrationCreateResponse(
    val message: String,
    val data: Registration
)

fun Event.toDomain(): DomainEvent {
    val parsedStartsAt = parseEventDateTime(startsAt, "starts_at")
    val parsedEndsAt = endsAt?.let { parseEventDateTime(it, "ends_at") } ?: parsedStartsAt
    val parsedCreatedAt = createdAt?.let { parseEventDateTime(it, "created_at") } ?: parsedStartsAt
    val statusEnum = when (status?.lowercase()) {
        "full" -> EventStatus.FULL
        "cancelled" -> EventStatus.CANCELLED
        else -> EventStatus.OPEN
    }
    return DomainEvent(
        id = id,
        createdBy = createdBy,
        source = source ?: "manual",
        title = title,
        description = description ?: "",
        location = location,
        college = college ?: "",
        price = price?.toDouble() ?: 0.0,
        ageLimit = ageLimit,
        capacity = capacity,
        registrationCount = registrationCount ?: 0,
        status = statusEnum,
        startsAt = parsedStartsAt,
        endsAt = parsedEndsAt,
        createdAt = parsedCreatedAt,
    )
}

private fun parseEventDateTime(value: String, fieldName: String): LocalDateTime {
    return runCatching { LocalDateTime.parse(value) }
        .recoverCatching {
            Instant.parse(value).toLocalDateTime(TimeZone.of("America/New_York"))
        }
        .getOrElse {
            throw IllegalArgumentException("Invalid event timestamp for $fieldName: $value", it)
        }
}
