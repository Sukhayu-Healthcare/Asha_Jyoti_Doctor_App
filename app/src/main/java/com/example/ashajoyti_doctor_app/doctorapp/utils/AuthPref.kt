package com.example.ashajoyti_doctor_app.utils

import android.content.Context

object AuthPref {
    private const val PREFS_NAME = "ashajyoti_auth_prefs"
    private const val KEY_TOKEN = "key_token"
    private const val KEY_DOCTOR_NAME = "key_doctor_name"
    private const val KEY_DOCTOR_ID = "key_doctor_id"
    private const val KEY_DOCTOR_SPECIALITY = "key_doctor_speciality"
    private const val KEY_USER_ROLE = "key_user_role"

    // Save auth token (e.g. "Bearer abc...")
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveDoctorName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DOCTOR_NAME, name).apply()
    }

    fun getDoctorName(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DOCTOR_NAME, null)
    }

    fun saveDoctorId(context: Context, id: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_DOCTOR_ID, id).apply()
    }

    fun getDoctorId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_DOCTOR_ID, -1)
    }

    fun saveDoctorSpeciality(context: Context, speciality: String?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (speciality != null) prefs.edit().putString(KEY_DOCTOR_SPECIALITY, speciality).apply()
    }

    fun getDoctorSpeciality(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DOCTOR_SPECIALITY, null)
    }

    // Save role (CHO / MO / CIVIL / EMERGENCY)
    fun saveRole(context: Context, role: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
    }

    fun getRole(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_ROLE, null)
    }

    // Clear all auth data (logout)
    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
