package com.example.ashajoyti_doctor_app.model

data class LoginResponse(
    val message: String,
    val token: String,
    val doctor: DoctorModel
)

data class DoctorModel(
    val doc_id: Int,
    val doc_name: String,
    val doc_phone: Long,
    val doc_speciality: String?,
    val doc_status: String,
    val hospital_address: String,
    val hospital_village: String,
    val hospital_taluka: String,
    val hospital_district: String,
    val hospital_state: String
)
