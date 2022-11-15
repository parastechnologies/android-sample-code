package com.app.muselink.ui.fragments.muzelinkpro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.muselink.R
import com.app.muselink.base.BaseFragment

class ProSlide4 : BaseFragment(){

    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_proslide4, container, false)
        return rootView
    }




}