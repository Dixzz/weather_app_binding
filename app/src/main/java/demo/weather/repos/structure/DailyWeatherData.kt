package demo.weather.repos.structure

import androidx.annotation.Keep

@Keep
data class DailyWeatherData(
    val daily: ArrayList<WeatherData2>,
)

@Keep
data class WeatherData2(
    val dt: Long,
    val temp: TempNumber,
    val feels_like: TempNumber,
    val sunrise: Long,
    val sunset: Long,
    val weather: ArrayList<WeatherIconData>
)
@Keep
data class TempNumber(
    val min: Number,
    val max: Number,
)