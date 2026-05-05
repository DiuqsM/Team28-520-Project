package com.example.happnin
import com.google.gson.annotations.SerializedName

data class Event(
    val id: String,

    @SerializedName("created_by")
    val createdBy: String?,

    val title: String,

    val description: String?,

    val location: String,

    val price: Float?,

    @SerializedName("starts_at")
    val startsAt: String,

    @SerializedName("ends_at")
    val endsAt: String?,

    @SerializedName("age_limit")
    val ageLimit: Int?
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