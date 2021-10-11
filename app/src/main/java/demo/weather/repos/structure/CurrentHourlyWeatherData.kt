package demo.weather.repos.structure

import androidx.annotation.Keep

@Keep
data class CurrentHourlyWeatherData(
    val current: WeatherData,
    val hourly: ArrayList<WeatherData>,
)
@Keep
data class WeatherData(
    val dt: Long,
    val temp: String,
    val humidity: String,
    val wind_speed: String,
    val feels_like: String,
    val sunrise: Long,
    val sunset: Long,
    val weather: ArrayList<WeatherIconData>
)

@Keep
data class WeatherIconData(
    val icon: String,
    val description: String,
)
