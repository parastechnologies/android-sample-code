package com.app.muselink.ui.fragments.welcome
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.muselink.R
import com.app.muselink.util.springAnimation_

class SecondFragment : Fragment() {
    private var view1: ImageView? = null
    private var view2: ImageView? = null
    private var view3: ImageView? = null
    private var view4: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.fragment_second_, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View?) {
        view1 = view?.findViewById(R.id.view1)
        view2 = view?.findViewById(R.id.view2)
        view3 = view?.findViewById(R.id.view3)
        view4 = view?.findViewById(R.id.view4)

        view1?.springAnimation_(1f)
        view2?.springAnimation_(2f)
        view3?.springAnimation_(3f)
        view4?.springAnimation_(4f)
    }

    fun onMoveToThirdScreen() {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right)
        transaction.replace(R.id.frame_container, ThirdFragment())
        transaction.commit()
    }

}