package com.example.ashajoyti_doctor_app.model

import com.google.gson.annotations.SerializedName

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
    val consultation_date: String,
    
    // Prescription fields (if saved)
    val medicine_name: String? = null,
    val dosage: String? = null,
    val prescription_notes: String? = null,
    
    // Patient data fields (may be null if not included in API response)
    @SerializedName("patient_name")
    val patient_name: String? = null,
    
    @SerializedName("name")
    val name: String? = null,  // Alternative field name
    
    @SerializedName("patient_phone")
    val patient_phone: Long? = null,
    
    @SerializedName("phone")
    val phone: Long? = null,  // Alternative field name
    
    @SerializedName("patient_dob")
    val patient_dob: String? = null,
    
    @SerializedName("age")
    val age: String? = null,  // Alternative field name
    
    @SerializedName("patient_gender")
    val patient_gender: String? = null,
    
    @SerializedName("gender")
    val gender: String? = null,  // Alternative field name
    
    // Additional nested patient object (if API returns it)
    val patient: PatientDataInConsultation? = null
)

// In case patient is nested inside consultation
data class PatientDataInConsultation(
    val patient_id: Int? = null,
    val patient_name: String? = null,
    val name: String? = null,
    val phone: Long? = null,
    val dob: String? = null,
    val gender: String? = null
)
