package com.mindbyromanzanoni.socket

import android.util.Log
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import com.mindbyromanzanoni.data.response.userMessageList.MessageListResponse
import com.mindbyromanzanoni.data.response.userMessageList.MessageResponse
import com.mindbyromanzanoni.data.response.userMessageList.UserMessageResponse
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.utils.constant.AppConstants
import io.reactivex.rxjava3.core.Single


class SignalRManager {
    private var hubConnection: HubConnection? = null

    /** Connect Socket SignalR to Server*/
    fun connectToHub(token: String) {
        try {
            val hubUrl = "https://api.mindbyroman.com/chatHub"
            hubConnection = HubConnectionBuilder.create(hubUrl).withAccessTokenProvider(Single.defer { Single.just(token.replace("Bearer","")) }).build()
            hubConnection!!.serverTimeout = 10000000
            hubConnection!!.keepAliveInterval = 10000000
            hubConnection!!.start().blockingAwait()
            //socketClosed()
            hubConnection!!.onClosed {
                connectToHub(token)
            }
        }catch (_:Exception){}
    }
    private fun socketClosed(){}
    /** After Command -UsersChatList
     * Get User List Response*/
    fun sendCommandForUserList(callback:(UserMessageResponse)->Unit) {
        RunInScope.ioThread {
            hubConnection?.on("ShowChatUsersList",{
                callback(Gson().fromJson(it, UserMessageResponse::class.java))
            }, String::class.java)
            hubConnection?.invoke("UsersChatList")?.subscribe({
               // Log.d("SignalR", "Received response:  ")
            }, { error ->
               // Log.e("SignalR", "Error: ${error.message}")
            },)
        }
    }
    /** After Send Command -ChatList
     * Get User Chat list response*/
    fun sendCommandForChatList(myId:Int,otherUserId:Int,callback:(MessageResponse)->Unit) {
        RunInScope.ioThread {
            hubConnection?.on("ShowChatList",{
                callback(Gson().fromJson(it, MessageResponse::class.java))
            }, String::class.java)
            hubConnection?.invoke("ChatList",myId,otherUserId)?.subscribe({
                Log.d("SignalR", "Received response:  ")
            }, { error ->
                Log.e("SignalR", "Error: ${error.message}")
            },)
        }
    }
    /** After Send Command -SendMessages
     * Get User Chat list response*/
    fun sendCommandForSendMessage(request: HashMap<String, Any>, callback:(MessageListResponse)->Unit) {
        RunInScope.ioThread {
            hubConnection?.on("ReceiveMessage",{
                callback(Gson().fromJson(it, MessageListResponse::class.java))
            }, String::class.java)
            hubConnection?.invoke("SendMessages",request)?.subscribe({
                Log.d("SignalR", "Received response:  ")
            }, { error ->
                Log.e("SignalR", "Error: ${error.message}")
            })
        }
    }
    fun setOnMessageReceivedListener(callback: (MessageListResponse) -> Unit) {
            hubConnection?.on("ReceiveMessage",{
                 callback(Gson().fromJson(it, MessageListResponse::class.java))
            }, String::class.java)
    }
    fun isSignalRConnected(): Boolean {
        return hubConnection?.connectionState == HubConnectionState.CONNECTED
    }
    fun disconnect() {
        hubConnection?.stop()
    }
}


