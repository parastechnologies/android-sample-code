package com.app.muselink.ui.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import com.app.muselink.util.AutoClearedValue

abstract class BaseFragmentBinding<B : ViewDataBinding> : Fragment() {
    protected var binding: B? = null
    var hud:KProgressHUD?=null
    protected fun getFragmentArguments(): Bundle? {
        return arguments
    }
    protected abstract fun getLayout(): Int
    /**
     * [onCreateView]
     * */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding: B = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        binding = AutoClearedValue(this, dataBinding).get()
        return binding!!.root
    }

    /**
     * [onViewCreated]
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hud= KProgressHUD.create(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.0f)
    }

    /**
     * Hide Progress
     * */
    fun hideLoader() {
        if (hud != null && hud!!.isShowing) {
            hud?.dismiss()
        }
    }

    /**
     * Show Progress
     * */
    fun showLoader() {
        hud?.show()
    }
}