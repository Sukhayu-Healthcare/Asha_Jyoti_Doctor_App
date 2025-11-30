package com.example.ashajoyti_doctor_app.model

data class ConsultationListResponse(
    val total: Int,
    val consultations: List<ConsultationModel>
)

data class ConsultationModel(
    val consultation_id: Int,
    val patient_id: Int,
    val doc_id: Int,
    val diagnosis: String?,
    val notes: String?,
    val consultation_date: String
)
