package com.github.captainayan.locationshare.android

import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.converter.gson.GsonConverterFactory

class LocationSender(retrofit: Retrofit) {
    data class Location(
        val lng: Double,
        val lat: Double
    )

    interface LocationApi {
        @POST("/sendlocation")
        suspend fun sendLocation(@HeaderMap headers: Map<String, String>, @Body location: Location)
    }

    private var retrofit: Retrofit? = retrofit

    suspend fun sendLocation(uuid: String, lng: Double, lat: Double) {
        val api = retrofit?.create(LocationApi::class.java)!!

        val headers = HashMap<String, String>()
        headers["uuid"] = uuid
        val reqBody = Location(lng, lat)

        api.sendLocation(headers, reqBody)
    }

    companion object {
        private const val baseUrl:String = "http://192.168.0.105:4000"
        private lateinit var INSTANCE: LocationSender

        fun getInstance(): LocationSender {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = LocationSender(Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build())
            }
            return INSTANCE
        }
    }
}