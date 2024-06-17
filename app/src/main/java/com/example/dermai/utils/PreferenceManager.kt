package com.example.dermai.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.dermai.utils.Constants.IS_LOGGED_IN
import com.example.dermai.utils.Constants.PREFS_NAME
import com.example.dermai.utils.Constants.USER_ID
import com.example.dermai.utils.Constants.USER_IMAGE

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun setIsNotFirstTime(isNotFirstTime: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.IS_NOT_FIRST_TIME, isNotFirstTime)
        editor.apply()
    }

    fun getIsNotFirstTime(): Boolean {
        return sharedPreferences.getBoolean(Constants.IS_NOT_FIRST_TIME, false)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false)
    }

    fun clearPreferences() {
        val editor = sharedPreferences.edit()
        editor.remove(IS_LOGGED_IN)
        editor.remove(USER_ID)
        editor.remove(USER_IMAGE)
        editor.apply()
    }

    fun setUserDetails(userId: String, userImage: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USER_ID, userId)
        editor.putString(USER_IMAGE, userImage)
        editor.apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID, null)
    }

    fun getUserImage(): String? {
        return sharedPreferences.getString(USER_IMAGE, null)
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}