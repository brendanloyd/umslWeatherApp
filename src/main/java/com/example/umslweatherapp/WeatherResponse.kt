package com.example.umslweatherapp

data class WeatherResponse(
    val coord: Coordinates,
    val weather: Array<WeatherDesc>,
    val main: Main,
    val weatherDesc: WeatherDesc
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class WeatherDesc(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Double,
    val humidity: Double
)