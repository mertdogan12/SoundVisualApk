package de.mert.soundvisualapk.network

import de.mert.soundvisualapk.ConnectActivity
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(ConnectActivity.baseUrl)
    .build()

interface SongApiService {
    @GET("getSongs")
    suspend fun getSongs(): String
}

object SongApi {
    val retrofitService : SongApiService by lazy {
        retrofit.create(SongApiService::class.java)
    }
}