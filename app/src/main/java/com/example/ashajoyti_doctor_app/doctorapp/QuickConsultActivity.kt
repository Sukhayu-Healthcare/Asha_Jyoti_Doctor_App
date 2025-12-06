package com.example.ashajoyti_doctor_app.doctorapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.PatientResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.TokenManager
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class QuickConsultActivity : AppCompatActivity() {

    private val TAG = "QuickConsultActivity"

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

    private lateinit var btnOpenVitals: Button
    private lateinit var btnOpenExam: Button
    private lateinit var btnOpenPrescription: Button

    private lateinit var consultationPanel: CardView
    private lateinit var overlayContainer: FrameLayout

    private var miniOverlayView: View? = null

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

        try {
            setContentView(R.layout.activity_quick_consult)
        } catch (e: Exception) {
            Log.e(TAG, "layout inflate error: ${e.message}", e)
            Toast.makeText(this, "Cannot open Quick Consult: ${e.message ?: "layout error"}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        bindViews()
        populateDummyData()
        setupListeners()

        val incomingPatientId = intent.getStringExtra("patient_id")
        if (!incomingPatientId.isNullOrBlank()) {
            fetchPatientDetails(incomingPatientId)
        }
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

        btnOpenVitals = findViewById(R.id.btnOpenVitals)
        btnOpenExam = findViewById(R.id.btnOpenExam)
        btnOpenPrescription = findViewById(R.id.btnOpenPrescription)

        consultationPanel = findViewById(R.id.consultationPanel)
        overlayContainer = findViewById(R.id.overlayContainer)

        overlayContainer.isClickable = false
        overlayContainer.isFocusable = false
    }

    private fun populateDummyData() {
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
        btnStartVoice.setOnClickListener { startVoiceCallFlow() }
        btnStartChat.setOnClickListener { openChat() }

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

    // -------------------------
    // Network: fetch patient by id
    // -------------------------
    private fun fetchPatientDetails(patientIdStr: String) {
        val tokenHeader = TokenManager.getAuthHeader(this)
        if (tokenHeader.isNullOrBlank()) {
            Toast.makeText(this, "Auth token missing — please login", Toast.LENGTH_SHORT).show()
            return
        }

        val idInt = try {
            patientIdStr.toInt()
        } catch (e: Exception) {
            Log.e(TAG, "Invalid patient id format: $patientIdStr")
            Toast.makeText(this, "Invalid patient id", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Loading patient details...", Toast.LENGTH_SHORT).show()

        ApiClient.api.getPatient(tokenHeader, idInt).enqueue(object : Callback<PatientResponse> {
            override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                if (!response.isSuccessful) {
                    Log.e(TAG, "getPatient failed code=${response.code()} msg=${response.message()}")
                    Toast.makeText(this@QuickConsultActivity, "Failed to load patient (${response.code()})", Toast.LENGTH_SHORT).show()
                    return
                }
                val body = response.body()
                if (body == null) {
                    Log.e(TAG, "getPatient returned empty body")
                    Toast.makeText(this@QuickConsultActivity, "No patient data", Toast.LENGTH_SHORT).show()
                    return
                }

                populatePatientCard(body)
            }

            override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                Log.e(TAG, "getPatient onFailure", t)
                Toast.makeText(this@QuickConsultActivity, "Network error loading patient", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populatePatientCard(p: PatientResponse) {
        tvPatientId.text = "P${p.patient_id}"
        val nameFallback = if (p.user_id != null) "Patient ${p.user_id}" else "Patient ${p.patient_id}"
        tvPatientName.text = nameFallback
        tvPatientGender.text = p.gender ?: "N/A"
        tvPatientAge.text = calculateAge(p.dob)
        val location = listOfNotNull(p.village, p.taluka, p.district).joinToString(", ")
        tvLocation.text = if (location.isNotBlank()) location else "Location: N/A"
        tvLastVisit.text = p.created_at ?: "Last visit: N/A"

        // set phone & history into the small fields we added in XML (nullable)
        findViewById<TextView?>(R.id.tvPhone)?.text = p.phone?.toString() ?: "Phone: --"
        findViewById<TextView?>(R.id.tvHistory)?.text = p.history ?: "History: -"
    }

    private fun calculateAge(dob: String?): String {
        if (dob.isNullOrBlank()) return "— yrs"
        try {
            val formats = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy", "dd/MM/yyyy")
            var parsed: Date? = null
            for (f in formats) {
                try {
                    parsed = SimpleDateFormat(f, Locale.getDefault()).parse(dob)
                    if (parsed != null) break
                } catch (_: Exception) { }
            }
            if (parsed == null) return "— yrs"
            val dobCal = Calendar.getInstance().apply { time = parsed }
            val now = Calendar.getInstance()
            var age = now.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR)
            if (now.get(Calendar.DAY_OF_YEAR) < dobCal.get(Calendar.DAY_OF_YEAR)) age--
            return "$age yrs"
        } catch (e: Exception) {
            return "— yrs"
        }
    }

    // -------------------------
    // Calls / permissions helpers
    // -------------------------
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
