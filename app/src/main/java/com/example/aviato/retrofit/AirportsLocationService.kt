package com.example.aviato.retrofit

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AirportsLocationService {
    @GET("autocomplete")
    fun getAirportList(@Query("term") text:String,
                       @Query("lang") lang:String): Single<ResponseBody>

    companion object Factory {
        fun create(): AirportsLocationService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://yasen.hotellook.com/")
                .build()

            return retrofit.create(AirportsLocationService::class.java);
        }
    }


}