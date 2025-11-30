package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.ashajoyti_doctor_app.R

class CHODashboardActivity : AppCompatActivity() {

    private val TAG = "CHODashboardActivity"

    private lateinit var cardQuick: MaterialCardView
    private lateinit var cardPatient: MaterialCardView
    private lateinit var cardRedirect: MaterialCardView
    private lateinit var cardPast: MaterialCardView                // <-- ADDED
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var tvAvailableBadge: TextView

    // header/profile/recent views
    private lateinit var appTitle: TextView
    private lateinit var appLogoSmall: ImageView
    private lateinit var headerRoleShort: TextView

    private lateinit var tvName: TextView
    private lateinit var tvDesignation: TextView
    private lateinit var tvId: TextView

    private lateinit var icRecentCalendar: ImageView
    private lateinit var tvRecentSubtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard) // matches file name

        // find views
        cardQuick = findViewById(R.id.cardQuick)
        cardPatient = findViewById(R.id.cardPatientQueue)
        cardRedirect = findViewById(R.id.cardRedirect)
        cardPast = findViewById(R.id.cardPastConsultations)       // <-- INITIALIZE
        switchAvailable = findViewById(R.id.switchAvailable)
        tvAvailableBadge = findViewById(R.id.tvAvailableBadge)

        appTitle = findViewById(R.id.appTitle)
        appLogoSmall = findViewById(R.id.appLogoSmall)
        headerRoleShort = findViewById(R.id.headerRoleShort)

        tvName = findViewById(R.id.tvName)
        tvDesignation = findViewById(R.id.tvDesignation)
        tvId = findViewById(R.id.tvId)

        icRecentCalendar = findViewById(R.id.ic_recent_calendar)
        tvRecentSubtitle = findViewById(R.id.tvRecentSubtitle)

        // header text / role
        appTitle.text = "ASHA JYOTI Doctor"
        val roleText = "Chief Health Officer"
        headerRoleShort.text = roleText

        // profile block (sample values)
        tvName.text = "Dr. Amit Kumar"
        tvDesignation.text = roleText
        tvId.text = "CHO001"

        // make profile card clickable to open profile
        val profileCard = findViewById<MaterialCardView>(R.id.profileCard)
        profileCard.setOnClickListener {
            Log.i(TAG, "profileCard clicked — launching CHOProfileActivity")
            startActivity(Intent(this, CHOProfileActivity::class.java))
        }

        // also wire the "Profile" text and header avatar to same action (safer UX)
        findViewById<TextView>(R.id.tvProfile).setOnClickListener {
            Log.i(TAG, "tvProfile clicked — launching CHOProfileActivity")
            startActivity(Intent(this, CHOProfileActivity::class.java))
        }
        appLogoSmall.setOnClickListener {
            Log.i(TAG, "header avatar clicked — launching CHOProfileActivity")
            startActivity(Intent(this, CHOProfileActivity::class.java))
        }

        // card clicks
        cardQuick.setOnClickListener { Toast.makeText(this, "Quick Consultation clicked", Toast.LENGTH_SHORT).show() }
        cardPatient.setOnClickListener {
            try {
                startActivity(Intent(this, PatientQueueActivity::class.java))
            } catch (t: Throwable) {
                Toast.makeText(this, "Can't open patient queue.", Toast.LENGTH_SHORT).show()
            }
        }
        cardRedirect.setOnClickListener { Toast.makeText(this, "Redirection clicked", Toast.LENGTH_SHORT).show() }

        // cardPast click (now uses class-level var)
        cardPast.setOnClickListener {
            Toast.makeText(this, "Open Past Consultations (todo)", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(this, PastConsultationsActivity::class.java))
        }


        cardPast.setOnClickListener {
        startActivity(Intent(this, PastConsultationsActivity::class.java))
        }

        // switch logic - using hex fallback colors (no custom R.color required)
        switchAvailable.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                tvAvailableBadge.text = "Available"
                tvAvailableBadge.setTextColor(Color.parseColor("#1F9D55")) // green
            } else {
                tvAvailableBadge.text = "Unavailable"
                tvAvailableBadge.setTextColor(Color.parseColor("#6B7280")) // gray-ish
            }
        }

        // ensure initial text for badge
        if (switchAvailable.isChecked) {
            tvAvailableBadge.text = "Available"
            tvAvailableBadge.setTextColor(Color.parseColor("#1F9D55"))
        } else {
            tvAvailableBadge.text = "Unavailable"
            tvAvailableBadge.setTextColor(Color.parseColor("#6B7280"))
        }

        // recent calendar icon tint (safe)
        try {
            val tint = Color.parseColor("#6B7280")
            icRecentCalendar.imageTintList = ColorStateList.valueOf(tint)
        } catch (t: Throwable) {
            icRecentCalendar.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray))
        }

        tvRecentSubtitle.setOnClickListener {
            Toast.makeText(this, "Open recent consultations (todo)", Toast.LENGTH_SHORT).show()
        }

        Log.i(TAG, "CHODashboard initialized")
    }
}
