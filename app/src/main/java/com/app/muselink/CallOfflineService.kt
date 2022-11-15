package com.app.muselink

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.app.muselink.retrofit.ApiServices
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class CallOfflineService : Service() {
    @Inject
    lateinit var repository: ApiServices
    var userId: String? = ""
    var status: String? = ""
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        userId = intent.getStringExtra("userId")
        status = intent.getStringExtra("status")
        val map = hashMapOf("userId" to userId.toString(), "status" to status.toString())
        repository.offlineUser(map).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                stopSelf()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}