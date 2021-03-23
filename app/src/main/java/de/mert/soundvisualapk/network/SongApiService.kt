package de.mert.soundvisualapk.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.net.URL

private const val BASE_URL = "http://localhost:3000/"
private var retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface SongApiService {
    @GET
    suspend fun getSongs(@Url url: String): String
}

object SongApi {
    val retrofitService : SongApiService by lazy {
        retrofit.create(SongApiService::class.java)
    }
}