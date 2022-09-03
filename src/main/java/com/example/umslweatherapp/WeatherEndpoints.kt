package com.example.umslweatherapp

import android.graphics.drawable.Icon
import android.media.Image
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherEndpoints {
    @GET("/data/2.5/weather")
    fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") appId: String, @Query("units") units: String): Call<WeatherResponse>

    @GET("/img/wn/{icon}")
    fun getIcon(@Path("icon") iconPath: String, @Query("appid") appId: String): Call<Icon>
}