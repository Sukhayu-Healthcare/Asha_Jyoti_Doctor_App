package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.button.MaterialButton

class QueryDetailActivity : AppCompatActivity() {

    private var queryId: Int = 0
    private var patientId: Int = 0
    private var text: String = ""
    private var voiceUrl: String? = null
    private var disease: String? = null
    private var doc: String? = null
    private var queryStatus: String = ""
    private var doneOrNot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_detail)

        // Retrieve data from intent
        queryId = intent.getIntExtra("query_id", 0)
        patientId = intent.getIntExtra("patient_id", 0)
        text = intent.getStringExtra("text") ?: ""
        voiceUrl = intent.getStringExtra("voice_url")
        disease = intent.getStringExtra("disease")
        doc = intent.getStringExtra("doc")
        queryStatus = intent.getStringExtra("query_status") ?: ""
        doneOrNot = intent.getBooleanExtra("done_or_not", false)

        // Initialize views
        val tvPatientName: TextView = findViewById(R.id.tvPatientName)
        val tvPatientPhone: TextView = findViewById(R.id.tvPatientPhone)
        val tvPatientDob: TextView = findViewById(R.id.tvPatientDob)
        val tvPatientGender: TextView = findViewById(R.id.tvPatientGender)
        val tvPatientZone: TextView = findViewById(R.id.tvPatientZone)
        val tvQueryText: TextView = findViewById(R.id.tvQueryText)
        val tvDisease: TextView = findViewById(R.id.tvDisease)
        val tvQueryStatus: TextView = findViewById(R.id.tvQueryStatus)
        val btnPlayVoice: ImageButton = findViewById(R.id.btnPlayVoiceDetail)
        val btnConsult: MaterialButton = findViewById(R.id.btnConsult)
        val btnBack: Button = findViewById(R.id.btnBackDetail)

        // Set static patient details (hardcoded for display)
        tvPatientName.text = "Shlok Dubey"
        tvPatientPhone.text = "9699202706"
        tvPatientDob.text = "12/12/2005"
        tvPatientGender.text = "Male"
        tvPatientZone.text = "Yellow viral fever"

        // Set query details
        tvQueryText.text = text
        tvDisease.text = disease ?: "Not specified"
        tvQueryStatus.text = queryStatus

        // Show/hide voice button
        if (!voiceUrl.isNullOrEmpty()) {
            btnPlayVoice.visibility = android.view.View.VISIBLE
            btnPlayVoice.setOnClickListener {
                Toast.makeText(this, "Playing voice: $voiceUrl", Toast.LENGTH_SHORT).show()
                // TODO: Implement audio player
            }
        } else {
            btnPlayVoice.visibility = android.view.View.GONE
        }

        // Dynamic Prescription button
        btnConsult.setOnClickListener {
            // Navigate to QuickPrescriptionActivity (prescription_consult page) with patient data
            val intent = Intent(this, QuickPrescriptionActivity::class.java)
            intent.putExtra("query_id", queryId)
            intent.putExtra("patient_id", patientId.toString())
            intent.putExtra("patient_name", "Shlok Dubey")
            intent.putExtra("patient_phone", "9699202706")
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }
    }
}
