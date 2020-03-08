package dev.smoketrees.twist.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.ui.home.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class BaseFragment<VM : ViewModel>(@LayoutRes layout: Int, private val clazz: KClass<VM>, private val share: Boolean = false): Fragment(layout) {

    protected val viewModel: VM by lazy { if (share) sharedViewModel(clazz) else viewModel(clazz)}.value

    private val ctx by lazy { requireContext() }

    private val m: Lazy<VM> by lazy { sharedViewModel(clazz) }

    protected val appBar by lazy { (requireActivity() as MainActivity).appbar }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // To make sure App bar is always visible after fragment change
        appBar.setExpanded(true, true)
    }
}