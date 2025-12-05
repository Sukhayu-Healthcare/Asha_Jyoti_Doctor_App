package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R

class VitalsActivity : AppCompatActivity() {

    private lateinit var tvPagePatientName: TextView
    private lateinit var etBloodPressure: EditText
    private lateinit var etHeartRate: EditText
    private lateinit var etTemperature: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var etOxygen: EditText
    private lateinit var btnSaveVitals: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vitals)

        tvPagePatientName = findViewById(R.id.tvPagePatientName)
        etBloodPressure = findViewById(R.id.etBloodPressure)
        etHeartRate = findViewById(R.id.etHeartRate)
        etTemperature = findViewById(R.id.etTemperature)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)
        etOxygen = findViewById(R.id.etOxygen)
        btnSaveVitals = findViewById(R.id.btnSaveVitals)

        val pname = intent.getStringExtra("patient_name") ?: ""
        val pid = intent.getStringExtra("patient_id") ?: ""
        tvPagePatientName.text = if (pname.isNotBlank()) "$pname • $pid" else "Vitals"

        btnSaveVitals.setOnClickListener {
            // collect values (you'll replace with persistent save later)
            val bp = etBloodPressure.text.toString().trim()
            val hr = etHeartRate.text.toString().trim()
            val temp = etTemperature.text.toString().trim()
            val weight = etWeight.text.toString().trim()
            val height = etHeight.text.toString().trim()
            val oxy = etOxygen.text.toString().trim()

            // minimal validation
            if (bp.isBlank() && hr.isBlank() && temp.isBlank() && weight.isBlank() && height.isBlank() && oxy.isBlank()) {
                Toast.makeText(this, "Please enter at least one vitals field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Vitals saved (placeholder)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
