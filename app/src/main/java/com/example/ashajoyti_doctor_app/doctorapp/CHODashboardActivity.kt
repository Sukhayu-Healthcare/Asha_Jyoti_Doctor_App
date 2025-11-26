package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.ashajoyti_doctor_app.R

class CHODashboardActivity : AppCompatActivity() {

    private lateinit var cardQuick: MaterialCardView
    private lateinit var cardPatient: MaterialCardView
    private lateinit var cardRedirect: MaterialCardView
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var tvAvailableBadge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cho_dashboard) // ensure this layout exists

        cardQuick = findViewById(R.id.cardQuick)
        cardPatient = findViewById(R.id.cardPatientQueue)
        cardRedirect = findViewById(R.id.cardRedirect)
        switchAvailable = findViewById(R.id.switchAvailable)
        tvAvailableBadge = findViewById(R.id.tvAvailableBadge)

        cardQuick.setOnClickListener {
            Toast.makeText(this, "Quick Consultation clicked", Toast.LENGTH_SHORT).show()
        }

        cardPatient.setOnClickListener {
            Toast.makeText(this, "Patient Queue clicked", Toast.LENGTH_SHORT).show()
        }

        cardRedirect.setOnClickListener {
            Toast.makeText(this, "Redirection clicked", Toast.LENGTH_SHORT).show()
        }

        switchAvailable.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                tvAvailableBadge.text = "Available"
                tvAvailableBadge.setTextColor(ContextCompat.getColor(this, R.color.available_text))
            } else {
                tvAvailableBadge.text = "Unavailable"
                tvAvailableBadge.setTextColor(ContextCompat.getColor(this, R.color.secondary_text))
            }
        }
    }
}
