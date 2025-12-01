package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.config.RoleConfigFactory
import com.example.ashajoyti_doctor_app.model.Role
import com.example.ashajoyti_doctor_app.model.ConsultationListResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.AuthPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CHODashboardActivity : AppCompatActivity() {

    private val TAG = "CHODashboardActivity"

    private lateinit var cardQuick: MaterialCardView
    private lateinit var cardPatient: MaterialCardView
    private lateinit var cardPast: MaterialCardView
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var tvAvailableBadge: TextView

    // header/profile/recent views
    private lateinit var appTitle: TextView
    private lateinit var appLogoSmall: ImageView
    private lateinit var headerRoleShort: TextView

    private lateinit var tvName: TextView
    private lateinit var tvDesignation: TextView
    private lateinit var tvId: TextView

    // metric views
    private lateinit var tvTodayCount: TextView
    private lateinit var tvQueueCount: TextView
    private lateinit var tvWaitingCount: TextView

    private lateinit var icRecentCalendar: ImageView
    private lateinit var tvRecentSubtitle: TextView

    // text labels inside cards
    private lateinit var tvPatientQueueTitle: TextView
    private lateinit var tvQuickTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // find views
        cardQuick = findViewById(R.id.cardQuick)
        cardPatient = findViewById(R.id.cardPatientQueue)
        cardPast = findViewById(R.id.cardPastConsultations)
        switchAvailable = findViewById(R.id.switchAvailable)
        tvAvailableBadge = findViewById(R.id.tvAvailableBadge)

        appTitle = findViewById(R.id.appTitle)
        appLogoSmall = findViewById(R.id.appLogoSmall)
        headerRoleShort = findViewById(R.id.headerRoleShort)

        tvName = findViewById(R.id.tvName)
        tvDesignation = findViewById(R.id.tvDesignation)
        tvId = findViewById(R.id.tvId)

        tvTodayCount = findViewById(R.id.tvTodayCount)
        tvQueueCount = findViewById(R.id.tvQueueCount)
        tvWaitingCount = findViewById(R.id.tvWaitingCount)

        icRecentCalendar = findViewById(R.id.ic_recent_calendar)
        tvRecentSubtitle = findViewById(R.id.tvRecentSubtitle)

        tvPatientQueueTitle = findViewById(R.id.tvPatientQueueTitle)
        tvQuickTitle = findViewById(R.id.tvQuickTitle)

        // header text
        appTitle.text = "ASHA JYOTI Doctor"

        // Determine role from prefs (fallback to CHO)
        val roleName = AuthPref.getRole(this)
        val role = Role.fromName(roleName)
        val cfg = RoleConfigFactory.get(role)

        // apply role-based labels
        headerRoleShort.text = cfg.designationLabel
        tvDesignation.text = cfg.designationLabel

        tvPatientQueueTitle.text = cfg.patientQueueLabel
        tvQuickTitle.text = cfg.quickConsultLabel

        // load saved doctor info (from login)
        val intentName = intent.getStringExtra("extra_username")
        val savedName = AuthPref.getDoctorName(this)
        val displayName = intentName ?: savedName ?: "Dr. (Unknown)"
        tvName.text = displayName

        // speciality (if backend saved it)
        val savedSpeciality = AuthPref.getDoctorSpeciality(this)
        tvDesignation.text = savedSpeciality ?: cfg.designationLabel

        // show doctor id if saved
        val savedId = AuthPref.getDoctorId(this)

        // id prefix based on role
        val idPrefix = when (role) {
            Role.CHO -> "CHO"
            Role.MO -> "MO"
            Role.CIVIL -> "CD"
            Role.EMERGENCY -> "ED"
        }

        tvId.text = if (savedId > 0) {
            String.format(Locale.getDefault(), "%s%03d", idPrefix, savedId)
        } else {
            "${idPrefix}001"
        }

        // profile click actions
        val profileCard = findViewById<MaterialCardView>(R.id.profileCard)
        profileCard.setOnClickListener {
            Log.i(TAG, "profileCard clicked — launching CHOProfileActivity")
            startActivity(Intent(this@CHODashboardActivity, CHOProfileActivity::class.java))
        }

        findViewById<TextView>(R.id.tvProfile).setOnClickListener {
            Log.i(TAG, "tvProfile clicked — launching CHOProfileActivity")
            startActivity(Intent(this@CHODashboardActivity, CHOProfileActivity::class.java))
        }
        appLogoSmall.setOnClickListener {
            Log.i(TAG, "header avatar clicked — launching CHOProfileActivity")
            startActivity(Intent(this@CHODashboardActivity, CHOProfileActivity::class.java))
        }

        // card clicks: keep general behavior but pass role where useful
        cardQuick.setOnClickListener {
            Toast.makeText(this, "${cfg.quickConsultLabel} clicked", Toast.LENGTH_SHORT).show()
        }
        cardPatient.setOnClickListener {
            try {
                Log.i(TAG, "Opening PatientQueueActivity for role=${role.name}")
                val intent = Intent(this@CHODashboardActivity, PatientQueueActivity::class.java)
                intent.putExtra("role", role.name)
                startActivity(intent)
            } catch (t: Throwable) {
                Log.e(TAG, "Error opening PatientQueueActivity", t)
                Toast.makeText(this, "Can't open patient queue.", Toast.LENGTH_SHORT).show()
            }
        }

        // Past Consultations card (now in grid)
        cardPast.setOnClickListener {
            Log.i(TAG, "cardPast clicked — attempting to open PastConsultationsActivity")
            try {
                startActivity(Intent(this@CHODashboardActivity, PastConsultationsActivity::class.java))
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to start PastConsultationsActivity", t)
                Toast.makeText(this, "Unable to open Past Consultations.", Toast.LENGTH_SHORT).show()
            }
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

        // initial badge state
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

        // fetch consultations to populate metrics
        loadConsultationMetrics()

        Log.i(TAG, "CHODashboard initialized for role=${role.name}")
    }

    private fun loadConsultationMetrics() {
        val token = AuthPref.getToken(this)
        if (token.isNullOrBlank()) {
            Log.w(TAG, "No auth token found — redirecting to login or showing message")
            Toast.makeText(this, "Please login to load dashboard", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.api.getConsultations(token).enqueue(object : Callback<ConsultationListResponse> {
            override fun onResponse(call: Call<ConsultationListResponse>, response: Response<ConsultationListResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    Log.e(TAG, "Failed to load consultations: code=${response.code()}")
                    Toast.makeText(this@CHODashboardActivity, "Failed to load dashboard metrics", Toast.LENGTH_SHORT).show()
                    return
                }

                val body = response.body()!!
                val list = body.consultations

                // total waiting as returned by backend (if body.total is valid)
                val totalWaiting = body.total
                tvWaitingCount.text = totalWaiting.toString()

                // queueCount = number of consultations in returned list
                tvQueueCount.text = list.size.toString()

                // today's date check (assumes consultation_date starts with yyyy-MM-dd or similar)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = sdf.format(Date())
                val todayCount = list.count { it.consultation_date.startsWith(today) }
                tvTodayCount.text = todayCount.toString()

                // subtitle update with sample text
                tvRecentSubtitle.text = if (todayCount > 0) "You have $todayCount consultations today" else "No consultations today"
            }

            override fun onFailure(call: Call<ConsultationListResponse>, t: Throwable) {
                Log.e(TAG, "Consultations API failure", t)
                Toast.makeText(this@CHODashboardActivity, "Network error while loading dashboard", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
