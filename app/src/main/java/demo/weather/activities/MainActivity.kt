package demo.weather.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.gms.tasks.Task
import demo.weather.CallbackRetro
import demo.weather.MainViewModel
import demo.weather.R
import demo.weather.api.NetworkRequest
import demo.weather.base.BaseActivity
import demo.weather.base.BaseRecyclerViewAdapter
import demo.weather.databinding.ActivityMainBinding
import demo.weather.databinding.RecyclerDailyForecastItemBinding
import demo.weather.databinding.RecyclerHourlyForecastItemBinding
import demo.weather.logit
import demo.weather.repos.structure.CurrentHourlyWeatherData
import demo.weather.repos.structure.DailyWeatherData
import demo.weather.repos.structure.WeatherData
import demo.weather.repos.structure.WeatherData2
import org.androidannotations.annotations.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

@RuntimePermissions
@EActivity
open class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewmodel by viewModels<MainViewModel>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_LOCATION_ACCESS: Int = 101
    private val simpleDateFormat = SimpleDateFormat("dd MMM, yyyy'T'hh:mmaaa", Locale.getDefault())

    @Bean
    lateinit var networkRequest: NetworkRequest

    @SystemService
    lateinit var locationServices: LocationManager

    @AfterInject
    fun init() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun getAnimFromWeather(weather: String): String = when (weather) {
        //"01d", "01n", "02d", "02n" -> weather
        "03d", "03n", "04d", "04n" -> "03_04_d_n"
        "09d", "10d" -> "10_9_d"
        "09n", "10n" -> "10_9_n"
        "11d", "11n" -> "11d"
        "13d", "13n" -> "13d"
        "50d", "50n" -> "50d"
        else -> weather
    }

    @AfterViews
    fun initViews() {
        setSupportActionBar(binding.toolbar)
        binding.toolbarShimmer.showShimmer(true)

        val adapterDailyForecast = BaseRecyclerViewAdapter(
            list = ArrayList<WeatherData2>(),
            bindingFactory = RecyclerDailyForecastItemBinding::inflate
        )
        val adapterHourlyForecast = BaseRecyclerViewAdapter(
            list = ArrayList<WeatherData>(),
            bindingFactory = RecyclerHourlyForecastItemBinding::inflate
        )

        binding.dailyForecastRec.run {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterDailyForecast
        }
        binding.hourlyForecastRec.run {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterHourlyForecast
        }
        viewmodel.hourlyLiveWeatherData.observe(this) {
            adapterHourlyForecast.update(it)
        }
        viewmodel.dailyLiveWeatherData.observe(this) {
            adapterDailyForecast.update(it)
        }

        viewmodel.currentLiveWeatherData.observe(this) {
            logit(it)
            val weatherInfo = it.weather.first()

            binding.root.postDelayed({
                supportActionBar?.title = getString(R.string.app_name)
                binding.toolbarShimmer.hideShimmer()
            }, 2000)
            binding.lottieAnimationView2.setAnimation("${getAnimFromWeather(weatherInfo.icon)}.json")
            val dateTime = simpleDateFormat.format(TimeUnit.MILLISECONDS.toMicros(it.dt)).split("T")

            binding.desc = weatherInfo.description.capitalize(Locale.ROOT)
            binding.wind = it.wind_speed.toFloat().roundToInt().toString() + "km/h"
            binding.humid = it.humidity + "%"
            binding.feelLike = it.feels_like.toFloat().roundToInt().toString() + "°"
            binding.temp = it.temp.toFloat().roundToInt().toString() + "°"
            binding.date = dateTime.first()
            binding.time = dateTime.last()
        }

        viewmodel.locationLiveData.observe(this) {
            if (binding.refreshLayout.isRefreshing) binding.root.postDelayed({
                binding.lottieAnimationView2.playAnimation()
                binding.refreshLayout.isRefreshing = false
                binding.toolbarShimmer.hideShimmer()
            }, 2000)
            val add = Geocoder(this).getFromLocation(it.latitude, it.longitude, 1).first()
            binding.place = if (add != null) "${add.locality}, ${add.adminArea}" else "N/A"
            networkRequest.client.getForecast(
                exclude = listOf("minutely,alerts,daily"),
                latitude = it.latitude,
                longitude = it.longitude
            )
                .enqueue(CallbackRetro<CurrentHourlyWeatherData, CurrentHourlyWeatherData>().addQuickCall {
                    it.body()?.let {
                        viewmodel.currentLiveWeatherData.postValue(it.current)
                        viewmodel.hourlyLiveWeatherData.postValue(it.hourly)
                    }
                })
            networkRequest.client.getForecastDaily(
                exclude = listOf("minutely,alerts,current,hourly"),
                latitude = it.latitude,
                longitude = it.longitude
            )
                .enqueue(CallbackRetro<DailyWeatherData, DailyWeatherData>().addQuickCall {
                    it.body()?.let {
                        viewmodel.dailyLiveWeatherData.postValue(it.daily)
                    }
                })
        }
        if (!locationServices.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showEnableGPSDialog()
        } else {
            locateUser()
        }
        binding.refreshLayout.setOnRefreshListener {
            if (binding.refreshLayout.isRefreshing) {
                binding.toolbarShimmer.showShimmer(true)
                locateUser()
            }
        }
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    open fun locateUser() {
        logit("Trying to locate")
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                viewmodel.locationLiveData.postValue(it)
            } else {
                fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    object : CancellationToken() {
                        override fun isCancellationRequested(): Boolean {
                            return false
                        }

                        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                            return this
                        }

                    }).addOnSuccessListener {
                    viewmodel.locationLiveData.postValue(it)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION_ACCESS) {
            locateUser()
        }
    }

    private fun showEnableGPSDialog() {
        val mReqHigh =
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        val result: Task<LocationSettingsResponse> = LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                    .addLocationRequest(mReqHigh)
                    .build()
            )

        result.addOnFailureListener {
            val resolvable = it as ResolvableApiException
            when (resolvable.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                    try {
                        resolvable.startResolutionForResult(
                            this,
                            REQUEST_LOCATION_ACCESS
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
            }
        }
    }
}