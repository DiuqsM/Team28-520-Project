package com.example.happnin
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("keyword") keyword: String? = null,
        @Query("location") location: String? = null,
        @Query("max_price") maxPrice: Float? = null,
        @Query("user_age") userAge: Int? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): EventResponse

    @GET("users")
    suspend fun getUsers(
        @Query("id") id: String? = null,
        @Query("name") name: String? = null
    ): UserResponse

    @GET("registrations")
    suspend fun getRegistrations(
        @Header("Authorization") authorization: String,
        @Query("id") id: String? = null,
        @Query("user_id") userId: String? = null,
        @Query("event_id") eventId: String? = null
    ): RegistrationResponse

    @POST("registrations")
    suspend fun createRegistration(
        @Header("Authorization") authorization: String,
        @Body registration: RegistrationCreateRequest
    ): RegistrationCreateResponse

    @DELETE("registrations/by-event/{event_id}")
    suspend fun deleteRegistrationByEvent(
        @Header("Authorization") authorization: String,
        @Path("event_id") eventId: String
    ): Response<Unit>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/" // so it goes to computer's address instead of emulator's

    val retrofitService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
