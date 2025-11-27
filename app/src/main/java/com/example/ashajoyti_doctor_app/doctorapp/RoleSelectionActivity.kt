package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.example.ashajoyti_doctor_app.R

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val cardChief = findViewById<MaterialCardView>(R.id.card_chief)
        val cardMedical = findViewById<MaterialCardView>(R.id.card_medical)
        val cardCivil = findViewById<MaterialCardView>(R.id.card_civil)
        val cardEmergency = findViewById<MaterialCardView>(R.id.card_emergency)
        val btnBack = findViewById<Button>(R.id.btn_back)

        cardChief.setOnClickListener {
            startActivity(Intent(this, CHOLoginActivity::class.java))
        }

        cardMedical.setOnClickListener {
            startActivity(Intent(this, MedicalOfficerActivity::class.java))
        }

        cardCivil.setOnClickListener {
            startActivity(Intent(this, CivilHospitalDoctorActivity::class.java))
        }

        cardEmergency.setOnClickListener {
            startActivity(Intent(this, EmergencyDoctorActivity::class.java))
        }

        btnBack.setOnClickListener { finish() }
    }
}
