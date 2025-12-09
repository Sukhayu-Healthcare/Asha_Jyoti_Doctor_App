package com.example.ashajoyti_doctor_app.model

import com.google.gson.annotations.SerializedName

data class DoctorQueriesResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("doctor")
    val doctor: DoctorModel,
    @SerializedName("query")
    val query: List<QueryModel>
)
