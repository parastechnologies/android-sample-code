package com.highenergymind.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.highenergymind.data.Data
import javax.inject.Inject

class SharedPrefs @Inject constructor(private var sharePref: SharedPreferences) {
    private val token = "token"
    private val userData = "userData"
    private val isUserLogin = "isUserLogin"
    private val trackChoice = "trackChoice"

    fun saveTrackChoice(pref: Boolean) {
        save(trackChoice, pref)
    }

    fun getTrackChoice(): Boolean {
        return getBoolean(trackChoice)
    }


    /**
     * Function which will save the integer value to preference with given key
     */

    fun save(preferenceKey: String, integerValue: Int) {
        saveToPreference(
            preferenceKey,
            integerValue
        )
    }

    fun contains(preferenceKey: String): Boolean {
        return sharePref.contains(preferenceKey)
    }

    /**
     * Function which will save the double value to preference with given key
     */
    fun save(preferenceKey: String, doubleValue: Float) {
        saveToPreference(
            preferenceKey,
            doubleValue
        )
    }

    /**
     * Function which will save the long value to preference with given key
     */
    fun save(preferenceKey: String, longValue: Long) {
        saveToPreference(
            preferenceKey,
            longValue
        )
    }

    /**
     * Function which will save the boolean value to preference with given key
     */
    fun save(preferenceKey: String, booleanValue: Boolean) {
        saveToPreference(
            preferenceKey,
            booleanValue
        )
    }

    /**
     * Function which will save the string value to preference with given key
     */
    fun save(preferenceKey: String, stringValue: String?) {
        stringValue?.let {
            saveToPreference(
                preferenceKey,
                stringValue
            )
        }
    }


    /**
     * General function to save preference value
     */
    private fun saveToPreference(key: String, value: Any) {
        with(sharePref.edit()) {
            when (value) {
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
            }
            apply()
        }
    }

    /**
     * Function which will return the integer value saved in the preference corresponds to the given preference key
     */
    fun getInt(preferenceKey: String): Int {
        return sharePref.getInt(preferenceKey, 0)
    }

    /**
     * Function which will return the float value saved in the preference corresponds to the given preference key
     */
    fun getFloat(preferenceKey: String): Float {
        return sharePref.getFloat(preferenceKey, 0f)
    }

    /**
     * Function which will return the long value saved in the preference corresponds to the given preference key
     */
    fun getLong(preferenceKey: String): Long {
        return sharePref.getLong(preferenceKey, 0L)
    }

    /**
     * Function which will return the boolean value saved in the preference corresponds to the given preference key
     */
    fun getBoolean(preferenceKey: String): Boolean {
        return sharePref.getBoolean(preferenceKey, false)
    }

    /**
     * Function which will return the string value saved in the preference corresponds to the given preference key
     */
    fun getString(preferenceKey: String): String {
        return sharePref.getString(preferenceKey, "") ?: ""
    }

    fun remove(preferenceKey: String) {
        with(sharePref.edit()) {
            remove(preferenceKey)
            apply()
        }
    }


    fun clearPreference() {
        with(sharePref.edit()) {
            val isSeen = sharePref.getBoolean(AppConstant.IS_SEEN, false)
            clear()
            commit()
            apply()
            sharePref.edit().putBoolean(AppConstant.IS_SEEN, isSeen).apply()
        }
    }


    fun setLoginStatusTrue() {
        save(isUserLogin, true)
    }

    fun getLoginStatus(): Boolean {
        return getBoolean(isUserLogin)
    }

    fun saveAccessToken(tokenValue: String) {
        save(token, tokenValue)
    }

    fun saveUserData(data: Data) {
        save(userData, Gson().toJson(data))
    }

    fun getUserData(): Data? {
        return try {
            Gson().fromJson(getString(userData), Data::class.java)
        } catch (e: Exception) {
            null
        }
    }


    fun getAccessToken(): String? {
        return if (contains(token)) getString(token) else null
    }
}