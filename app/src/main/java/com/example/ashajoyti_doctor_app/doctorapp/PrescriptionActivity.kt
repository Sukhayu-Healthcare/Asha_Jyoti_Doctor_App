package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R

class PrescriptionActivity : AppCompatActivity() {

    private lateinit var tvPagePatientName: TextView
    private lateinit var etMedicineName: EditText
    private lateinit var etDosage: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnSavePrescription: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        tvPagePatientName = findViewById(R.id.tvPagePatientName)
        etMedicineName = findViewById(R.id.etMedicineName)
        etDosage = findViewById(R.id.etDosage)
        etNotes = findViewById(R.id.etNotes)
        btnSavePrescription = findViewById(R.id.btnSavePrescription)

        val pname = intent.getStringExtra("patient_name") ?: ""
        val pid = intent.getStringExtra("patient_id") ?: ""
        tvPagePatientName.text = if (pname.isNotBlank()) "$pname • $pid" else "Prescription"

        btnSavePrescription.setOnClickListener {
            val med = etMedicineName.text.toString().trim()
            val dose = etDosage.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            if (med.isBlank() && dose.isBlank() && notes.isBlank()) {
                Toast.makeText(this, "Enter at least one prescription field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Prescription saved (placeholder)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
