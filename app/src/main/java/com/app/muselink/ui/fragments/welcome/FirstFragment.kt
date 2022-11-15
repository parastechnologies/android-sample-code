package com.app.muselink.ui.fragments.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.muselink.R
import com.app.muselink.util.springAnimation

class FirstFragment : Fragment() {

    private var view1: TextView? = null
    private var view2: TextView? = null
    private var view3: TextView? = null
    private var dot1: ImageView? = null
    private var dot2: ImageView? = null
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?{
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_first_, container, false)
            initView(rootView)
        }
        return rootView
    }

    private fun initView(view: View?) {
        view1 = view?.findViewById(R.id.view1)
        view2 = view?.findViewById(R.id.view2)
        view3 = view?.findViewById(R.id.view3)
        dot1 = view?.findViewById(R.id.dot1)
        dot2 = view?.findViewById(R.id.dot2)
        view1?.springAnimation(0f)
        dot1?.springAnimation(0f)
        view2?.springAnimation(1.5f)
        dot2?.springAnimation(1.5f)
        view3?.springAnimation(2.5f)
    }

    fun onMoveToSecondScreen() {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        transaction.replace(R.id.frame_container, SecondFragment())
        transaction.commit()
    }
}