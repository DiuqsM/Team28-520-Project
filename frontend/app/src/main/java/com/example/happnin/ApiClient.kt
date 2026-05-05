package com.example.happnin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("keyword") keyword: String? = null,
        @Query("location") location: String? = null,
        @Query("max_price") maxPrice: Float? = null,
        @Query("min_age") minAge: Int? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): EventResponse
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