package com.in2bliss.ui.activity.home.fragment.notification.unread

import com.in2bliss.R
import com.in2bliss.base.BaseViewModel
import com.in2bliss.data.model.NotificationList
import com.in2bliss.ui.activity.home.notification.NotificationAdapter
import javax.inject.Inject

class UnReadVM @Inject constructor() : BaseViewModel(){


    val notificationAdapter by lazy {
        NotificationAdapter()
    }

    val notificationList = arrayListOf(
        NotificationList(
            R.drawable.journal_icon,
            "Journal",
            R.string.journal_description.toString()
        ),
        NotificationList(
            R.drawable.quotes_icon,
            "Quotes",
            R.string.journal_description.toString()        ),
        NotificationList(
            R.drawable.trial_icon,
            "You're on your 5th day of the trial",
            R.string.journal_description.toString()        ),
        NotificationList(
            R.drawable.upload_icon,
            "New content uploaded",
            R.string.journal_description.toString()        )
    )


    override fun retryApiRequest(apiName: String) {
    }
}