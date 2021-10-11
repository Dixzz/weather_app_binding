package demo.weather.base

import androidx.recyclerview.widget.DiffUtil
import demo.weather.repos.structure.WeatherData
import demo.weather.repos.structure.WeatherData2

@Suppress("UNCHECKED_CAST")
class BaseDiffUtil<X, Y>(
    private val newList: List<X>,
    private val oldList: List<Y>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            newList[newItemPosition] is WeatherData -> {
                val list = newList as List<WeatherData>
                val list2 = oldList as List<WeatherData>
                list[newItemPosition].dt == list2[oldItemPosition].dt
            }
            newList[newItemPosition] is WeatherData2 -> {
                val list = newList as List<WeatherData2>
                val list2 = oldList as List<WeatherData2>
                list[newItemPosition].dt == list2[oldItemPosition].dt
            }
            else -> newList[newItemPosition] == oldList[oldItemPosition]
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}