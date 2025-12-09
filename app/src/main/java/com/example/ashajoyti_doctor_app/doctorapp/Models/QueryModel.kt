package com.example.ashajoyti_doctor_app.model

import com.google.gson.annotations.SerializedName

data class QueryModel(
    @SerializedName("query_id")
    val query_id: Int,
    @SerializedName("patient_id")
    val patient_id: Int,
    @SerializedName("asha_id")
    val asha_id: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("voice_url")
    val voice_url: String?,
    @SerializedName("disease")
    val disease: String?,
    @SerializedName("doc")
    val doc: String?,
    @SerializedName("doc_id")
    val doc_id: Int,
    @SerializedName("query_status")
    val query_status: String,
    @SerializedName("done_or_not")
    val done_or_not: Boolean,
    // Patient details
    @SerializedName("patient_name")
    val patient_name: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("patient_phone")
    val patient_phone: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("patient_dob")
    val patient_dob: String? = null,
    @SerializedName("dob")
    val dob: String? = null,
    @SerializedName("patient_gender")
    val patient_gender: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("zone")
    val zone: String? = null
)
