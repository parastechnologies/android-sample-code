package com.in2bliss.data.sharedPreferences

import android.content.SharedPreferences
import com.google.gson.Gson
import com.in2bliss.data.model.LogInResponse
import javax.inject.Inject

class SharedPreference @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private var streakStatus="isSteak7Showed"

    companion object {
        const val USER_DATA = "USER_DATA"
    }

    fun showed7Streak(){
        sharedPreferences.edit().putBoolean(streakStatus,true).apply()
    }
    fun isShowed7Streak():Boolean{
        return sharedPreferences.getBoolean(streakStatus,false)
    }

    var userData: LogInResponse?
        get() {
            return Gson().fromJson(
                getString(), LogInResponse::class.java
            )
        }
        set(value) {
            putString(
                value = Gson().toJson(value)
            )
        }

    private fun putString(
        value: String?
    ) {
        sharedPreferences.edit().putString(
            USER_DATA, value
        ).apply()
    }

    private fun getString(
    ): String? {
        return sharedPreferences.getString(USER_DATA, null)
    }
}