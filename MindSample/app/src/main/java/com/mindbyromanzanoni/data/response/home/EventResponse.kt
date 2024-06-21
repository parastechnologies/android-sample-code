package com.mindbyromanzanoni.data.response.home

import com.google.gson.annotations.SerializedName
import com.mindbyromanzanoni.base.BaseResponse

data class EventResponse(
    val `data`: ArrayList<EventListResponse>,
) : BaseResponse()


data class EventListResponse(
    val docFileName: String?=null,
    val createdOn: String,
    val eventDesc: String,
    val eventDate: String,
    val eventTime: String,
    val eventId: Int,
    val id: Int,
    var isFavoritedbyUser: Boolean,
    var isImage: Boolean,
    val mediaPath: String?=null,
    val title: String,
    var totalComments: Int,
    var totalFavourites: Int,
    val totalShared: Int,
    val usersListWhoFavouriteEvent: Any,
    val zoomLink: String?=null,
    val videoThumbImage: String,
    val type:Int,
    val videoHlsLink:String?=null,
 )

sealed class ScreenType(var type: Int){
    object EVENTS: ScreenType(4)
    object MEDITATION: ScreenType(2)
    object EDIFICATION: ScreenType(1)
    object RESOURCE: ScreenType(3)
}
