package com.example.ashajoyti_doctor_app.model

data class PatientResponse(
    val patient_id: Int,
    val patient_name: String?,
    val gender: String?,
    val dob: String?,
    val phone: Long?,

    val supreme_id: Int?,
    //val profile_pic: String?,
    val village: String?,
    val taluka: String?,
    val district: String?,
    val history: String?,
    val created_at: String?,

    val registered_asha_id: Int?,
    val user_id: Int?
)
