package com.example.happnin.data

// All mock data lives here so it is easy to find and replace with real API calls.
object MockData {

    // TODO: Ava/Srijan - replace with real GET /users/me API call
    val currentUser = User(
        id = "user-001",
        email = "alex@umass.edu",
        name = "Alex Rivera",
        college = "UMass Amherst",
        role = "student",
    )

    // TODO: Ava/Srijn - replace with real GET /users/{id}/registrations API call
    val initialRegistrations = listOf(
        Registration("reg-001", "user-001", "event-001"),
        Registration("reg-002", "user-001", "event-005"),
        Registration("reg-003", "user-001", "event-009"),
    )
}
