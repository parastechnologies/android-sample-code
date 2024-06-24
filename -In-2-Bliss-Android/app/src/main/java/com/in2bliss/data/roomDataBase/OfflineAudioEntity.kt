package com.in2bliss.data.roomDataBase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.in2bliss.utils.constants.AppConstant

@Entity(tableName = "offline_audio")
data class OfflineAudioEntity(
    @PrimaryKey(autoGenerate = true)
    var roomId: Int?=null,
    val id: Int,
    val userId: Int,
    val title: String,
    val musicUrl: String,
    val description: String,
    val musicFilePath: String,
    val musicFileName: String,
    val musicImageFilePath: String,
    val musicImageFileName: String,
    var isDelete: Boolean,
    val backMusicFilePath: String? = null,
    val backMusicFileName: String? = null,
    val backMusicImageFilePath: String? = null,
    val backMusicImageFileName: String? = null,
    val backgroundMusicTitle: String? = null,
    val downloadCategoryName: AppConstant.HomeCategory? = null,
    val isCustomizationEnabled: Boolean= false,
    var customHourAffirmation:Int?=null,
    var customMinAffirmation:Int?=null,
    var customHourMusic:Int?=null,
    var customMinMusic:Int?=null
)
