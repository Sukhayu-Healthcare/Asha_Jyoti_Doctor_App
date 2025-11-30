package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.ashajoyti_doctor_app.R

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)

        // direct toolbar find (ensure layout contains toolbarDetails)
        val tb = findViewById<Toolbar?>(R.id.toolbarDetails)
        if (tb != null) {
            setSupportActionBar(tb)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            tb.setNavigationOnClickListener { finish() }
        } else {
            Log.w(TAG, "toolbarDetails not found in layout")
        }

        // --- read extras safely ---
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

        // --- bind views (these IDs must exist in activity_patient_details.xml) ---
        val tvName = findViewById<TextView?>(R.id.tvName)
        val tvAgeGender = findViewById<TextView?>(R.id.tvAgeGender)
        val tvPatientId = findViewById<TextView?>(R.id.tvPatientId)
        val tvSymptoms = findViewById<TextView?>(R.id.tvSymptoms)
        val tvVisitDate = findViewById<TextView?>(R.id.tvVisitDate)
        val tvEstWait = findViewById<TextView?>(R.id.tvEstWait)
        val tvVitals = findViewById<TextView?>(R.id.tvVitals)
        val tvFeedbackText = findViewById<TextView?>(R.id.tvFeedbackText)
        val tvFeedbackRating = findViewById<TextView?>(R.id.tvFeedbackRating)

        // fill
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
    }
}
