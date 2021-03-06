package de.mert.soundvisualapk.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

private const val BASE_URL = "http://localhost:3000/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private var retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface SongApiService {
    @GET
    suspend fun getSongs(@Url url: String): List<GetSongs>

    @POST
    suspend fun playSong(@Url url: String, @Body playSong: PlaySong)

    @GET
    suspend fun pauseSong(@Url url: String)

    @GET
    suspend fun skipSong(@Url url: String)

    @GET
    suspend fun backSong(@Url url: String)

    @GET
    suspend fun getSong(@Url url: String): GetSong
}

object SongApi {
    val retrofitService : SongApiService by lazy {
        retrofit.create(SongApiService::class.java)
    }
}