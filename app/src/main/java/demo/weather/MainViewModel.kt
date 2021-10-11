package demo.weather

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import demo.weather.repos.structure.WeatherData
import demo.weather.repos.structure.WeatherData2

class MainViewModel : ViewModel() {
    val locationLiveData = MutableLiveData<Location>()
    val currentLiveWeatherData = MutableLiveData<WeatherData>()
    val hourlyLiveWeatherData = MutableLiveData<ArrayList<WeatherData>>()
    val dailyLiveWeatherData = MutableLiveData<ArrayList<WeatherData2>>()

    override fun onCleared() {
        hourlyLiveWeatherData.value?.clear()
        dailyLiveWeatherData.value?.clear()
        super.onCleared()
    }
}