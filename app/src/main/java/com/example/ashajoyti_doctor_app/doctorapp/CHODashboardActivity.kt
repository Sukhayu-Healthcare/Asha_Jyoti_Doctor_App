package com.example.ashajoyti_doctor_app.doctorapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.config.RoleConfigFactory
import com.example.ashajoyti_doctor_app.model.Role
import com.example.ashajoyti_doctor_app.model.ConsultationListResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.AuthPref
import com.example.ashajoyti_doctor_app.utils.TokenManager
import com.example.ashajoyti_doctor_app.network.DoctorWebSocketManager

import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CHODashboardActivity : AppCompatActivity() {

    private val TAG = "CHODashboardActivity"
    private val PERMISSION_REQUEST_CODE = 1001

    private lateinit var cardQuick: MaterialCardView
    private lateinit var cardPatient: MaterialCardView
    private lateinit var cardPast: MaterialCardView
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var tvAvailableBadge: TextView

    private lateinit var appLogoSmall: ImageView
    private lateinit var headerRoleShort: TextView

    private lateinit var tvName: TextView
    private lateinit var tvDesignation: TextView
    private lateinit var tvId: TextView

    private lateinit var tvTodayCount: TextView
    private lateinit var tvQueueCount: TextView
    private lateinit var tvWaitingCount: TextView

    private lateinit var icRecentCalendar: ImageView
    private lateinit var tvRecentSubtitle: TextView

    private lateinit var tvPatientQueueTitle: TextView
    private lateinit var tvQuickTitle: TextView

    private var webSocketManager: DoctorWebSocketManager? = null
    private var eglBase: EglBase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // find views
        cardQuick = findViewById(R.id.cardQuick)
        cardPatient = findViewById(R.id.cardPatientQueue)
        cardPast = findViewById(R.id.cardPastConsultations)
        switchAvailable = findViewById(R.id.switchAvailable)
        tvAvailableBadge = findViewById(R.id.tvAvailableBadge)

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

        // Request permissions first
        requestPermissions()

        // initial UI from saved prefs
        applyRoleToUi()

        // load saved doctor info (from login)
        val intentName = intent.getStringExtra("extra_username")
        val savedName = AuthPref.getDoctorName(this)
        val displayName = intentName ?: savedName ?: "Dr. (Unknown)"
        tvName.text = displayName

        // speciality
        tvDesignation.text = AuthPref.getDoctorSpeciality(this) ?: tvDesignation.text

        // show doctor ID
        val roleName = AuthPref.getRole(this) ?: "CHO"
        val role = Role.fromName(roleName)
        val savedId = AuthPref.getDoctorId(this)
        val idPrefix = when (role) {
            Role.CHO -> "CHO"
            Role.MO -> "MO"
            Role.CIVIL -> "CD"
            Role.EMERGENCY -> "ED"
        }
        tvId.text = if (savedId > 0)
            String.format(Locale.getDefault(), "%s%03d", idPrefix, savedId)
        else
            "${idPrefix}001"

        // profile click
        val profileCard = findViewById<MaterialCardView>(R.id.profileCard)
        profileCard.setOnClickListener {
            startActivity(Intent(this@CHODashboardActivity, CHOProfileActivity::class.java))
        }
        findViewById<TextView>(R.id.tvProfile).setOnClickListener {
            startActivity(Intent(this@CHODashboardActivity, CHOProfileActivity::class.java))
        }
        appLogoSmall.setOnClickListener {
            startActivity(Intent(this@CHODashboardActivity, CHOProfileActivity::class.java))
        }

        // card clicks
        cardQuick.setOnClickListener {
            try {
                val intent = Intent(this@CHODashboardActivity, QuickConsultActivity::class.java)

                // pass patient info (if available)
                intent.putExtra("patient_name", "Mrs. S Sharma")

                // always pass the role so the next activity doesn't need to fight to find it
                val roleNameToSend = role.name
                intent.putExtra("role", roleNameToSend)

                // pass auth token header if QuickConsultActivity needs to call APIs immediately
                val tokenHeader = TokenManager.getAuthHeader(this@CHODashboardActivity)
                if (!tokenHeader.isNullOrBlank()) {
                    intent.putExtra("auth_header", tokenHeader)
                }

                // debug logs (remove in production)
                Log.d(TAG, "Starting QuickConsultActivity role=$roleNameToSend token-present=${!tokenHeader.isNullOrBlank()}")

                startActivity(intent)
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to open QuickConsultActivity", t)
                Toast.makeText(this, "Unable to open quick consult.", Toast.LENGTH_SHORT).show()
            }
        }

        cardPatient.setOnClickListener {
            try {
                val intent = Intent(this@CHODashboardActivity, PatientQueueActivity::class.java)
                intent.putExtra("role", role.name)
                startActivity(intent)
            } catch (t: Throwable) {
                Log.e(TAG, "Error opening PatientQueueActivity", t)
                Toast.makeText(this, "Can't open patient queue.", Toast.LENGTH_SHORT).show()
            }
        }

        cardPast.setOnClickListener {
            try {
                startActivity(Intent(this@CHODashboardActivity, PastConsultationsActivity::class.java))
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to start PastConsultationsActivity", t)
                Toast.makeText(this, "Unable to open Past Consultations.", Toast.LENGTH_SHORT).show()
            }
        }

        // switch logic - update badge text/color AND backend WebSocket status
        switchAvailable.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            val statusSubtitle = findViewById<TextView>(R.id.tvStatusSubtitle)
            val doctorIdText = tvId.text.toString() // e.g. CHO001
            val doctorLevel = doctorIdText.takeWhile { it.isLetter() } // "CHO"

            if (isChecked) {
                tvAvailableBadge.text = "Available"
                tvAvailableBadge.setTextColor(Color.parseColor("#1F9D55"))
                statusSubtitle.text = "You are available for consultations"

                // Connect using instance method
                connectWebSocket(doctorIdText, doctorLevel)
            } else {
                tvAvailableBadge.text = "Unavailable"
                tvAvailableBadge.setTextColor(Color.parseColor("#6B7280"))
                statusSubtitle.text = "You are not available for consultations"

                // Disconnect using instance
                webSocketManager?.disconnect()
                webSocketManager = null
            }
        }

        // initial badge & subtitle state
        try {
            val subtitle = findViewById<TextView>(R.id.tvStatusSubtitle)
            if (switchAvailable.isChecked) {
                tvAvailableBadge.text = "Available"
                tvAvailableBadge.setTextColor(Color.parseColor("#1F9D55"))
                subtitle.text = "You are available for consultations"
            } else {
                tvAvailableBadge.text = "Unavailable"
                tvAvailableBadge.setTextColor(Color.parseColor("#6B7280"))
                subtitle.text = "You are not available for consultations"
            }
        } catch (_: Throwable) {}

        // tint icon
        try {
            val tint = Color.parseColor("#7D8790")
            icRecentCalendar.imageTintList = ColorStateList.valueOf(tint)
        } catch (t: Throwable) {
            icRecentCalendar.imageTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray))
        }

        tvRecentSubtitle.setOnClickListener {
            Toast.makeText(this, "Open recent consultations (todo)", Toast.LENGTH_SHORT).show()
        }

        // fetch metrics
        loadConsultationMetrics()

        // Initialize EglBase only when needed
        try {
            eglBase = EglBase.create()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create EglBase: ${e.message}")
        }

    }

    override fun onResume() {
        super.onResume()
        // Update header role each time
        applyRoleToUi()
    }

    private fun applyRoleToUi() {
        val roleName = AuthPref.getRole(this) ?: "CHO"
        val role = Role.fromName(roleName)
        val cfg = RoleConfigFactory.get(role)
        headerRoleShort.text = cfg.designationLabel
        tvDesignation?.text = cfg.designationLabel
        tvPatientQueueTitle?.text = cfg.patientQueueLabel
        tvQuickTitle?.text = cfg.quickConsultLabel
    }

    private fun redirectToLogin() {
        TokenManager.clear(this)
        AuthPref.clear(this)
        val i = Intent(this, CHOLoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }

    private fun loadConsultationMetrics() {
        val token = TokenManager.getAuthHeader(this)
        if (token.isNullOrBlank()) {
            Log.w(TAG, "No auth token found — redirecting to login or showing message")
            Toast.makeText(this, "Please login to load dashboard", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.api.getConsultations(token).enqueue(object : Callback<ConsultationListResponse> {
            override fun onResponse(
                call: Call<ConsultationListResponse>,
                response: Response<ConsultationListResponse>
            ) {
                if (!response.isSuccessful || response.body() == null) {
                    if (response.code() == 401 || response.code() == 403) {
                        redirectToLogin()
                        return
                    }
                    Log.e(TAG, "Failed to load consultations: code=${response.code()}")
                    Toast.makeText(
                        this@CHODashboardActivity,
                        "Failed to load dashboard metrics",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val body = response.body()!!
                val list = body.consultations
                val totalWaiting = body.total
                tvWaitingCount.text = totalWaiting.toString()
                tvQueueCount.text = list.size.toString()

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = sdf.format(Date())
                val todayCount = list.count { it.consultation_date.startsWith(today) }
                tvTodayCount.text = todayCount.toString()
                tvRecentSubtitle.text =
                    if (todayCount > 0) "You have $todayCount consultations today" else "No consultations today"
            }

            override fun onFailure(call: Call<ConsultationListResponse>, t: Throwable) {
                Log.e(TAG, "Consultations API failure", t)
                Toast.makeText(
                    this@CHODashboardActivity,
                    "Network error while loading dashboard",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Updated connectWebSocket to accept parameters
    private fun connectWebSocket(doctorId: String, doctorLevel: String) {
        try {
            webSocketManager = DoctorWebSocketManager(
                this,
                null, // localVideoView - will be null for now
                null, // remoteVideoView - will be null for now
                eglBase?.eglBaseContext
            )

            webSocketManager?.apply {
                onIncomingCall = { patientId ->
                    runOnUiThread {
                        showIncomingCallDialog(patientId)
                    }
                }
                onCallConnected = {
                    runOnUiThread {
                        Toast.makeText(this@CHODashboardActivity, "Call connected", Toast.LENGTH_SHORT).show()
                    }
                }
                onCallEnded = { reason ->
                    runOnUiThread {
                        Toast.makeText(this@CHODashboardActivity, "Call ended: $reason", Toast.LENGTH_SHORT).show()
                    }
                }
                onError = { error ->
                    runOnUiThread {
                        Toast.makeText(this@CHODashboardActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "WebSocket error: $error")
                    }
                }

                connect(doctorId, doctorLevel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create WebSocketManager: ${e.message}", e)
            Toast.makeText(this, "Failed to connect: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (!allGranted) {
                Toast.makeText(
                    this,
                    "Camera and microphone permissions are required for video calls",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showIncomingCallDialog(patientId: String) {
        // Check if patient ID is valid
        if (patientId.isEmpty()) {
            Log.e(TAG, "Invalid patient ID received")
            Toast.makeText(this, "Invalid incoming call", Toast.LENGTH_SHORT).show()
            return
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Incoming Call")
            .setMessage("Patient ID: $patientId is calling")
            .setPositiveButton("Accept") { _, _ ->
                // Check permissions before accepting
                if (!hasRequiredPermissions()) {
                    Toast.makeText(this, "Please grant camera and microphone permissions", Toast.LENGTH_LONG).show()
                    requestPermissions()
                    return@setPositiveButton
                }

                // Launch video call activity
                val intent = Intent(this, VideoCallActivity::class.java)
                intent.putExtra("patient_id", patientId)
                intent.putExtra("doctor_id", tvId.text.toString())
                startActivity(intent)
                
                // Accept call through WebSocket
                webSocketManager?.acceptCall()
            }
            .setNegativeButton("Reject") { _, _ ->
                webSocketManager?.rejectCall("Doctor is busy")
            }
            .setCancelable(false)
            .show()
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager?.disconnect()
        webSocketManager = null
        eglBase?.release()
        eglBase = null
    }
}
