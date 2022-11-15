package com.app.muselink.commonutils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.app.muselink.MainApplication.Companion.getApplicationInstance
import com.app.muselink.constants.AppConstants
import com.app.muselink.model.responses.GetUserDetails
import com.app.muselink.model.ui.UserDetails
import java.lang.Exception

object SharedPrefs {

    private const val preferenceName = "MUSELINK"
    private const val prefNameFirstTime = "prefFirstTime"
    const val TUTORIAL_COUNTS = "TUTORIAL_COUNTS"
    private var pref: SharedPreferences? = null
    private var prefFirstTime: SharedPreferences? = null

    init {
        pref = getApplicationInstance().getSharedPreferences(
            preferenceName, Context.MODE_PRIVATE
        )
        prefFirstTime = getApplicationInstance().getSharedPreferences(
            prefNameFirstTime, Context.MODE_PRIVATE
        )
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
        pref?.let {
            with(it.edit()) {
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
    }

    /**
     * Function which will return the integer value saved in the preference corresponds to the given preference key
     */
    fun getInt(preferenceKey: String): Int {
        return try{
            pref?.getInt(preferenceKey, 0) ?: 0
        }catch (e:Exception){
            0
        }
    }

    /**
     * Function which will return the float value saved in the preference corresponds to the given preference key
     */
    fun getFloat(preferenceKey: String): Float {
        return pref?.getFloat(preferenceKey, 0f) ?: 0f
    }

    /**
     * Function which will return the long value saved in the preference corresponds to the given preference key
     */
    fun getLong(preferenceKey: String): Long {
        return pref?.getLong(preferenceKey, 0L) ?: 0L
    }

    /**
     * Function which will return the boolean value saved in the preference corresponds to the given preference key
     */
    fun getBoolean(preferenceKey: String): Boolean {
        return pref?.getBoolean(preferenceKey, false) ?: false
    }

    fun getFirstTime(preferenceKey: String): Boolean {
        return prefFirstTime?.getBoolean(preferenceKey, false) ?: false
    }

    fun setFirstTime(preferenceKey: String, value: Boolean) {
        prefFirstTime?.edit()?.putBoolean(preferenceKey, value)?.apply()

    }

    fun setBoolean(preferenceKey: String, value: Boolean) {
        saveToPreference(preferenceKey, value)
    }

    /**
     * Function which will return the string value saved in the preference corresponds to the given preference key
     */
    fun getString(preferenceKey: String): String {
        return pref?.getString(preferenceKey, "") ?: ""
    }

    private fun remove(preferenceKey: String) {
        pref?.let {
            with(it.edit()) {
                remove(preferenceKey)
                apply()
            }
        }
    }

    fun clearPreference() {

        val currentLat = getString(AppConstants.CURRENT_LATITUDE)
        val currentLng = getString(AppConstants.CURRENT_LONGITUDE)
        val language_selection = getString(AppConstants.PREFS_LANGUAGE_SELECTION)

        pref?.let {
            with(it.edit()) {
                clear()
                apply()
                save(AppConstants.CURRENT_LATITUDE, currentLat)
                save(AppConstants.CURRENT_LONGITUDE, currentLng)
                save(AppConstants.PREFS_LANGUAGE_SELECTION, language_selection)
            }
        }

    }

    fun saveProfileCreated(profileCreated: Int) {
        save(AppConstants.PREFS_PROFILE_CREATED, profileCreated)
    }

    fun isProfileApproved(): Boolean {
        return getInt(AppConstants.PREFS_PROFILE_APPROVAL) != 0
    }

    fun isProfileCreated(): Boolean {
        return getInt(AppConstants.PREFS_PROFILE_CREATED) != 0
    }

    fun isDriverCreated(): Boolean {
        return getInt(AppConstants.PREFS_DRIVER_ID) != 0
    }

    fun getDrivertId(): String? {
        return getString(AppConstants.PREFS_DRIVER_ID)
    }

    fun saveLanguageSelection(language: String) {
        save(AppConstants.PREFS_LANGUAGE_SELECTION, language)
    }

    fun getLanguageSeleted(): String? {
        return getString(AppConstants.PREFS_LANGUAGE_SELECTION)
    }

    fun isLanguageSelected(): Boolean {
        return getString(AppConstants.PREFS_LANGUAGE_SELECTION) != ""
    }

    fun getUserProfilePic(): String? {
        return getString(AppConstants.PREFS_PROFILE_IMAGE)
    }

    fun saveUserProfilePic(url: String?) {
        save(
            AppConstants.PREFS_PROFILE_IMAGE,
            url
        )
    }

    fun saveUser(user: UserDetails?) {

        Log.d("sadadadasadsasd","hello "+user?.susbscriptionStatus.toString())

        user?.let {
            save(
                AppConstants.PREFS_USER_ID,
                user.id
            )

            save(
                AppConstants.PREFS_CHAT_UNIQUE_NO,
                user.Chat_Uniq_Number
            )

            save(
                AppConstants.PREFS_PROFILE_IMAGE,
                user.id
            )

            save(
                AppConstants.PREFS_EMAIL,
                user.Email
            )

            save(
                AppConstants.PREFS_PHONE,
                user.phone
            )

            save(
                AppConstants.PREFS_USER_NAME,
                user.User_Name
            )

            save(
                AppConstants.PREFS_ACCOUNT_STATUS,
                user.Account_Status
            )

            save(
                AppConstants.PREFS_CREATED_DATE,
                user.Created_Date
            )
            save(
                AppConstants.User_Name,
                user.User_Name
            )
            save(
                AppConstants.COUNTRY_NAME,
                user.Country_Name
            )

            save(
                AppConstants.PREFS_SUBSCRIPTION_STATUS,
                user.susbscriptionStatus
            )

        }
    }

    fun getUser(): GetUserDetails {
        val user = GetUserDetails()

        user.name =
            getString(
                AppConstants.PREFS_NAME
            )

        user.Chat_Uniq_Number =
            getString(
                AppConstants.PREFS_CHAT_UNIQUE_NO
            )

        user.User_Name =
            getString(
                AppConstants.User_Name
            )

        user.firstname =
            getString(
                AppConstants.PREFS_FIRSTNAME
            )

        user.lastname =
            getString(
                AppConstants.PREFS_LASTNAME
            )

        user.profileImage =
            getString(
                AppConstants.PREFS_PROFILE_IMAGE
            )

        user.location =
            getString(
                AppConstants.PREFS_LOCATION
            )

        user.gender =
            getString(
                AppConstants.PREFS_GENDER
            )

        user.firebaseToken =
            getString(
                AppConstants.PREFS_TOKEN_ID
            )

        user.id =
            getString(
                AppConstants.PREFS_USER_ID
            )

        user.phone =
            getString(
                AppConstants.PREFS_PHONE
            )

        user.email =
            getString(
                AppConstants.PREFS_EMAIL
            )
        user.countryName =
            getString(
                AppConstants.COUNTRY_NAME
            )


        return user
    }

    fun getCurrentLongitude(): String {
        return pref?.getString(
            AppConstants.CURRENT_LONGITUDE, ""
        ) ?: ""
    }

    fun getCurrentLatitude(): String {
        return pref?.getString(
            AppConstants.CURRENT_LATITUDE, ""
        ) ?: ""
    }
    fun subscriptionStatus(): Boolean {
        return getInt(AppConstants.PREFS_SUBSCRIPTION_STATUS) != 0
    }

    fun isUserLogin(): Boolean {
        return getUser().isHaveUserId();
    }

    fun isHaveUserId() {

    }

    fun setDeviceToken(token: String) {
        save(
            AppConstants.PREF_DEVICE_TOKEN,
            token
        )
    }

    fun setIsFCMRegistered(b: Boolean) {
        save(
            AppConstants.PREF_FCM_REGISTERED,
            b
        )
    }

    fun isFCMRegistered(): Boolean {
        return getBoolean(
            AppConstants.PREF_FCM_REGISTERED
        )
    }

    fun getDeviceToken(): String? {
        return pref?.getString(
            AppConstants.PREF_DEVICE_TOKEN, ""
        ) ?: ""
    }


    fun clearUser() {
        remove(
            AppConstants.PREF_DEVICE_TOKEN
        )
        remove(
            AppConstants.PREF_FCM_REGISTERED
        )
        remove(
            AppConstants.PREFS_NAME
        )
        remove(
            AppConstants.PREFS_TOKEN_ID
        )
        remove(
            AppConstants.PREFS_UID
        )

    }

    fun getUserToken(): String {
        return "Bearer " + getString(
            AppConstants.PREFS_TOKEN_ID
        )
    }

    fun getSignoutToken(): String? {
        return getString(
            AppConstants.PREFS_TOKEN_ID
        )
    }


    fun rememberMe(remember: Boolean, password: String, email: String) {
        if (remember) {
            save(
                AppConstants.PREFS_PASS,
                password
            )
            save(
                AppConstants.PREFS_EMAIL,
                email
            )
            save(
                AppConstants.PREFS_REMEMBERME,
                remember
            )
        } else {
            remove(
                AppConstants.PREFS_PASS
            )
            remove(
                AppConstants.PREFS_EMAIL
            )
            remove(
                AppConstants.PREFS_REMEMBERME
            )
        }
    }

    fun shouldRemember(): Boolean {
        return pref?.getBoolean(
            AppConstants.PREFS_REMEMBERME, true
        ) ?: true
    }

    fun getMail(): String? {
        return getString(
            AppConstants.PREFS_EMAIL
        )
    }

    fun getUserId(): String? {
        return getString(
            AppConstants.PREFS_USER_ID
        )
    }

    fun getPassword(): String? {
        return getString(
            AppConstants.PREFS_PASS
        )
    }

}