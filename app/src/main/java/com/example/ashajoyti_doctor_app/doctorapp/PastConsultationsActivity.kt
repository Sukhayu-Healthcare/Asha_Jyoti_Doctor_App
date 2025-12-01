package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.ConsultationListResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.AuthPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PastConsultationsActivity : AppCompatActivity() {

    private val TAG = "PastConsultationsAct"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_consultations)

        // Toolbar (if present in layout)
        val tbId = resources.getIdentifier("toolbarPast", "id", packageName)
        if (tbId != 0) {
            val tb = findViewById<androidx.appcompat.widget.Toolbar>(tbId)
            setSupportActionBar(tb)
        }

        val rv = findViewById<RecyclerView>(R.id.rvPast)
        rv.layoutManager = LinearLayoutManager(this)

        val token = AuthPref.getToken(this)
        if (token == null) {
            Log.w(TAG, "No auth token found — user likely not logged in")
            Toast.makeText(this, "Login expired", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.api.getConsultations(token)
            .enqueue(object : Callback<ConsultationListResponse> {

                override fun onResponse(
                    call: Call<ConsultationListResponse>,
                    response: Response<ConsultationListResponse>
                ) {
                    if (!response.isSuccessful || response.body() == null) {
                        Toast.makeText(
                            this@PastConsultationsActivity,
                            "Failed to load consultations",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    val list = response.body()!!.consultations

                    rv.adapter = PastConsultationsAdapter(list.map {
                        PastConsultation(
                            name = "Patient ${it.patient_id}",
                            patientId = it.patient_id.toString(),
                            age = "N/A",
                            gender = "N/A",
                            symptoms = it.diagnosis ?: "N/A",
                            visit = it.consultation_date,
                            wait = "",
                            vitals = "",
                            feedbackRating = 0.0,
                            feedbackText = ""
                        )
                    }) { item ->
                        val intent = PatientDetailsActivity.createIntent(
                            context = this@PastConsultationsActivity,
                            name = item.name,
                            patientId = item.patientId,
                            age = item.age,
                            gender = item.gender,
                            symptoms = item.symptoms,
                            visit = item.visit,
                            wait = item.wait,
                            vitals = item.vitals,
                            feedbackRating = item.feedbackRating,
                            feedbackText = item.feedbackText
                        )
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<ConsultationListResponse>, t: Throwable) {
                    Toast.makeText(
                        this@PastConsultationsActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
