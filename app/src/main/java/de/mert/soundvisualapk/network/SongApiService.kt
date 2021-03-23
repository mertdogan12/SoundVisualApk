package de.mert.soundvisualapk.network

import de.mert.soundvisualapk.ConnectActivity
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

interface SongApiService {
    @GET("getSongs")
    suspend fun getSongs(): String
}

object SongApi {
    const val BASE_URL = "http://localhost:3000"

    var retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val retrofitService : SongApiService by lazy {
        retrofit.create(SongApiService::class.java)
    }
}