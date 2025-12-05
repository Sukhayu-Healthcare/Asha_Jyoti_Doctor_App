package com.example.ashajoyti_doctor_app.doctorapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.ashajoyti_doctor_app.R
import de.hdodenhof.circleimageview.CircleImageView

class QuickConsultActivity : AppCompatActivity() {

    private lateinit var patientAvatar: CircleImageView
    private lateinit var tvPatientName: TextView
    private lateinit var tvPatientId: TextView

    private lateinit var tvPatientAge: TextView
    private lateinit var tvPatientGender: TextView

    private lateinit var tvLastVisit: TextView
    private lateinit var tvLocation: TextView

    private lateinit var btnStartVideo: Button
    private lateinit var btnStartVoice: Button
    private lateinit var btnStartChat: Button

    // NEW: open-pages buttons
    private lateinit var btnOpenVitals: Button
    private lateinit var btnOpenExam: Button
    private lateinit var btnOpenPrescription: Button

    private lateinit var consultationPanel: CardView
    private lateinit var overlayContainer: FrameLayout

    private var miniOverlayView: View? = null

    private val TAG = "QuickConsultActivity"

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val cam = perms[Manifest.permission.CAMERA] == true
        val mic = perms[Manifest.permission.RECORD_AUDIO] == true
        if (cam && mic) {
            startVideoCallFlow()
        } else {
            Toast.makeText(this, "Camera / Microphone required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Defensive: catch inflation errors (missing layout / bad XML)
        try {
            setContentView(R.layout.activity_quick_consult)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inflate activity_quick_consult layout: ${e.message}", e)
            Toast.makeText(this, "Cannot open Quick Consult: ${e.message ?: "layout inflate error"}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Defensive: catch binding/runtime errors during view lookup/setup
        try {
            bindViews()
            populateDummyData()
            setupListeners()
        } catch (e: Exception) {
            Log.e(TAG, "Error during QuickConsultActivity setup: ${e.message}", e)
            Toast.makeText(this, "Cannot open Quick Consult: ${e.message ?: "setup error"}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // no fragment/viewpager setup anymore
    }

    private fun bindViews() {
        patientAvatar = findViewById(R.id.patientAvatar)
        tvPatientName = findViewById(R.id.tvPatientName)
        tvPatientId = findViewById(R.id.tvPatientId)

        tvPatientAge = findViewById(R.id.tvPatientAge)
        tvPatientGender = findViewById(R.id.tvPatientGender)

        tvLastVisit = findViewById(R.id.tvLastVisit)
        tvLocation = findViewById(R.id.tvLocation)

        btnStartVideo = findViewById(R.id.btnStartVideo)
        btnStartVoice = findViewById(R.id.btnStartVoice)
        btnStartChat = findViewById(R.id.btnStartChat)

        // new buttons
        btnOpenVitals = findViewById(R.id.btnOpenVitals)
        btnOpenExam = findViewById(R.id.btnOpenExam)
        btnOpenPrescription = findViewById(R.id.btnOpenPrescription)

        consultationPanel = findViewById(R.id.consultationPanel)
        overlayContainer = findViewById(R.id.overlayContainer)

        overlayContainer.isClickable = false
        overlayContainer.isFocusable = false
    }

    private fun populateDummyData() {
        // If calling activity passed patient details, prefer that
        val pname = intent.getStringExtra("patient_name")
        val pid = intent.getStringExtra("patient_id")
        tvPatientName.text = pname ?: "मोहन गुप्ता"
        tvPatientId.text = pid ?: "P003"

        tvPatientAge.text = "55 yrs"
        tvPatientGender.text = "Male"

        tvLastVisit.text = "Last visit: 2025-11-28"
        tvLocation.text = "Location: Ward 3B"
    }

    private fun setupListeners() {
        btnStartVideo.setOnClickListener {
            if (checkCameraAudioPermissions()) startVideoCallFlow() else requestCameraAudioPermissions()
        }

        btnStartVoice.setOnClickListener {
            startVoiceCallFlow()
        }

        btnStartChat.setOnClickListener {
            openChat()
        }

        // NEW: full-screen activities
        btnOpenVitals.setOnClickListener {
            val intent = Intent(this, VitalsActivity::class.java).apply {
                putExtra("patient_id", tvPatientId.text.toString())
                putExtra("patient_name", tvPatientName.text.toString())
            }
            startActivity(intent)
        }

        btnOpenExam.setOnClickListener {
            val intent = Intent(this, ExaminationActivity::class.java).apply {
                putExtra("patient_id", tvPatientId.text.toString())
                putExtra("patient_name", tvPatientName.text.toString())
            }
            startActivity(intent)
        }

        btnOpenPrescription.setOnClickListener {
            val intent = Intent(this, PrescriptionActivity::class.java).apply {
                putExtra("patient_id", tvPatientId.text.toString())
                putExtra("patient_name", tvPatientName.text.toString())
            }
            startActivity(intent)
        }
    }

    private fun startVideoCallFlow() {
        try {
            val intent = Intent(this, VideoCallActivity::class.java).apply {
                putExtra("patient_id", tvPatientId.text.toString())
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "VideoCallActivity not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startVoiceCallFlow() {
        try {
            val intent = Intent(this, VoiceCallActivity::class.java)
            startActivity(intent)
        } catch (t: Throwable) {
            Toast.makeText(this, "VoiceCallActivity not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openChat() {
        try {
            val intent = Intent(this, ChatFallbackActivity::class.java).apply {
                putExtra("patient_name", tvPatientName.text.toString())
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "ChatActivity not available", Toast.LENGTH_SHORT).show()
        }
    }

    // mini overlay kept unchanged
    private fun showMiniCallOverlay() { /* unchanged from your earlier implementation */ }

    private fun restoreFromMiniOverlay() { /* unchanged */ }

    private fun checkCameraAudioPermissions(): Boolean {
        val cam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val mic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        return cam && mic
    }

    private fun requestCameraAudioPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val req = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) req.add(Manifest.permission.CAMERA)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) req.add(Manifest.permission.RECORD_AUDIO)
            if (req.isNotEmpty()) permissionLauncher.launch(req.toTypedArray())
        } else {
            startVideoCallFlow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        miniOverlayView = null
    }
}
