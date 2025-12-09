package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
    private lateinit var llEditMode: LinearLayout
    private lateinit var llViewMode: LinearLayout
    private lateinit var tvMedicineName: TextView
    private lateinit var tvDosage: TextView
    private lateinit var tvNotes: TextView
    
    private var isPastConsultation = false
    
    companion object {
        private const val TAG = "PrescriptionActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription)

        tvPagePatientName = findViewById(R.id.tvPagePatientName)
        etMedicineName = findViewById(R.id.etMedicineName)
        etDosage = findViewById(R.id.etDosage)
        etNotes = findViewById(R.id.etNotes)
        btnSavePrescription = findViewById(R.id.btnSavePrescription)
        llEditMode = findViewById(R.id.llEditMode)
        llViewMode = findViewById(R.id.llViewMode)
        tvMedicineName = findViewById(R.id.tvMedicineName)
        tvDosage = findViewById(R.id.tvDosage)
        tvNotes = findViewById(R.id.tvNotes)

        val pname = intent.getStringExtra("patient_name") ?: ""
        val pid = intent.getStringExtra("patient_id") ?: ""
        val consultationId = intent.getStringExtra("consultation_id")
        val medicineName = intent.getStringExtra("medicine_name") ?: ""
        val dosage = intent.getStringExtra("dosage") ?: ""
        val prescriptionNotes = intent.getStringExtra("prescription_notes") ?: ""
        
        Log.d(TAG, "onCreate - consultationId: $consultationId")
        Log.d(TAG, "onCreate - medicineName: $medicineName")
        Log.d(TAG, "onCreate - dosage: $dosage")
        Log.d(TAG, "onCreate - prescriptionNotes: $prescriptionNotes")
        
        tvPagePatientName.text = if (pname.isNotBlank()) "$pname • $pid" else "Prescription"

        // Check if this is a past consultation (viewing saved prescription)
        if (!consultationId.isNullOrEmpty()) {
            isPastConsultation = true
            Log.d(TAG, "Past consultation mode detected")
            
            // Show view mode (static text display)
            llViewMode.visibility = View.VISIBLE
            llEditMode.visibility = View.GONE
            
            // Display prescription data as static text
            val displayMedicine = if (medicineName.isNotEmpty() && medicineName != "null") medicineName else "N/A"
            val displayDosage = if (dosage.isNotEmpty() && dosage != "null") dosage else "N/A"
            val displayNotes = if (prescriptionNotes.isNotEmpty() && prescriptionNotes != "null") prescriptionNotes else "N/A"
            
            tvMedicineName.text = displayMedicine
            tvDosage.text = displayDosage
            tvNotes.text = displayNotes
            
            Log.d(TAG, "Display - Medicine: $displayMedicine, Dosage: $displayDosage, Notes: $displayNotes")
            
            // Update button text and behavior
            btnSavePrescription.text = "Close"
            btnSavePrescription.setOnClickListener {
                finish()
            }
        } else {
            // New consultation - show empty form for creating prescription
            Log.d(TAG, "New consultation mode detected")
            llEditMode.visibility = View.VISIBLE
            llViewMode.visibility = View.GONE
            
            btnSavePrescription.text = "Save Prescription"
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
}
