package com.example.ashajoyti_doctor_app.doctor_app


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class CivilHospitalDoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_role)

        findViewById<TextView>(R.id.role_name).text = "Civil Hospital Doctor"
        findViewById<TextView>(R.id.role_desc).text = "Hospital-based consultations"
    }
}
