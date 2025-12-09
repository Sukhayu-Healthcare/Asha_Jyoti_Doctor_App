package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.model.DoctorQueriesResponse
import com.example.ashajoyti_doctor_app.model.QueryModel
import com.example.ashajoyti_doctor_app.utils.AuthPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QueriesActivity : AppCompatActivity(), QueryAdapter.OnQueryActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var btnBack: Button
    private var docId: Int = 0
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queries)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewQueries)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)
        btnBack = findViewById(R.id.btnBack)

        // Get doctor info from AuthPref
        docId = AuthPref.getDoctorId(this)
        token = AuthPref.getToken(this) ?: ""

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener { finish() }

        // Load static sample data
        loadStaticQueries()
    }

    private fun loadStaticQueries() {
        progressBar.visibility = android.view.View.GONE
        emptyView.visibility = android.view.View.GONE

        // Create static sample queries data
        val staticQueries = listOf(
            QueryModel(
                query_id = 1,
                patient_id = 2,
                asha_id = 1,
                text = "I have been experiencing severe headaches for the past 3 days. The pain is located at the back of my head and is accompanied by dizziness.",
                voice_url = null,
                disease = "Yellow viral fever",
                doc = null,
                doc_id = 1,
                query_status = "Pending",
                done_or_not = false,
                patient_name = "Shlok Dubey",
                name = "Shlok Dubey",
                patient_phone = "9699202706",
                phone = "9699202706",
                patient_dob = "12/12/2005",
                dob = "12/12/2005",
                patient_gender = "Male",
                gender = "Male",
                zone = "Yellow viral fever"
            ),
            QueryModel(
                query_id = 2,
                patient_id = 3,
                asha_id = 1,
                text = "High fever with cough for 5 days. Need medical advice.",
                voice_url = null,
                disease = "Influenza",
                doc = null,
                doc_id = 1,
                query_status = "Pending",
                done_or_not = false,
                patient_name = "Priya Singh",
                name = "Priya Singh",
                patient_phone = "9876543210",
                phone = "9876543210",
                patient_dob = "05/15/2000",
                dob = "05/15/2000",
                patient_gender = "Female",
                gender = "Female",
                zone = "Red zone"
            ),
            QueryModel(
                query_id = 3,
                patient_id = 4,
                asha_id = 1,
                text = "Stomach pain and nausea. Please advise on medication.",
                voice_url = null,
                disease = "Gastroenteritis",
                doc = null,
                doc_id = 1,
                query_status = "Pending",
                done_or_not = false,
                patient_name = "Rajesh Kumar",
                name = "Rajesh Kumar",
                patient_phone = "8899776655",
                phone = "8899776655",
                patient_dob = "08/22/1995",
                dob = "08/22/1995",
                patient_gender = "Male",
                gender = "Male",
                zone = "Green zone"
            )
        )

        if (staticQueries.isEmpty()) {
            emptyView.visibility = android.view.View.VISIBLE
            emptyView.text = "No queries available"
        } else {
            emptyView.visibility = android.view.View.GONE

            // Create maps for patient names and phones
            val patientNameMap = mutableMapOf<Int, String>()
            val patientPhoneMap = mutableMapOf<Int, String>()

            staticQueries.forEach { query ->
                patientNameMap[query.patient_id] = query.patient_name ?: "Patient ${query.patient_id}"
                patientPhoneMap[query.patient_id] = query.patient_phone ?: "N/A"
            }

            val adapter = QueryAdapter(staticQueries, patientNameMap, patientPhoneMap, this@QueriesActivity)
            recyclerView.adapter = adapter
        }
    }

    override fun onOpenQuery(query: QueryModel) {
        // Navigate to query detail with all patient and query data
        val intent = Intent(this, QueryDetailActivity::class.java)
        intent.putExtra("query_id", query.query_id)
        intent.putExtra("patient_id", query.patient_id)
        intent.putExtra("text", query.text)
        intent.putExtra("voice_url", query.voice_url)
        intent.putExtra("disease", query.disease)
        intent.putExtra("doc", query.doc)
        intent.putExtra("query_status", query.query_status)
        intent.putExtra("done_or_not", query.done_or_not)
        // Pass patient details
        intent.putExtra("patient_name", query.patient_name ?: query.name ?: "N/A")
        intent.putExtra("patient_phone", query.patient_phone ?: query.phone ?: "N/A")
        intent.putExtra("patient_dob", query.patient_dob ?: query.dob ?: "N/A")
        intent.putExtra("patient_gender", query.patient_gender ?: query.gender ?: "N/A")
        intent.putExtra("zone", query.zone ?: "N/A")
        startActivity(intent)
    }

    override fun onPlayVoice(query: QueryModel) {
        Toast.makeText(this, "Playing voice: ${query.voice_url}", Toast.LENGTH_SHORT).show()
        // TODO: Implement audio player for voice_url
    }
}
