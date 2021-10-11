package demo.weather.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding

@Suppress("UNCHECKED_CAST")
open class BaseFragment<B : ViewBinding, T : FragmentActivity>(private val bindingFactory: (LayoutInflater) -> B) :
    Fragment() {

    lateinit var baseActivity: T
    val binding: B by lazy { bindingFactory(layoutInflater) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as T
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}