package com.example.ashajoyti_doctor_app.network

import com.example.ashajoyti_doctor_app.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("doctor/login")
    fun loginDoctor(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("doctor/consultations")
    fun getConsultations(
        @Header("Authorization") token: String
    ): Call<ConsultationListResponse>

    @POST("doctor/consultation-with-items")
    fun createConsultation(
        @Header("Authorization") token: String,
        @Body request: CreateConsultationRequest
    ): Call<ConsultationCreateResponse>
}
