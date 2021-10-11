package demo.weather.repos.structure


data class DailyWeatherData(
    val daily: ArrayList<WeatherData2>,
)


data class WeatherData2(
    val dt: Long,
    val temp: TempNumber,
    val feels_like: TempNumber,
    val sunrise: Long,
    val sunset: Long,
    val weather: ArrayList<WeatherIconData>
)

data class TempNumber(
    val min: Number,
    val max: Number,
)