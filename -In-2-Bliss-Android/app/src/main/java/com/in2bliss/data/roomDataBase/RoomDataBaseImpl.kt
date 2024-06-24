package com.in2bliss.data.roomDataBase

import com.in2bliss.domain.RoomDataBaseInterface

class RoomDataBaseImpl(
    private val dataBase: OfflineAudioDao
) : RoomDataBaseInterface {

    /**
     * Inserting data in room data base
     * @param data
     * */
    override suspend fun insert(data  : OfflineAudioEntity) {
        dataBase.insert(data)
    }

    /**
     * Deleting the data from the room data base
     * @param data
     * */
    override suspend fun delete(data: OfflineAudioEntity): Int {
        return dataBase.delete(data)
    }

    /**
     * Getting download file list
     * */
    override suspend fun getList(): List<OfflineAudioEntity> {
        return dataBase.getAudioList()
    }
}