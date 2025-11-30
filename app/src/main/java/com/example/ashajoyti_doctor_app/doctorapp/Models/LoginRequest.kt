package com.example.ashajoyti_doctor_app.model

data class LoginRequest(
    val doc_id: String? = null,
    val doc_phone: String? = null,
    val password: String
)
