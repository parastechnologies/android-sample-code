package com.app.muselink.ui.fragments.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.app.muselink.R
import com.app.muselink.util.springAnimation

class ThirdFragment : Fragment() {

    private var view1: ImageView? = null
    private var view2: ImageView? = null
    private var view3: ImageView? = null
    private var view4: ImageView? = null
    private var view5: ImageView? = null
    private var view6: ImageView? = null
    private var view7: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        view1 = view?.findViewById(R.id.view1)
        view2 = view?.findViewById(R.id.view2)
        view3 = view?.findViewById(R.id.view3)
        view4 = view?.findViewById(R.id.view4)
        view5 = view?.findViewById(R.id.view5)
        view6 = view?.findViewById(R.id.view6)
        view7 = view?.findViewById(R.id.view7)

        view1?.springAnimation(1f)
        view2?.springAnimation(2f)
        view3?.springAnimation(3f)
        view4?.springAnimation(4f)
        view5?.springAnimation(5f)
        view6?.springAnimation(6f)
        view7?.springAnimation(7f)
    }

}