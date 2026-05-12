package com.example.happnin.ui.registration

import com.example.happnin.ApiClient
import com.example.happnin.ApiService
import com.example.happnin.Registration
import com.example.happnin.RegistrationCreateRequest
import com.example.happnin.data.SupabaseNetwork
import io.github.jan.supabase.gotrue.auth

interface RegistrationRepository {
    suspend fun getRegisteredEventIds(): Set<String>
    suspend fun createRegistration(eventId: String): Registration
    suspend fun deleteRegistrationForEvent(eventId: String)
}

class ApiRegistrationRepository(
    private val apiService: ApiService = ApiClient.retrofitService,
) : RegistrationRepository {

    override suspend fun getRegisteredEventIds(): Set<String> {
        val session = currentAuthSession()
        return apiService.getRegistrations(
            authorization = session.authorizationHeader,
            userId = session.userId,
        ).data.map { it.eventId }.toSet()
    }

    override suspend fun createRegistration(eventId: String): Registration {
        val session = currentAuthSession()
        return apiService.createRegistration(
            authorization = session.authorizationHeader,
            registration = RegistrationCreateRequest(eventId),
        ).data
    }

    override suspend fun deleteRegistrationForEvent(eventId: String) {
        val session = currentAuthSession()
        val response = apiService.deleteRegistrationByEvent(
            authorization = session.authorizationHeader,
            eventId = eventId,
        )

        if (!response.isSuccessful) {
            throw IllegalStateException("Could not cancel registration.")
        }
    }

    private suspend fun currentAuthSession(): AuthSession {
        val auth = SupabaseNetwork.client.auth
        auth.awaitInitialization()
        if (auth.currentAccessTokenOrNull() == null || auth.currentUserOrNull() == null) {
            auth.loadFromStorage()
        }

        val token = auth.currentAccessTokenOrNull()
        val userId = auth.currentUserOrNull()?.id
        if (token.isNullOrBlank() || userId.isNullOrBlank()) {
            throw IllegalStateException("You must be signed in to manage registrations.")
        }

        return AuthSession(
            authorizationHeader = "Bearer $token",
            userId = userId,
        )
    }

    private data class AuthSession(
        val authorizationHeader: String,
        val userId: String,
    )
}
