package demo.weather.repos.structure


data class CurrentHourlyWeatherData(
    val current: WeatherData,
    val hourly: ArrayList<WeatherData>,
)


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


data class WeatherIconData(
    val icon: String,
    val description: String,
)
