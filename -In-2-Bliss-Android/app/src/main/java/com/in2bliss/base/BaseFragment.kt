package com.in2bliss.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<VB : ViewDataBinding>(
    private val layoutInflater: (inflater: LayoutInflater) -> VB
) : Fragment(){

    private lateinit var viewBinding : VB

    val binding : VB
        get() = viewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = layoutInflater.invoke(inflater)
        return viewBinding.root
    }
}