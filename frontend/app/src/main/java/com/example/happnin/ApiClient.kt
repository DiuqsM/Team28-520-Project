package com.example.happnin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// endpoints
interface ApiService {
    @GET("events")
    suspend fun getEvents(): EventResponse
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