package com.example.ashajoyti_doctor_app.model

import com.google.gson.annotations.SerializedName

data class ConsultationCreateResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("consultation_id")
    val consultation_id: Int,
    @SerializedName("consultation_date")
    val consultation_date: String
)
