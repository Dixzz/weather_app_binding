package demo.weather

interface Constants {
    companion object {
        //const val RECEIVE_CLUSTER_IMAGES_ROOT_URL = "https://weatherbit-v1-mashape.p.rapidapi.com/"
        const val WEATHER_ROOT_URL = "http://api.openweathermap.org/"
        val WEATHER_ICON_URL = "http://openweathermap.org/img/wn/%s@2x.png"
    }
}