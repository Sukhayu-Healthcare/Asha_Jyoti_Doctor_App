package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.ConsultationListResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.AuthPref
import com.example.ashajoyti_doctor_app.utils.TokenManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PastConsultationsActivity : AppCompatActivity() {

    private val TAG = "PastConsultationsAct"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_consultations)

        // Toolbar (if present in layout)
        val tbId = resources.getIdentifier("toolbarPast", "id", packageName)
        if (tbId != 0) {
            val tb = findViewById<androidx.appcompat.widget.Toolbar>(tbId)
            setSupportActionBar(tb)
        }

        val rv = findViewById<RecyclerView>(R.id.rvPast)
        rv.layoutManager = LinearLayoutManager(this)

        // attach an empty adapter early so RecyclerView won't warn
        rv.adapter = PastConsultationsAdapter(emptyList()) { item ->
            // Open the prescription page with consultation details
            val intent = Intent(this@PastConsultationsActivity, PrescriptionActivity::class.java)
            intent.putExtra("patient_id", item.patientId)
            intent.putExtra("patient_name", item.name)
            intent.putExtra("patient_phone", item.phone)
            intent.putExtra("patient_age", item.age)
            intent.putExtra("consultation_id", item.consultationId)
            startActivity(intent)
        }

        // Use TokenManager to ensure "Bearer " prefix
        val tokenHeader = TokenManager.getAuthHeader(this)
        if (tokenHeader.isNullOrBlank()) {
            // fallback: if saved token exists but no Bearer, show warning
            val raw = AuthPref.getToken(this)
            Log.w(TAG, "No auth header (Bearer) found. rawToken=${raw != null}")
            Toast.makeText(this, "Login expired or token missing. Please login.", Toast.LENGTH_SHORT).show()
            return
        }

        // Debug log
        Log.d(TAG, "Calling getConsultations with tokenHeader=${tokenHeader.takeIf { it.length < 40 } ?: tokenHeader.replaceRange(10, tokenHeader.length, "…")}")

        ApiClient.api.getConsultations(tokenHeader).enqueue(object : Callback<ConsultationListResponse> {
            override fun onResponse(call: Call<ConsultationListResponse>, response: Response<ConsultationListResponse>) {
                if (!response.isSuccessful) {
                    // log status + body for debugging
                    val code = response.code()
                    val err: String = try {
                        val eb: ResponseBody? = response.errorBody()
                        eb?.string() ?: "no error body"
                    } catch (ex: Exception) {
                        "errorBodyReadFailed: ${ex.message}"
                    }
                    Log.e(TAG, "getConsultations failed: code=$code err=$err")
                    if (code == 401 || code == 403) {
                        // unauthorized -> token issue
                        Toast.makeText(this@PastConsultationsActivity, "Unauthorized. Please login again.", Toast.LENGTH_LONG).show()
                        return
                    }

                    Toast.makeText(this@PastConsultationsActivity, "Failed to load consultations (code $code). See logs.", Toast.LENGTH_SHORT).show()
                    return
                }

                val body = response.body()
                if (body == null) {
                    Log.e(TAG, "getConsultations succeeded but returned empty body")
                    Toast.makeText(this@PastConsultationsActivity, "No consultations found", Toast.LENGTH_SHORT).show()
                    return
                }

                // Build display list - extract patient data from consultation response
                val list = body.consultations
                val display = list.mapIndexed { index, consultation ->
                    val dateOnly = consultation.consultation_date.split("T")[0].split(" ")[0]
                    
                    // Log prescription data from API
                    Log.d(TAG, "Consultation $index - Raw API data:")
                    Log.d(TAG, "  medicine_name: ${consultation.medicine_name}")
                    Log.d(TAG, "  dosage: ${consultation.dosage}")
                    Log.d(TAG, "  prescription_notes: ${consultation.prescription_notes}")
                    
                    // Extract patient data from consultation response (try multiple field names)
                    val patientName = consultation.patient_name 
                        ?: consultation.name 
                        ?: consultation.patient?.patient_name
                        ?: consultation.patient?.name
                        ?: "Patient ${consultation.patient_id}"
                    
                    val patientPhone = consultation.patient_phone 
                        ?: consultation.phone 
                        ?: consultation.patient?.phone
                        ?: null
                    
                    val patientDob = consultation.patient_dob 
                        ?: consultation.patient?.dob
                        ?: null
                    
                    // Calculate age from DOB if available
                    val age = if (!patientDob.isNullOrEmpty()) {
                        try {
                            val dobYear = patientDob.split("-")[0].toInt()
                            val currentYear = java.time.Year.now().value
                            (currentYear - dobYear).toString()
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to parse age from DOB: $patientDob")
                            "N/A"
                        }
                    } else {
                        "N/A"
                    }
                    
                    val gender = consultation.patient_gender 
                        ?: consultation.gender 
                        ?: consultation.patient?.gender
                        ?: "N/A"
                    
                    Log.d(TAG, "Consultation $index: name=$patientName, phone=$patientPhone, dob=$patientDob, age=$age")
                    
                    PastConsultation(
                        name = patientName,
                        patientId = consultation.patient_id.toString(),
                        age = age,
                        gender = gender,
                        phone = patientPhone?.toString() ?: "N/A",
                        symptoms = consultation.diagnosis ?: "N/A",
                        visit = dateOnly,
                        wait = "",
                        vitals = "",
                        feedbackRating = 0.0,
                        feedbackText = "",
                        consultationId = consultation.consultation_id.toString(),
                        docId = consultation.doc_id.toString(),
                        notes = consultation.notes ?: "No notes",
                        medicineName = consultation.medicine_name ?: "",
                        dosage = consultation.dosage ?: "",
                        prescriptionNotes = consultation.prescription_notes ?: ""
                    )
                }
                
                // Set adapter with data extracted from consultation response
                rv.adapter = PastConsultationsAdapter(display) { item ->
                    val intent = Intent(this@PastConsultationsActivity, PrescriptionActivity::class.java)
                    intent.putExtra("patient_id", item.patientId)
                    intent.putExtra("patient_name", item.name)
                    intent.putExtra("patient_phone", item.phone)
                    intent.putExtra("consultation_id", item.consultationId)
                    intent.putExtra("medicine_name", item.medicineName)
                    intent.putExtra("dosage", item.dosage)
                    intent.putExtra("prescription_notes", item.prescriptionNotes)
                    startActivity(intent)
                }
                
                Log.d(TAG, "✓ Loaded ${display.size} consultations with patient data from API response")
            }

            override fun onFailure(call: Call<ConsultationListResponse>, t: Throwable) {
                Log.e(TAG, "getConsultations onFailure", t)
                Toast.makeText(this@PastConsultationsActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
