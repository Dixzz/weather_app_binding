package demo.weather.api

import demo.weather.repos.structure.CurrentHourlyWeatherData
import demo.weather.repos.structure.DailyWeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("/data/2.5/onecall")
    fun getForecast(
        @Query("exclude") exclude: List<String>,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Call<CurrentHourlyWeatherData>


    @GET("/data/2.5/onecall")
    fun getForecastDaily(
        @Query("exclude") exclude: List<String>,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Call<DailyWeatherData>
}