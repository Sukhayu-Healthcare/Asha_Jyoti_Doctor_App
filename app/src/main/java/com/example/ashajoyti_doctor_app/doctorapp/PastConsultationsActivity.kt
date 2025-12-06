package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
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
import com.example.ashajoyti_doctor_app.utils.TokenManager
import okhttp3.ResponseBody
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

        // attach an empty adapter early so RecyclerView won't warn
        rv.adapter = PastConsultationsAdapter(emptyList()) { item ->
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

        // Use TokenManager to ensure "Bearer " prefix
        val tokenHeader = TokenManager.getAuthHeader(this)
        if (tokenHeader.isNullOrBlank()) {
            // fallback: if saved token exists but no Bearer, show warning
            val raw = AuthPref.getToken(this)
            Log.w(TAG, "No auth header (Bearer) found. rawToken=${raw != null}")
            Toast.makeText(this, "Login expired or token missing. Please login.", Toast.LENGTH_SHORT).show()
            return
        }

        // Debug log
        Log.d(TAG, "Calling getConsultations with tokenHeader=${tokenHeader.takeIf { it.length < 40 } ?: tokenHeader.replaceRange(10, tokenHeader.length, "…")}")

        ApiClient.api.getConsultations(tokenHeader).enqueue(object : Callback<ConsultationListResponse> {
            override fun onResponse(call: Call<ConsultationListResponse>, response: Response<ConsultationListResponse>) {
                if (!response.isSuccessful) {
                    // log status + body for debugging
                    val code = response.code()
                    val err: String = try {
                        val eb: ResponseBody? = response.errorBody()
                        eb?.string() ?: "no error body"
                    } catch (ex: Exception) {
                        "errorBodyReadFailed: ${ex.message}"
                    }
                    Log.e(TAG, "getConsultations failed: code=$code err=$err")
                    if (code == 401 || code == 403) {
                        // unauthorized -> token issue
                        Toast.makeText(this@PastConsultationsActivity, "Unauthorized. Please login again.", Toast.LENGTH_LONG).show()
                        // Optionally clear token and redirect to login:
                        // TokenManager.clear(this@PastConsultationsActivity); startActivity(Intent(this@PastConsultationsActivity, CHOLoginActivity::class.java))
                        return
                    }

                    Toast.makeText(this@PastConsultationsActivity, "Failed to load consultations (code $code). See logs.", Toast.LENGTH_SHORT).show()
                    return
                }

                val body = response.body()
                if (body == null) {
                    Log.e(TAG, "getConsultations succeeded but returned empty body")
                    Toast.makeText(this@PastConsultationsActivity, "No consultations found", Toast.LENGTH_SHORT).show()
                    return
                }

                // Build display list and set adapter
                val list = body.consultations
                val display = list.map {
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
                }

                // update adapter on UI thread (we're already on main thread)
                rv.adapter = PastConsultationsAdapter(display) { item ->
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
                Log.e(TAG, "getConsultations onFailure", t)
                Toast.makeText(this@PastConsultationsActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
