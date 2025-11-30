package com.example.ashajoyti_doctor_app.model

data class CreateConsultationRequest(
    val patient_id: Int,
    val diagnosis: String? = null,
    val notes: String? = null,
    val items: List<PrescriptionItem>? = null
)

data class PrescriptionItem(
    val medicine_name: String,
    val dosage: String?,
    val frequency: String?,
    val duration: String?,
    val instructions: String?
)
