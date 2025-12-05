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
        setContentView(R.layout.activity_quick_consult)

        bindViews()
        populateDummyData()
        setupListeners()
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

        consultationPanel = findViewById(R.id.consultationPanel)
        overlayContainer = findViewById(R.id.overlayContainer)

        // ensure overlay does not consume touches until mini-overlay is shown
        overlayContainer.isClickable = false
        overlayContainer.isFocusable = false
    }

    private fun populateDummyData() {
        tvPatientName.text = "मोहन गुप्ता"
        tvPatientId.text = "P003"

        tvPatientAge.text = "55 yrs"
        tvPatientGender.text = "Male"

        tvLastVisit.text = "Last visit: 2025-11-28"
        tvLocation.text = "Location: Ward 3B"
    }

    private fun setupListeners() {

        btnStartVideo.setOnClickListener {
            Toast.makeText(this, "Video Call button clicked", Toast.LENGTH_SHORT).show()
            if (checkCameraAudioPermissions()) startVideoCallFlow() else requestCameraAudioPermissions()
        }

        btnStartVoice.setOnClickListener {
            Toast.makeText(this, "Voice Call button clicked", Toast.LENGTH_SHORT).show()
            startVoiceCallFlow()
        }

        btnStartChat.setOnClickListener {
            Toast.makeText(this, "Text Chat button clicked", Toast.LENGTH_SHORT).show()
            openChat()
        }

        // overlayContainer click listener is enabled only when mini-overlay is shown.
    }

    // Launch Video - uses explicit class reference (compile-time checked)
    private fun startVideoCallFlow() {
        Toast.makeText(this, "Opening Video Call…", Toast.LENGTH_SHORT).show()
        try {
            val intent = Intent(this, VideoCallActivity::class.java).apply {
                putExtra("patient_id", tvPatientId.text.toString())
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "VideoCallActivity start failed", e)
            Toast.makeText(this, "VideoCallActivity not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch Voice - explicit class reference
    private fun startVoiceCallFlow() {
        Toast.makeText(this, "Opening Voice Call…", Toast.LENGTH_SHORT).show()
        try {
            val intent = Intent(this, VoiceCallActivity::class.java).apply {
                putExtra("patient_id", tvPatientId.text.toString())
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "VoiceCallActivity start failed", e)
            Toast.makeText(this, "VoiceCallActivity not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch Chat - explicit class reference to ChatFallbackActivity
    private fun openChat() {
        Toast.makeText(this, "Opening Chat…", Toast.LENGTH_SHORT).show()
        try {
            val intent = Intent(this, ChatFallbackActivity::class.java).apply {
                putExtra("patient_name", tvPatientName.text.toString())
                putExtra("patient_id", tvPatientId.text.toString())
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "ChatFallbackActivity start failed", e)
            // If this happens, show detailed log in Logcat and a friendly toast
            Toast.makeText(this, "ChatActivity not found — check manifest & package name", Toast.LENGTH_LONG).show()
        }
    }

    // mini overlay (unchanged)
    private fun showMiniCallOverlay() {
        if (miniOverlayView != null) return

        consultationPanel.visibility = View.GONE

        val card = CardView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                val margin = (16 * resources.displayMetrics.density).toInt()
                setMargins(margin, margin + 80, margin, 0)
            }
            radius = 12f
            cardElevation = 8f
            setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 12, 12, 12)
        }

        val avatar = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(80, 80)
            setImageDrawable(patientAvatar.drawable)
        }

        val name = TextView(this).apply {
            text = tvPatientName.text
            textSize = 14f
            setPadding(12, 0, 0, 0)
        }

        val btnRestore = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            background = null
        }

        row.addView(avatar)
        row.addView(name)
        row.addView(btnRestore)

        card.addView(row)
        overlayContainer.addView(card)
        miniOverlayView = card

        // make overlay container intercept taps while overlay present
        overlayContainer.isClickable = true
        overlayContainer.setOnClickListener { restoreFromMiniOverlay() }

        btnRestore.setOnClickListener { restoreFromMiniOverlay() }
        card.setOnClickListener { restoreFromMiniOverlay() }
    }

    private fun restoreFromMiniOverlay() {
        miniOverlayView?.let {
            overlayContainer.removeView(it)
            miniOverlayView = null
        }
        overlayContainer.isClickable = false
        overlayContainer.setOnClickListener(null)
        consultationPanel.visibility = View.VISIBLE
    }

    // permissions
    private fun checkCameraAudioPermissions(): Boolean {
        val cam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val mic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        return cam && mic
    }

    private fun requestCameraAudioPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val req = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                req.add(Manifest.permission.CAMERA)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                req.add(Manifest.permission.RECORD_AUDIO)
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
