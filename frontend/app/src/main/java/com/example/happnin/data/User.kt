package com.example.happnin.data

data class User(
    val id: String,
    val googleSub: String? = null,
    val email: String,
    val name: String,
    val profilePhotoUrl: String? = null,
    val college: String,
    val role: String = "student",
    val isSuspended: Boolean = false,
)

data class Registration(
    val id: String,
    val userId: String,
    val eventId: String,
)
