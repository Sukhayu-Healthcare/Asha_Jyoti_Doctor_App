package com.example.ashajoyti_doctor_app.doctorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.QueryModel
import com.google.android.material.button.MaterialButton

class QueryAdapter(
    private val items: List<QueryModel>,
    private val patientNameMap: Map<Int, String>,
    private val patientPhoneMap: Map<Int, String>,
    private val listener: OnQueryActionListener
) : RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    interface OnQueryActionListener {
        fun onOpenQuery(query: QueryModel)
        fun onPlayVoice(query: QueryModel)
    }

    inner class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPatientName: TextView = itemView.findViewById(R.id.tvQueryPatientName)
        val tvPatientId: TextView = itemView.findViewById(R.id.tvQueryPatientId)
        val tvPhone: TextView = itemView.findViewById(R.id.tvQueryPhone)
        val tvProblemText: TextView = itemView.findViewById(R.id.tvQueryProblem)
        val btnPlayVoice: ImageButton = itemView.findViewById(R.id.btnPlayVoice)
        val btnOpenQuery: MaterialButton = itemView.findViewById(R.id.btnOpenQuery)

        fun bind(query: QueryModel) {
            val patientName = patientNameMap[query.patient_id] ?: "Unknown Patient"
            val patientPhone = patientPhoneMap[query.patient_id] ?: "N/A"

            tvPatientName.text = patientName
            tvPatientId.text = "ID: ${query.patient_id}"
            tvPhone.text = "Phone: $patientPhone"
            tvProblemText.text = query.text.take(100) + if (query.text.length > 100) "..." else ""

            // Show or hide play button based on voice_url availability
            if (!query.voice_url.isNullOrEmpty()) {
                btnPlayVoice.visibility = View.VISIBLE
                btnPlayVoice.setOnClickListener {
                    listener.onPlayVoice(query)
                }
            } else {
                btnPlayVoice.visibility = View.GONE
            }

            btnOpenQuery.setOnClickListener {
                listener.onOpenQuery(query)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_query_card, parent, false)
        return QueryViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
