package com.in2bliss.utils.notification

data class NotificationModel(
    var id: String? = "",
    var type: String = "",
    var title: String = "",
    var message: String = "",
    var dataId: String? = ""
)

