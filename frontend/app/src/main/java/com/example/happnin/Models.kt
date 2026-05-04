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