package demo.weather.api

import android.os.Build
import demo.weather.BuildConfig
import demo.weather.Constants.Companion.WEATHER_ROOT_URL
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.androidannotations.annotations.EBean
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


@EBean(scope = EBean.Scope.Singleton)
open class NetworkRequest {
    val client by lazy { invoke(WEATHER_ROOT_URL) }
    private val queryInterceptor by lazy {
        Interceptor { chain ->
            var request: Request = chain.request()
            val url: HttpUrl = request.url.newBuilder()
                .addQueryParameter("appid", BuildConfig.API_KEY)
                .addQueryParameter("units", "metric")
//                .addQueryParameter("exclude", "minutely,alerts")
                .build()

            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }
    }
    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(queryInterceptor)
        readTimeout(2, TimeUnit.MINUTES)
        connectTimeout(1, TimeUnit.MINUTES)
    }

    private operator fun invoke(baseUrl: String): Api =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
}