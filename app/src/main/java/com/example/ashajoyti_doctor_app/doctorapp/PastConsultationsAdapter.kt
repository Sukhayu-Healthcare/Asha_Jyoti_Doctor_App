package com.example.ashajoyti_doctor_app.doctorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R

// Model matches what PastConsultationsActivity creates
data class PastConsultation(
    val name: String,
    val patientId: String,
    val age: String = "N/A",
    val gender: String,
    val phone: String = "N/A",
    val symptoms: String,
    val visit: String,
    val wait: String,
    val vitals: String,
    val feedbackRating: Double,
    val feedbackText: String,
    val consultationId: String = "",
    val docId: String = "N/A",
    val notes: String = "N/A",
    val medicineName: String = "",
    val dosage: String = "",
    val prescriptionNotes: String = ""
)

/**
 * Adapter that accepts a lambda for item clicks: (PastConsultation) -> Unit
 * Uses adapterPosition (compatible widely), populates the item layout fields,
 * and extracts date/time for the two "pills" from the visit string gracefully.
 */
class PastConsultationsAdapter(
    private val items: List<PastConsultation>,
    private val onViewDetails: (PastConsultation) -> Unit
) : RecyclerView.Adapter<PastConsultationsAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPatientId: TextView = view.findViewById(R.id.tvPatientId)
        val tvAge: TextView = view.findViewById(R.id.tvAge)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val tvDateBadge: TextView = view.findViewById(R.id.tvDateBadge)
        val tvTimeBadge: TextView = view.findViewById(R.id.tvTimeBadge)
        val tvSummary: TextView = view.findViewById(R.id.tvSummary)

        init {
            // Click whole card to view details
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onViewDetails(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_past_consultation, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvName.text = p.name
        holder.tvPatientId.text = "ID: ${p.patientId}"
        holder.tvAge.text = "Age: ${p.age}"
        holder.tvPhone.text = "Phone: ${p.phone}"

        // Display date only (no time)
        holder.tvDateBadge.text = p.visit  // visit is already date-only format from activity
        holder.tvTimeBadge.text = ""        // Hide time badge

        // summary: show all backend data
        holder.tvSummary.text = """
            Consultation ID: ${p.consultationId}
            Doctor ID: ${p.docId}
            Gender: ${p.gender}
            Diagnosis: ${p.symptoms}
            Notes: ${p.notes}
        """.trimIndent()
    }

    override fun getItemCount(): Int = items.size
}
