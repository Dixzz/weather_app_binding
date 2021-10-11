package demo.weather.base.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import demo.weather.base.BaseRecyclerViewAdapter


open class BaseRecyclerViewHolderClickable(
    view: View,
    onItemClick: BaseRecyclerViewAdapter.OnItemClickI? = null
) : RecyclerView.ViewHolder(view) {
    init {
        if (onItemClick != null) {
            itemView.setOnClickListener {
                onItemClick.click(adapterPosition, it)
            }
        }
    }
}