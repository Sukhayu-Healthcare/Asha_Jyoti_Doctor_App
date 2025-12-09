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

        if (docId > 0 && token.isNotEmpty()) {
            fetchQueries()
        } else {
            Toast.makeText(this, "Doctor information not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchQueries() {
        progressBar.visibility = android.view.View.VISIBLE
        emptyView.visibility = android.view.View.GONE

        val apiService = ApiClient.api
        val call = apiService.getDoctorQueries("Bearer $token", docId)

        call.enqueue(object : Callback<DoctorQueriesResponse> {
            override fun onResponse(
                call: Call<DoctorQueriesResponse>,
                response: Response<DoctorQueriesResponse>
            ) {
                progressBar.visibility = android.view.View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val queries = response.body()!!.query

                    if (queries.isEmpty()) {
                        emptyView.visibility = android.view.View.VISIBLE
                        emptyView.text = "No queries assigned yet"
                    } else {
                        emptyView.visibility = android.view.View.GONE

                        // Create maps for patient names and phones
                        // In a real app, you'd fetch patient details separately or from the response
                        val patientNameMap = mutableMapOf<Int, String>()
                        val patientPhoneMap = mutableMapOf<Int, String>()

                        // For now, we'll use placeholder data
                        // You can enhance this by making individual patient detail calls
                        queries.forEach { query ->
                            patientNameMap[query.patient_id] = "Patient ${query.patient_id}"
                            patientPhoneMap[query.patient_id] = "N/A"
                        }

                        val adapter = QueryAdapter(queries, patientNameMap, patientPhoneMap, this@QueriesActivity)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@QueriesActivity, "Failed to fetch queries", Toast.LENGTH_SHORT).show()
                    emptyView.visibility = android.view.View.VISIBLE
                    emptyView.text = "Failed to load queries"
                }
            }

            override fun onFailure(call: Call<DoctorQueriesResponse>, t: Throwable) {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@QueriesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                emptyView.visibility = android.view.View.VISIBLE
                emptyView.text = "Error loading queries"
            }
        })
    }

    override fun onOpenQuery(query: QueryModel) {
        // Navigate to query detail with modal/pane
        val intent = Intent(this, QueryDetailActivity::class.java)
        intent.putExtra("query_id", query.query_id)
        intent.putExtra("patient_id", query.patient_id)
        intent.putExtra("text", query.text)
        intent.putExtra("voice_url", query.voice_url)
        intent.putExtra("disease", query.disease)
        intent.putExtra("doc", query.doc)
        intent.putExtra("query_status", query.query_status)
        intent.putExtra("done_or_not", query.done_or_not)
        startActivity(intent)
    }

    override fun onPlayVoice(query: QueryModel) {
        Toast.makeText(this, "Playing voice: ${query.voice_url}", Toast.LENGTH_SHORT).show()
        // TODO: Implement audio player for voice_url
    }
}
