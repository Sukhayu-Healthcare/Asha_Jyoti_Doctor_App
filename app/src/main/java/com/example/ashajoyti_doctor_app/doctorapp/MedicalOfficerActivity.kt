package com.example.ashajoyti_doctor_app.doctor_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MedicalOfficerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_role)

        findViewById<TextView>(R.id.role_name).text = "Medical Officer"
        findViewById<TextView>(R.id.role_desc).text = "Advanced medical consultations"
    }
}
