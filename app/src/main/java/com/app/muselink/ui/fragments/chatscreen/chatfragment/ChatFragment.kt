package com.app.muselink.ui.fragments.chatscreen.chatfragment

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.app.muselink.R
import com.app.muselink.base.BaseFragment
import com.app.muselink.ui.activities.home.HomeActivity
import com.app.muselink.ui.fragments.chatscreen.NotificationsFragment
import com.app.muselink.ui.fragments.chatscreen.RecentChatFragment
import kotlinx.android.synthetic.main.fragment_chat.*
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class ChatFragment : BaseFragment() {
    private var rootView: View? = null
    var notificationCounts: TextView? = null
    var messageCount: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initView()
    }

    private fun initView() {
        notificationCounts = rootView?.findViewById(R.id.notificationCounts)
        messageCount = rootView?.findViewById(R.id.messageCount)
    }

    fun updateText(s: String, type: String) {
         if (type == "Notification") {
            notificationCounts?.text = s
        } else {
            messageCount?.text = s
        }

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as HomeActivity).showHideSearchButton(false)
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as HomeActivity).showHideSearchButton(true)
    }

    private fun setListeners() {
        nmcUnreadMessage.setOnClickListener {
            changeImageColorSelected(imgChatUnreadMessage, imgChatNotification)
            showHideView(viewChatUnReadMessage, viewChatNotification)
            selectUnSelectTab(nmcUnreadMessage, nmcNotification)
            openFragment(RecentChatFragment(), "RecentChatFragment")
        }
        nmcNotification.setOnClickListener {
            changeImageColorSelected(imgChatNotification, imgChatUnreadMessage)
            showHideView(viewChatNotification, viewChatUnReadMessage)
            selectUnSelectTab(nmcNotification, nmcUnreadMessage)
            openFragment(NotificationsFragment(), "NotificationsFragment")
        }
        nmcNotification.performClick()
    }

    fun changeImageColorSelected(selectImg: AppCompatImageView, unSelectImg1: AppCompatImageView) {
        selectImg.setColorFilter(
            ContextCompat.getColor(requireActivity(), android.R.color.black),
            PorterDuff.Mode.SRC_IN
        )
        unSelectImg1.setColorFilter(
            ContextCompat.getColor(
                requireActivity(),
                R.color.color_icon_grey
            ), PorterDuff.Mode.SRC_IN
        )
    }

    fun openFragment(fragment: Fragment, id: String) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.containerChat, fragment, id)
        transaction.commit()
    }

    private fun showHideView(selectedView: View, unSelectView: View) {
        selectedView.visibility = View.VISIBLE
        unSelectView.visibility = View.GONE
    }

    private fun selectUnSelectTab(selctedCard: NeumorphCardView, unselectCard: NeumorphCardView) {
        selctedCard.setShapeType(ShapeType.PRESSED)
        unselectCard.setShapeType(ShapeType.FLAT)
    }
}