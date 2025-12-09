package com.example.ashajoyti_doctor_app.doctorapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.ConsultationItem
import com.example.ashajoyti_doctor_app.model.CreateConsultationRequest
import com.example.ashajoyti_doctor_app.model.ConsultationCreateResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.TokenManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class QuickPrescriptionActivity : AppCompatActivity() {

    private lateinit var etPatientName: EditText
    private lateinit var etPatientId: EditText
    private lateinit var etPatientPhone: EditText
    private lateinit var etDate: EditText

    private lateinit var etBP: EditText
    private lateinit var etHR: EditText
    private lateinit var etTemp: EditText
    private lateinit var etOxygen: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText

    // clinical fields are intentionally commented per your request
    // private lateinit var etDiagnosis: EditText
    // private lateinit var etClinical: EditText
    private lateinit var etFollowup: EditText

    private lateinit var btnAddMed: Button
    private lateinit var btnConfirm: Button
    private lateinit var btnReset: Button

    private lateinit var rowsContainer: LinearLayout

    private val gson = Gson()
    private val frequencyList = listOf("Once daily", "Twice daily", "Thrice daily", "As needed")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prescription_consult)

        bindViews()
        
        // Populate patient data from intent if available
        val patientName = intent.getStringExtra("patient_name") ?: ""
        val patientId = intent.getStringExtra("patient_id") ?: ""
        val patientPhone = intent.getStringExtra("patient_phone") ?: ""
        
        etPatientName.setText(patientName)
        etPatientId.setText(patientId)
        etPatientPhone.setText(patientPhone)
        
        setupDatePicker()
        setupButtons()

        // Setup default spinner in the first static row
        setupSpinner(findViewById(R.id.default_spFreq))

        // add default dynamic row container initially empty (if you want default dynamic row add it here)
        // rowsContainer.removeAllViews()
        // addMedicineRow()
    }

    private fun bindViews() {
        etPatientName = findViewById(R.id.etPatientName)
        etPatientId = findViewById(R.id.etPatientId)
        etPatientPhone = findViewById(R.id.etPatientPhone)
        etDate = findViewById(R.id.etDate)

        etBP = findViewById(R.id.etBP)
        etHR = findViewById(R.id.etHR)
        etTemp = findViewById(R.id.etTemp)
        etOxygen = findViewById(R.id.etOxygen)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)

        //etDiagnosis = findViewById(R.id.etDiagnosis)
        //etClinical = findViewById(R.id.etClinical)
        etFollowup = findViewById(R.id.etFollowup)

        btnAddMed = findViewById(R.id.btnAddMed)
        btnConfirm = findViewById(R.id.btnConfirm)
        btnReset = findViewById(R.id.btnReset)

        rowsContainer = findViewById(R.id.rowsContainer)
    }

    // -----------------------
    // DATE PICKER
    // -----------------------
    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                TimePickerDialog(this, { _, hour, min ->
                    etDate.setText("$d-${m + 1}-$y $hour:$min")
                }, 12, 0, true).show()

            }, year, month, day).show()
        }
    }

    // -----------------------
    // BUTTON SETUP
    // -----------------------
    private fun setupButtons() {
        btnAddMed.setOnClickListener { addMedicineRow() }
        btnConfirm.setOnClickListener { onConfirm() }
        btnReset.setOnClickListener { resetForm() }
    }

    // -----------------------
    // ADD NEW MEDICINE ROW
    // -----------------------
    private fun addMedicineRow() {
        val row = LayoutInflater.from(this).inflate(R.layout.row_medicine_table, null)
        val spinner = row.findViewById<Spinner>(R.id.spFreq)
        setupSpinner(spinner)

        rowsContainer.addView(row)
    }

    // -----------------------
    // SETUP SPINNER VALUES
    // -----------------------
    private fun setupSpinner(spinner: Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(1) // Default: twice daily
    }

    // -----------------------
    // RESET FORM
    // -----------------------
    private fun resetForm() {
        AlertDialog.Builder(this)
            .setTitle("Reset form?")
            .setMessage("All fields will be cleared.")
            .setPositiveButton("Yes") { _, _ ->
                etPatientName.setText("")
                etPatientId.setText("")
                etPatientPhone.setText("")
                etDate.setText("")

                etBP.setText("")
                etHR.setText("")
                etTemp.setText("")
                etOxygen.setText("")
                etWeight.setText("")
                etHeight.setText("")

                // clinical fields are commented, so not cleared
                // etDiagnosis.setText("")
                // etClinical.setText("")
                etFollowup.setText("")

                rowsContainer.removeAllViews()
                // keep default visible table row as in XML — do not add extra unless desired
            }
            .setNegativeButton("No", null)
            .show()
    }

    // -----------------------
    // COLLECT ALL MED ROWS
    // -----------------------
    private fun gatherMedicines(): List<com.example.ashajoyti_doctor_app.model.ConsultationItem> {
        val meds = mutableListOf<com.example.ashajoyti_doctor_app.model.ConsultationItem>()

        // First, include default static row fields if filled
        val defaultName = findViewById<EditText>(R.id.default_etMedName)?.text?.toString()?.trim() ?: ""
        val defaultDosage = findViewById<EditText>(R.id.default_etDosage)?.text?.toString()?.trim() ?: ""
        val defaultFreq = findViewById<Spinner>(R.id.default_spFreq)?.selectedItem?.toString() ?: ""
        val defaultDuration = findViewById<EditText>(R.id.default_etDuration)?.text?.toString()?.trim() ?: ""
        if (defaultName.isNotEmpty()) {
            meds.add(
                com.example.ashajoyti_doctor_app.model.ConsultationItem(
                    medicine_name = defaultName,
                    dosage = defaultDosage,
                    frequency = defaultFreq,
                    duration = defaultDuration,
                    instructions = ""
                )
            )
        }

        // Then dynamic rows added to rowsContainer
        for (i in 0 until rowsContainer.childCount) {
            val row = rowsContainer.getChildAt(i)
            val name = row.findViewById<EditText>(R.id.etMedName)?.text?.toString()?.trim() ?: ""
            val dosage = row.findViewById<EditText>(R.id.etDosage)?.text?.toString()?.trim() ?: ""
            val freq = row.findViewById<Spinner>(R.id.spFreq)?.selectedItem?.toString() ?: ""
            val duration = row.findViewById<EditText>(R.id.etDuration)?.text?.toString()?.trim() ?: ""
            if (name.isNotEmpty()) {
                meds.add(
                    com.example.ashajoyti_doctor_app.model.ConsultationItem(
                        medicine_name = name,
                        dosage = dosage,
                        frequency = freq,
                        duration = duration,
                        instructions = ""
                    )
                )
            }
        }

        return meds
    }

    // -----------------------
    // CONFIRM + SAVE JSON + POST
    // -----------------------
    private fun onConfirm() {
        // gather medicines
        val items = gatherMedicines()
        if (items.isEmpty()) {
            Toast.makeText(this, "Add at least one medicine", Toast.LENGTH_SHORT).show()
            return
        }

        // patient id - backend expects INT
        val patientId = etPatientId.text.toString().trim().toIntOrNull() ?: 0

        // Build diagnosis string from vitals (all as strings)
        val diagnosisString = buildString {
            append("BP: ${etBP.text.toString().trim()}; ")
            append("HR: ${etHR.text.toString().trim()}; ")
            append("Temp: ${etTemp.text.toString().trim()}; ")
            append("Oxygen: ${etOxygen.text.toString().trim()}; ")
            append("Weight: ${etWeight.text.toString().trim()}; ")
            append("Height: ${etHeight.text.toString().trim()}")
        }

        // Notes come from follow-up field
        val notesString = etFollowup.text.toString().trim()

        val request = CreateConsultationRequest(
            patient_id = patientId,
            diagnosis = diagnosisString,
            notes = notesString,
            items = items
        )

        // Get token from SharedPreferences (adjust key/name to match your login flow)
        val token = TokenManager.getAuthHeader(this)

        if (token.isNullOrEmpty()) {
        Toast.makeText(this, "Auth token missing — please login again.", Toast.LENGTH_LONG).show()
        return
    }

        val loading = AlertDialog.Builder(this)
            .setMessage("Saving consultation...")
            .setCancelable(false)
            .create()
        loading.show()

        ApiClient.api.createConsultation(token, request)
            .enqueue(object : Callback<ConsultationCreateResponse> {
                override fun onResponse(
                    call: Call<ConsultationCreateResponse>,
                    response: Response<ConsultationCreateResponse>
                ) {
                    loading.dismiss()
                    if (response.isSuccessful) {
                        val body = response.body()
                        AlertDialog.Builder(this@QuickPrescriptionActivity)
                            .setTitle("Success")
                            .setMessage("Consultation completed\nID: ${body?.consultation_id}\nDate: ${body?.consultation_date}")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        // Try to show server error body if available
                        val err = response.errorBody()?.string()
                        AlertDialog.Builder(this@QuickPrescriptionActivity)
                            .setTitle("Server error")
                            .setMessage("Status: ${response.code()}\n${err ?: response.message()}")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ConsultationCreateResponse>, t: Throwable) {
                    loading.dismiss()
                    AlertDialog.Builder(this@QuickPrescriptionActivity)
                        .setTitle("Network error")
                        .setMessage(t.localizedMessage ?: "Unknown error")
                        .setPositiveButton("OK", null)
                        .show()
                }
            })

        // Also write a local JSON copy as before (optional)
        try {
            val localFile = File(filesDir, "prescription_${System.currentTimeMillis()}.json")
            localFile.writeText(gson.toJson(request))
        } catch (e: Exception) {
            // ignore file write errors
        }
    }

}
