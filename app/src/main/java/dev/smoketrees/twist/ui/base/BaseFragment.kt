package dev.smoketrees.twist.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dev.smoketrees.twist.ui.home.MainActivity
import dev.smoketrees.twist.utils.Messages
import dev.smoketrees.twist.utils.autoCleared
import dev.smoketrees.twist.utils.hide
import dev.smoketrees.twist.utils.show
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

abstract class BaseFragment<DB : ViewDataBinding, VM : ViewModel>(
    @LayoutRes private val layout: Int,
    private val clazz: KClass<VM>,
    private val share: Boolean = false
) : Fragment() {


    abstract val bindingVariable: Int

    protected var dataBinding by autoCleared<DB>()

    protected val viewModel: VM by lazy { if (share) sharedViewModel(clazz) else viewModel(clazz) }.value

    protected val ctx by lazy { requireContext() }

    private val m: Lazy<VM> by lazy { sharedViewModel(clazz) }

    protected val appBar by lazy { (requireActivity() as MainActivity).appbar }

    protected fun showLoader() = (requireActivity() as MainActivity).showLoader()
    protected fun hideLoader() = (requireActivity() as MainActivity).hideLoader()

    protected fun notice(err_code: Int?) = (requireActivity() as MainActivity).notice(err_code)
    protected fun noticeClear() = (requireActivity() as MainActivity).noticeClear()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layout, container, false)
        dataBinding.apply {
            lifecycleOwner = lifecycleOwner
            setVariable(bindingVariable, viewModel)
            executePendingBindings()
        }
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // To make sure App bar is always visible after fragment change
        appBar.setExpanded(true, true)
    }

    override fun onDestroyView() {
        hideLoader()
        super.onDestroyView()
    }
}