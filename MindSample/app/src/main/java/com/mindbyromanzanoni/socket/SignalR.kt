package com.mindbyromanzanoni.socket

import android.annotation.SuppressLint
import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


/**
 * Created by Developer on 27/12/23
 */
@SuppressLint("CheckResult")
object SignalR {
    var hubConnection: HubConnection? = null
    private var reConnect: Boolean = true
    fun connectSocket(
        token: String,
        hub: ((hub: HubConnection) -> Unit)? = null,
        error: ((error: Throwable) -> Unit)? = null) {
        try {
            reConnect = true
            if (hubConnection == null) {
                hubConnection =
                    HubConnectionBuilder.create("CHAT HUB URL")
                        .withAccessTokenProvider(Single.defer {
                            Single.just(token.replace("Bearer",""))
                        })
                        .build()
             }
            if (hubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
                hubConnection?.start()?.timeout(20000, TimeUnit.MILLISECONDS)?.subscribe({
                    Log.e("SignalR", "Connection Status-> ${hubConnection?.connectionState}")
                    hub?.invoke(hubConnection!!)
                }, {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)
                        connectSocket(token, { hub?.invoke(hubConnection!!) })
                        Log.e("SignalR","Connection Status error-> ${hubConnection?.connectionState} error->${it.message}")
                        error?.invoke(it)
                    }
                })
            } else if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                hub?.invoke(hubConnection!!)
            }
            hubConnection?.onClosed {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(timeMillis = 1000)
                    if (reConnect) {
                        hubConnection = null
                        connectSocket(token, { hub?.invoke(hubConnection!!) })
                    } else {
                        hubConnection = null
                    }
                }
                Log.e("SignalR", "Closed Status-> ${hubConnection?.connectionState}")
            }
        } catch (e: Exception) {
            Log.e("SignalR", "error ${e.message}")
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                connectSocket(token, hub, error)
            }
        }
    }
    fun closeConnection() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            reConnect = false
            hubConnection?.stop()?.subscribe({
                Log.e("SignalR", "Connection Status-> ${hubConnection?.connectionState}")
            }, {
                Log.e(
                    "SignalR",
                    "Connection Status error-> ${hubConnection?.connectionState} error->${it.message}"
                )
            })
        }
    }

}