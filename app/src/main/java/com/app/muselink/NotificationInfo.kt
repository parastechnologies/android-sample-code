package com.app.muselink

const val NOTIFICATION_TYPE_ORDER_PLACED = "Order Placed"
const val NOTIFICATION_TYPE_SUCCCESSFUL = "Successful Transaction"
const val NOTIFICATION_TYPE_FAIL = "Fail Transaction"
const val NOTIFICATION_TYPE_START_SERVICE = "Started service"
const val NOTIFICATION_TYPE_LEFT_HOME_SERVICE = "Left from Home"

data class NotificationInfo(val type: String?, val id: Int?) {
    fun isOrderPlacedNotification() = type == NOTIFICATION_TYPE_ORDER_PLACED
    fun isSuccessfulNotification() = type == NOTIFICATION_TYPE_SUCCCESSFUL
    fun isChatNotifications() = type == NotificationConstants.CHAT_MESSAGE
    fun isFailNotifications() = type == NOTIFICATION_TYPE_FAIL
    fun isStartServiceNotifications() = type == NOTIFICATION_TYPE_START_SERVICE
    fun isLeftHomeNotifications() = type == NOTIFICATION_TYPE_LEFT_HOME_SERVICE
}