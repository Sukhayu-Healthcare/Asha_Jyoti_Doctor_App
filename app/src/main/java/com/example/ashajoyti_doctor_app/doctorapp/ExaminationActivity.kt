package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R

class ExaminationActivity : AppCompatActivity() {

    private lateinit var tvPagePatientName: TextView
    private lateinit var etDiagnosis: EditText
    private lateinit var etClinicalNotes: EditText
    private lateinit var btnSaveExam: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examination)

        tvPagePatientName = findViewById(R.id.tvPagePatientName)
        etDiagnosis = findViewById(R.id.etDiagnosis)
        etClinicalNotes = findViewById(R.id.etClinicalNotes)
        btnSaveExam = findViewById(R.id.btnSaveExam)

        val pname = intent.getStringExtra("patient_name") ?: ""
        val pid = intent.getStringExtra("patient_id") ?: ""
        tvPagePatientName.text = if (pname.isNotBlank()) "$pname • $pid" else "Examination"

        btnSaveExam.setOnClickListener {
            val diag = etDiagnosis.text.toString().trim()
            val notes = etClinicalNotes.text.toString().trim()
            if (diag.isBlank() && notes.isBlank()) {
                Toast.makeText(this, "Enter diagnosis or clinical notes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Examination saved (placeholder)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
