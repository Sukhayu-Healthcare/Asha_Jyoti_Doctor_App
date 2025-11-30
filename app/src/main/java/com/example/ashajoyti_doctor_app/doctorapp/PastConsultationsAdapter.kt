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
    val age: String,
    val gender: String,
    val symptoms: String,
    val visit: String,         // expected "YYYY-MM-DD HH:MM" or "DD MMM YYYY HH:MM" — adapter will try to split
    val wait: String,
    val vitals: String,
    val feedbackRating: Double,
    val feedbackText: String
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
        val tvDateBadge: TextView = view.findViewById(R.id.tvDateBadge)
        val tvTimeBadge: TextView = view.findViewById(R.id.tvTimeBadge)
        val tvSummary: TextView = view.findViewById(R.id.tvSummary)
        val tvViewDetails: TextView = view.findViewById(R.id.tvViewDetails)

        init {
            tvViewDetails.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onViewDetails(items[pos])
            }
            // click whole card too
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

        // Try to split visit into date/time. Be robust to formats.
        val visit = p.visit ?: ""
        var dateText = visit
        var timeText = ""
        // If visit contains space and looks like "YYYY-MM-DD HH:MM" or "DD MMM YYYY HH:MM"
        val parts = visit.trim().split(Regex("\\s+"))
        if (parts.size >= 2) {
            // last token likely time
            timeText = parts.last()
            dateText = parts.subList(0, parts.size - 1).joinToString(" ")
        }

        holder.tvDateBadge.text = dateText
        holder.tvTimeBadge.text = timeText

        // summary: symptoms + patientId (matches screenshot)
        holder.tvSummary.text = "${p.symptoms} · ${p.patientId}"

        // "View Details" is styled in XML (we made it bold); keep as is
        holder.tvViewDetails.text = "View Details"
    }

    override fun getItemCount(): Int = items.size
}
