package com.example.ashajoyti_doctor_app.model

import com.google.gson.annotations.SerializedName

data class CreateConsultationRequest(
    @SerializedName("patient_id")
    val patient_id: Int,
    @SerializedName("diagnosis")
    val diagnosis: String,
    @SerializedName("notes")
    val notes: String,
    @SerializedName("items")
    val items: List<ConsultationItem>
)

data class ConsultationItem(
    @SerializedName("medicine_name")
    val medicine_name: String,
    @SerializedName("dosage")
    val dosage: String,
    @SerializedName("frequency")
    val frequency: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("instructions")
    val instructions: String = ""
)
