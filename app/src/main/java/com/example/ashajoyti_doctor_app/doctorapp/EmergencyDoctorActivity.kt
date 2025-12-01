package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.example.ashajoyti_doctor_app.R

class EmergencyDoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_role)

        findViewById<TextView>(R.id.role_name).text = "Emergency Doctor"
        findViewById<TextView>(R.id.role_desc).text = "Emergency medical care"
    }
}
