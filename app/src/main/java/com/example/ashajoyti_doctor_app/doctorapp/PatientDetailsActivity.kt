package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.CreateConsultationRequest
// import com.example.ashajoyti_doctor_app.model.PrescriptionItem   // commented to avoid unresolved reference
import com.example.ashajoyti_doctor_app.model.ConsultationCreateResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.AuthPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDetailsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PatientDetailsActivity"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_PATIENT_ID = "extra_patient_id"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_SYMPTOMS = "extra_symptoms"
        const val EXTRA_VISIT = "extra_visit"
        const val EXTRA_WAIT = "extra_wait"
        const val EXTRA_VITALS = "extra_vitals"
        const val EXTRA_FEEDBACK_TEXT = "extra_feedback_text"
        const val EXTRA_FEEDBACK_RATING = "extra_feedback_rating"

        fun createIntent(
            context: Context,
            name: String,
            patientId: String,
            age: String,
            gender: String,
            symptoms: String,
            visit: String,
            wait: String,
            vitals: String,
            feedbackRating: Double,
            feedbackText: String
        ): Intent {
            return Intent(context, PatientDetailsActivity::class.java).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_PATIENT_ID, patientId)
                putExtra(EXTRA_AGE, age)
                putExtra(EXTRA_GENDER, gender)
                putExtra(EXTRA_SYMPTOMS, symptoms)
                putExtra(EXTRA_VISIT, visit)
                putExtra(EXTRA_WAIT, wait)
                putExtra(EXTRA_VITALS, vitals)
                putExtra(EXTRA_FEEDBACK_RATING, feedbackRating)
                putExtra(EXTRA_FEEDBACK_TEXT, feedbackText)
            }
        }
    }

    // previously there was a prescription list here; commented out to avoid type errors:
    // private val prescriptionListForSubmission: MutableList<PrescriptionItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)

        val tb = findViewById<Toolbar?>(R.id.toolbarDetails)
        if (tb != null) {
            setSupportActionBar(tb)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            tb.setNavigationOnClickListener { finish() }
        } else {
            Log.w(TAG, "toolbarDetails not found in layout")
        }

        val name = intent.getStringExtra(EXTRA_NAME) ?: "Unknown"
        val patientId = intent.getStringExtra(EXTRA_PATIENT_ID) ?: "—"
        val age = intent.getStringExtra(EXTRA_AGE) ?: "—"
        val gender = intent.getStringExtra(EXTRA_GENDER) ?: "—"
        val symptoms = intent.getStringExtra(EXTRA_SYMPTOMS) ?: "—"
        val visit = intent.getStringExtra(EXTRA_VISIT) ?: "—"
        val wait = intent.getStringExtra(EXTRA_WAIT) ?: "—"
        val vitals = intent.getStringExtra(EXTRA_VITALS) ?: "—"
        val feedbackText = intent.getStringExtra(EXTRA_FEEDBACK_TEXT) ?: "No feedback"
        val feedbackRating = intent.getDoubleExtra(EXTRA_FEEDBACK_RATING, -1.0)

        val tvName = findViewById<TextView?>(R.id.tvName)
        val tvAgeGender = findViewById<TextView?>(R.id.tvAgeGender)
        val tvPatientId = findViewById<TextView?>(R.id.tvPatientId)
        val tvSymptoms = findViewById<TextView?>(R.id.tvSymptoms)
        val tvVisitDate = findViewById<TextView?>(R.id.tvVisitDate)
        val tvEstWait = findViewById<TextView?>(R.id.tvEstWait)
        val tvVitals = findViewById<TextView?>(R.id.tvVitals)
        val tvFeedbackText = findViewById<TextView?>(R.id.tvFeedbackText)
        val tvFeedbackRating = findViewById<TextView?>(R.id.tvFeedbackRating)

        val etDiagnosis = findViewById<EditText?>(R.id.etDiagnosis)
        val etNotes = findViewById<EditText?>(R.id.etNotes)
        val btnSubmit = findViewById<Button?>(R.id.btnSubmitConsultation)

        tvName?.text = name
        tvAgeGender?.text = "$age · $gender"
        tvPatientId?.text = "ID: $patientId"
        tvSymptoms?.text = "Symptoms: $symptoms"
        tvVisitDate?.text = "Visit: $visit"
        tvEstWait?.text = "Wait: $wait"
        tvVitals?.text = vitals
        tvFeedbackText?.text = feedbackText
        tvFeedbackRating?.text = if (feedbackRating >= 0.0) {
            String.format("%.1f / 5", feedbackRating)
        } else {
            "No rating"
        }

        val patientIdInt = extractDigitsAsInt(patientId)
        if (patientIdInt == null) {
            Log.w(TAG, "Unable to parse patient id to integer: '$patientId'")
            btnSubmit?.isEnabled = false
        }

        btnSubmit?.setOnClickListener {
            val token = AuthPref.getToken(this)
            if (token == null) {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pid = patientIdInt
            if (pid == null) {
                Toast.makeText(this, "Invalid patient ID. Cannot submit.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val diagnosisText = etDiagnosis?.text?.toString()?.trim()
            val notesText = etNotes?.text?.toString()?.trim()

            // Build request with non-null strings for diagnosis/notes and NO items to avoid type mismatch.
            val request = CreateConsultationRequest(
                patient_id = pid,
                diagnosis = diagnosisText ?: "",
                notes = notesText ?: "",
                items = emptyList()
            )

            btnSubmit.isEnabled = false
            Toast.makeText(this, "Submitting consultation...", Toast.LENGTH_SHORT).show()

            ApiClient.api.createConsultation(token, request)
                .enqueue(object : Callback<ConsultationCreateResponse> {
                    override fun onResponse(
                        call: Call<ConsultationCreateResponse>,
                        response: Response<ConsultationCreateResponse>
                    ) {
                        btnSubmit.isEnabled = true
                        if (response.isSuccessful && response.body() != null) {
                            Toast.makeText(
                                this@PatientDetailsActivity,
                                "Consultation submitted",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            val msg = try {
                                response.errorBody()?.string() ?: "Server error"
                            } catch (e: Exception) {
                                "Server error"
                            }
                            Toast.makeText(
                                this@PatientDetailsActivity,
                                "Submit failed: $msg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ConsultationCreateResponse>, t: Throwable) {
                        btnSubmit.isEnabled = true
                        Toast.makeText(
                            this@PatientDetailsActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun extractDigitsAsInt(id: String): Int? {
        val match = Regex("\\d+").find(id)
        return match?.value?.toIntOrNull()
    }
}
