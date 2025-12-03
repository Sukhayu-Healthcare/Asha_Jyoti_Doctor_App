package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R

class PatientQueueActivity : AppCompatActivity(), PatientQueueAdapter.OnPatientActionListener {

    private val TAG = "PatientQueueActivity"

    private lateinit var rvPatientQueue: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_queue)

        // find recycler view
        rvPatientQueue = findViewById(R.id.rvPatientQueue)

        // setup list (dummy data)
        rvPatientQueue.layoutManager = LinearLayoutManager(this)
        rvPatientQueue.adapter = PatientQueueAdapter(makeSamplePatients(), this)
    }

    private fun makeSamplePatients(): List<Patient> {
        // Dummy data in English as requested
        return listOf(
            Patient(
                index = 1,
                name = "Mohan Gupta",
                severity = "", // hidden by default
                patientId = "Patient ID: P003",
                ageGender = "Age: 55, Gender: male",
                symptoms = "Symptoms: cold, cough, sore throat",
                estWait = "15 minutes"
            ),
            Patient(
                index = 2,
                name = "Priya Sharma",
                severity = "",
                patientId = "Patient ID: P004",
                ageGender = "Age: 28, Gender: female",
                symptoms = "Symptoms: headache, fever, fatigue",
                estWait = "30 minutes"
            ),
            Patient(
                index = 3,
                name = "Sita Devi",
                severity = "",
                patientId = "Patient ID: P002",
                ageGender = "Age: 32, Gender: female",
                symptoms = "Symptoms: mild fever, runny nose",
                estWait = "45 minutes"
            ),
            Patient(
                index = 4,
                name = "Anil Verma",
                severity = "",
                patientId = "Patient ID: P007",
                ageGender = "Age: 40, Gender: male",
                symptoms = "Symptoms: minor headache, mild fatigue",
                estWait = "60 minutes"
            )
        )
    }

    override fun onStartConsultation(patient: Patient) {
        Toast.makeText(this, "Start consultation: ${patient.name}", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "Start consultation for ${patient.patientId}")
    }

    override fun onTagEmergency(patient: Patient) {
        Toast.makeText(this, "Emergency: ${patient.name}", Toast.LENGTH_SHORT).show()
        Log.i(TAG, "Emergency tagged for ${patient.patientId}")
    }

    override fun onViewDetails(patient: Patient) {
        val intent = PatientDetailsActivity.createIntent(
            context = this,
            name = patient.name,
            patientId = patient.patientId,
            age = patient.ageGender.split(',').getOrNull(0)?.trim() ?: "—",
            gender = patient.ageGender.split(',').getOrNull(1)?.trim() ?: "—",
            symptoms = patient.symptoms,
            visit = "2025-11-30 14:00",
            wait = patient.estWait,
            vitals = "BP: —  HR: —",
            feedbackRating = 4.0,
            feedbackText = "Sample feedback"
        )
        startActivity(intent)
    }
}
