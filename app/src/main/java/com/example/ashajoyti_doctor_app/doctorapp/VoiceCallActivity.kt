package com.example.ashajoyti_doctor_app.doctorapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class VoiceCallActivity : AppCompatActivity() {

    // UI refs
    private lateinit var tvDoctorName: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvCallStatus: TextView
    private lateinit var btnTurnOnVideo: FloatingActionButton
    private lateinit var btnEndCall: FloatingActionButton
    private lateinit var btnChat: FloatingActionButton
    private lateinit var layoutHeader: LinearLayout

    // Timer state
    private var startTimeMs: Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            val elapsed = System.currentTimeMillis() - startTimeMs
            tvTimer.text = formatElapsed(elapsed)
            handler.postDelayed(this, 1000L)
        }
    }

    private var videoRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the existing layout file you provided
        setContentView(R.layout.activity_voice_call)

        // find views (IDs must match activity_voice_call.xml)
        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvTimer = findViewById(R.id.tvTimer)
        tvCallStatus = findViewById(R.id.tvCallStatus)
        btnTurnOnVideo = findViewById(R.id.btnTurnOnVideo)
        btnEndCall = findViewById(R.id.btnEndCall)
        btnChat = findViewById(R.id.btnChat)
        layoutHeader = findViewById(R.id.layoutHeader)

        val doctorName = intent.getStringExtra("doctor_name") ?: "Dr. अमित कुमार"
        tvDoctorName.text = doctorName

        startCallTimer()

        btnTurnOnVideo.setOnClickListener { onTurnOnVideoClicked() }
        btnEndCall.setOnClickListener { onEndCallClicked() }
        btnChat.setOnClickListener { onChatClicked() }

        tvCallStatus.text = "Voice call in progress"
    }

    private fun startCallTimer() {
        startTimeMs = System.currentTimeMillis()
        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
    }

    private fun stopCallTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun formatElapsed(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = (totalSeconds / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun onTurnOnVideoClicked() {
        videoRequested = !videoRequested
        if (videoRequested) {
            tvCallStatus.text = "Switching to video..."
            try {
                val intent = Intent(this, VideoCallActivity::class.java)
                intent.putExtra("doctor_name", tvDoctorName.text.toString())
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Video activity not available", Toast.LENGTH_SHORT).show()
                tvCallStatus.text = "Voice call in progress"
                videoRequested = false
            }
        } else {
            tvCallStatus.text = "Voice call in progress"
        }
    }

    private fun onEndCallClicked() {
        AlertDialog.Builder(this)
            .setTitle("End Call")
            .setMessage("Are you sure you want to end the consultation?")
            .setPositiveButton("End") { _, _ -> performEndCall() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performEndCall() {
        stopCallTimer()
        tvCallStatus.text = "Call ended"
        tvTimer.visibility = View.GONE
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun onChatClicked() {
        try {
            // If ChatFallbackActivity exists in your project use it; otherwise change to your chat activity class
            val intent = Intent(this, ChatFallbackActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Chat not available (placeholder)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCallTimer()
    }
}
