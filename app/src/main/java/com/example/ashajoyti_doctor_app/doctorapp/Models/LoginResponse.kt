package com.example.ashajoyti_doctor_app.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("doctor")
    val doctor: DoctorModel
)

data class DoctorModel(
    @SerializedName("doc_id")
    val doc_id: Int,
    @SerializedName("doc_name")
    val doc_name: String,
    @SerializedName("doc_role")
    val doc_role: String,
    @SerializedName("doc_phone")
    val doc_phone: Long,
    @SerializedName("doc_speciality")
    val doc_speciality: String?,
    @SerializedName("doc_status")
    val doc_status: String,
    @SerializedName("hospital_address")
    val hospital_address: String,
    @SerializedName("hospital_village")
    val hospital_village: String,
    @SerializedName("hospital_taluka")
    val hospital_taluka: String,
    @SerializedName("hospital_district")
    val hospital_district: String,
    @SerializedName("hospital_state")
    val hospital_state: String
)
