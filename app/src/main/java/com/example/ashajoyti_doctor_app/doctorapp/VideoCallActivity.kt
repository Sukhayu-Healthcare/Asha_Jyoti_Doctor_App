package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.databinding.ActivityVideoCallBinding
import com.example.ashajoyti_doctor_app.network.DoctorWebSocketManager
import org.webrtc.EglBase

class VideoCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding
    private var webSocketManager: DoctorWebSocketManager? = null
    private lateinit var eglBase: EglBase

    private var patientId: String = ""
    private var doctorId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get intent extras
        patientId = intent.getStringExtra("patient_id") ?: ""
        doctorId = intent.getStringExtra("doctor_id") ?: ""

        Log.d("VideoCallActivity", "Starting call with patient: $patientId, doctor: $doctorId")

        initWebRTC()
        setupCallbacks()

        binding.btnEndCall.setOnClickListener {
            endCall()
        }
    }

    private fun initWebRTC() {
        try {
            // Initialize EglBase
            eglBase = EglBase.create()

            // Initialize video views
            binding.localVideoView.init(eglBase.eglBaseContext, null)
            binding.localVideoView.setEnableHardwareScaler(true)
            binding.localVideoView.setMirror(true)

            binding.remoteVideoView.init(eglBase.eglBaseContext, null)
            binding.remoteVideoView.setEnableHardwareScaler(true)
            binding.remoteVideoView.setMirror(false)

            // Create WebSocket manager with video views
            webSocketManager = DoctorWebSocketManager(
                this,
                binding.localVideoView,
                binding.remoteVideoView,
                eglBase.eglBaseContext
            )

            setupCallbacks()

            // Note: The call was already accepted in CHODashboardActivity
            // This activity is just for displaying the video call UI

        } catch (e: Exception) {
            Log.e("VideoCallActivity", "Error initializing WebRTC: ${e.message}", e)
            Toast.makeText(this, "Failed to initialize video call", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupCallbacks() {
        webSocketManager?.apply {
            onCallConnected = {
                runOnUiThread {
                    Toast.makeText(this@VideoCallActivity, "Call connected", Toast.LENGTH_SHORT).show()
                    Log.d("VideoCallActivity", "Call connected successfully")
                }
            }

            onCallEnded = { reason ->
                runOnUiThread {
                    Toast.makeText(this@VideoCallActivity, "Call ended: $reason", Toast.LENGTH_SHORT).show()
                    Log.d("VideoCallActivity", "Call ended: $reason")
                    finish()
                }
            }

            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this@VideoCallActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                    Log.e("VideoCallActivity", "Call error: $error")
                }
            }
        }
    }

    private fun endCall() {
        try {
            Log.d("VideoCallActivity", "Ending call")
            webSocketManager?.disconnect()

            // Release resources
            binding.localVideoView.release()
            binding.remoteVideoView.release()
            eglBase.release()

            finish()
        } catch (e: Exception) {
            Log.e("VideoCallActivity", "Error ending call: ${e.message}", e)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            webSocketManager?.disconnect()
            webSocketManager = null

            if (::eglBase.isInitialized) {
                eglBase.release()
            }
        } catch (e: Exception) {
            Log.e("VideoCallActivity", "Error in onDestroy: ${e.message}", e)
        }
    }

    override fun onBackPressed() {
        // Show confirmation dialog before ending call
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("End Call?")
            .setMessage("Are you sure you want to end this call?")
            .setPositiveButton("End Call") { _, _ ->
                endCall()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
