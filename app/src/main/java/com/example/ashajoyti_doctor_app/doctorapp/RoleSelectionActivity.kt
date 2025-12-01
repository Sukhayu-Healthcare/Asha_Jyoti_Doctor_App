package com.example.ashajoyti_doctor_app.doctorapp

// If CHOLoginActivity etc are in same package, no import needed. If not, import explicitly:
import com.example.ashajoyti_doctor_app.doctorapp.CHOLoginActivity
import com.example.ashajoyti_doctor_app.doctorapp.MedicalOfficerActivity
import com.example.ashajoyti_doctor_app.doctorapp.CivilHospitalDoctorActivity
import com.example.ashajoyti_doctor_app.doctorapp.EmergencyDoctorActivity

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

        fun openLoginForRole(role: String) {
            val intent = Intent(this@RoleSelectionActivity, CHOLoginActivity::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }

        cardChief.setOnClickListener { openLoginForRole("CHO") }
        cardMedical.setOnClickListener { openLoginForRole("MO") }
        cardCivil.setOnClickListener { openLoginForRole("CIVIL") }
        cardEmergency.setOnClickListener { openLoginForRole("EMERGENCY") }

        btnBack.setOnClickListener { finish() }
    }
}
