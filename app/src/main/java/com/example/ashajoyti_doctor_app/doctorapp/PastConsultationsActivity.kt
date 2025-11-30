package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R

class PastConsultationsActivity : AppCompatActivity() {
    private val TAG = "PastConsultationsAct"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_consultations)

        // toolbar safe lookup
        val tbId = resources.getIdentifier("toolbarPast", "id", packageName)
        if (tbId != 0) {
            val tb = findViewById<androidx.appcompat.widget.Toolbar>(tbId)
            setSupportActionBar(tb)
        }

        val rv = findViewById<RecyclerView>(R.id.rvPast)
        rv.layoutManager = LinearLayoutManager(this)

        // sample data matching PatientDetailsActivity.createIntent
        val sample = listOf(
            PastConsultation(
                name = "Rohit Sharma",
                patientId = "P001",
                age = "34",
                gender = "male",
                symptoms = "Fever, weakness",
                visit = "2025-02-12 10:15",
                wait = "10m",
                vitals = "BP:120/80 HR:78",
                feedbackRating = 4.5,
                feedbackText = "Good consult, clear instructions."
            ),
            PastConsultation(
                name = "Neha Patil",
                patientId = "P002",
                age = "29",
                gender = "female",
                symptoms = "Follow-up checkup",
                visit = "2025-02-11 11:00",
                wait = "5m",
                vitals = "BP:118/76 HR:76",
                feedbackRating = 5.0,
                feedbackText = "Very helpful."
            )
        )

        rv.adapter = PastConsultationsAdapter(sample) { item ->
            Log.i(TAG, "View details clicked for ${item.patientId}")
            val intent = PatientDetailsActivity.createIntent(
                context = this,
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
}
