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

        // setup list
        rvPatientQueue.layoutManager = LinearLayoutManager(this)
        rvPatientQueue.adapter = PatientQueueAdapter(makeSamplePatients(), this)

        // REMOVE THIS → no back arrow needed
        // val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarPatientQueue)
        // toolbar?.setNavigationOnClickListener { finish() }
    }

    private fun makeSamplePatients(): List<Patient> {
        return listOf(
            Patient(
                1, "मोहन गुप्ता", "YELLOW",
                "Patient ID: P003",
                "Age: 55, Gender: male",
                "Symptoms: cold, cough, sore throat",
                "15 minutes"
            ),
            Patient(
                2, "प्रिया शर्मा", "YELLOW",
                "Patient ID: P004",
                "Age: 28, Gender: female",
                "Symptoms: headache, fever, fatigue",
                "30 minutes"
            ),
            Patient(
                3, "सीता देवी", "YELLOW",
                "Patient ID: P002",
                "Age: 32, Gender: female",
                "Symptoms: mild fever, runny nose",
                "45 minutes"
            ),
            Patient(
                4, "अनिल वर्मा", "YELLOW",
                "Patient ID: P007",
                "Age: 40, Gender: male",
                "Symptoms: minor headache, mild fatigue",
                "60 minutes"
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

    // inside your Activity implementing PatientQueueAdapter.OnPatientActionListener
override fun onViewDetails(patient: Patient) {
    val intent = PatientDetailsActivity.createIntent(
        context = this,
        name = patient.name,
        patientId = patient.patientId,
        age = patient.ageGender.split(',').getOrNull(0)?.trim() ?: "—", // quick parse if needed
        gender = patient.ageGender.split(',').getOrNull(1)?.trim() ?: "—",
        symptoms = patient.symptoms,
        visit = "2025-11-30 14:00",
        wait = patient.estWait,
        vitals = "BP: —  HR: —", // pass real vitals if you have them
        feedbackRating = 4.0,
        feedbackText = "Sample feedback"
    )
    startActivity(intent)
}

}
