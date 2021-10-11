package demo.weather.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.squareup.picasso.Picasso
import demo.weather.R
import demo.weather.base.adapters.BaseRecyclerViewHolderClickable
import demo.weather.databinding.RecyclerDailyForecastItemBinding
import demo.weather.databinding.RecyclerHourlyForecastItemBinding
import demo.weather.repos.structure.WeatherData
import demo.weather.repos.structure.WeatherData2
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class BaseRecyclerViewAdapter<X, Y : ViewBinding>(
    private val bindingFactory: (LayoutInflater) -> Y,
    private var list: List<X>,
    private val onClickI: OnItemClickI? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var recBinding: Y

    private val simpleDateFormat by lazy { SimpleDateFormat("dd/MM", Locale.getDefault()) }
    private val simpleHourFormat by lazy { SimpleDateFormat("hh:mmaaa", Locale.getDefault()) }
    var lastPosition = -1
    lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        recBinding = bindingFactory(LayoutInflater.from(context))
        return BaseRecyclerViewHolderClickable(recBinding.root, onClickI)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        setAnimation(holder.itemView, position)
        if (list.first() is WeatherData2) {
            val list = list[position] as WeatherData2
            val binding = recBinding as RecyclerDailyForecastItemBinding
            Picasso.get()
                .load("http://openweathermap.org/img/wn/${list.weather.first().icon}@4x.png")
                .into(binding.weatherIcon)
            binding.min = list.temp.min.toDouble().roundToInt().toString() + "°"
            binding.max = list.temp.max.toDouble().roundToInt().toString() + "°"
            binding.date = simpleDateFormat.format(TimeUnit.MILLISECONDS.toMicros(list.dt))
        } else if (list.first() is WeatherData) {
            val list = list[position] as WeatherData
            val binding = recBinding as RecyclerHourlyForecastItemBinding
            Picasso.get()
                .load("http://openweathermap.org/img/wn/${list.weather.first().icon}@4x.png")
                .into(binding.weatherIcon)
            binding.time = simpleHourFormat.format(TimeUnit.MILLISECONDS.toMicros(list.dt))
            binding.temp = list.temp.toDouble().roundToInt().toString() + "°"
        }
    }


    fun update(newList: List<X>?) {
        if (newList == null)
            return
        val m = BaseDiffUtil(newList, list)
        list = newList
        DiffUtil.calculateDiff(m).dispatchUpdatesTo(this)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.fade_in_1000)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickI {
        fun click(pos: Int, v: View)
    }
}