package com.mindbyromanzanoni.data.response.eventDetails

import com.mindbyromanzanoni.base.BaseResponse

data class EventDetailsResponse(
    val `data`: Details) : BaseResponse()
data class Details(
    val createdOn: String,
    var eventDate: String?=null,
    val eventDesc: String,
    val eventId: Int,
    var eventTime: String?=null,
    val isImage: Boolean,
    var isFavoritedbyUser: Boolean,
    val mediaPath: String,
    val title: String,
    var totalComments: Int,
    var totalFavourites: Int,
    val totalShared: Int,
    val videoThumbImage: String,
    val usersListWhoFavouriteEvent: ArrayList<WhoLikesFavouriteList>? = arrayListOf(),
    val zoomLink: String?=null,
    val commentList : ArrayList<CommentListResponse>? = arrayListOf(),
    val docFileName: String?,
    val type:Int=1,
    val videoHlsLink:String?=null
)

data class WhoLikesFavouriteList(
    val userId: Int,
    val userImage: String="",
    val userName: String?="",
    val favEventId: Int=-1,
)
