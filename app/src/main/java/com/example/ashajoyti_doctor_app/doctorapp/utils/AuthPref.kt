package com.example.ashajoyti_doctor_app.utils

import android.content.Context

object AuthPref {
    private const val PREFS_NAME = "ashajyoti_auth_prefs"
    private const val KEY_TOKEN = "key_token"
    private const val KEY_DOCTOR_NAME = "key_doctor_name"
    private const val KEY_DOCTOR_ID = "key_doctor_id"
    private const val KEY_DOCTOR_SPECIALITY = "key_doctor_speciality"
    private const val KEY_USER_ROLE = "key_user_role"

    // additional keys (optional)
    private const val KEY_DOCTOR_PHONE = "key_doctor_phone"
    private const val KEY_PROFILE_PIC = "key_profile_pic"
    private const val KEY_DOC_STATUS = "key_doc_status"

    // Save/get token (store raw or "Bearer <token>")
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    fun getToken(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_TOKEN, null)

    // Basic doctor info
    fun saveDoctorName(context: Context, name: String?) {
        name?.let { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_DOCTOR_NAME, it).apply() }
    }
    fun getDoctorName(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_DOCTOR_NAME, null)

    fun saveDoctorId(context: Context, id: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putInt(KEY_DOCTOR_ID, id).apply()
    }
    fun getDoctorId(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(KEY_DOCTOR_ID, -1)

    fun saveDoctorSpeciality(context: Context, speciality: String?) {
        speciality?.let { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_DOCTOR_SPECIALITY, it).apply() }
    }
    fun getDoctorSpeciality(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_DOCTOR_SPECIALITY, null)

    fun saveRole(context: Context, role: String?) {
        role?.let { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_USER_ROLE, it).apply() }
    }
    fun getRole(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_USER_ROLE, null)

    // a few optional fields used by profile screens
    fun saveDoctorPhone(context: Context, phone: String?) { phone?.let { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_DOCTOR_PHONE, it).apply() } }
    fun getDoctorPhone(context: Context): String? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_DOCTOR_PHONE, null)

    fun saveProfilePic(context: Context, urlOrUri: String?) { urlOrUri?.let { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_PROFILE_PIC, it).apply() } }
    fun getProfilePic(context: Context): String? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_PROFILE_PIC, null)

    fun saveDocStatus(context: Context, status: String?) { status?.let { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_DOC_STATUS, it).apply() } }
    fun getDocStatus(context: Context): String? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_DOC_STATUS, null)

    // Clear all auth data
    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
