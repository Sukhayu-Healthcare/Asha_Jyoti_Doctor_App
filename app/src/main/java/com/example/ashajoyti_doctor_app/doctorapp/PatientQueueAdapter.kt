package com.example.ashajoyti_doctor_app.doctorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class PatientQueueAdapter(
    private val items: List<Patient>,
    private val listener: OnPatientActionListener
) : RecyclerView.Adapter<PatientQueueAdapter.VH>() {

    interface OnPatientActionListener {
        fun onStartConsultation(patient: Patient)
        fun onTagEmergency(patient: Patient)
        fun onViewDetails(patient: Patient)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
        val ivInitial: CircleImageView = view.findViewById(R.id.ivInitial)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvSeverity: TextView = view.findViewById(R.id.tvSeverity)
        val tvPatientId: TextView = view.findViewById(R.id.tvPatientId)
        val tvMeta: TextView = view.findViewById(R.id.tvMeta)
        val tvSymptoms: TextView = view.findViewById(R.id.tvSymptoms)
        val tvWait: TextView = view.findViewById(R.id.tvWait)
        val btnStart: MaterialButton = view.findViewById(R.id.btnStart)
        val btnTag: MaterialButton = view.findViewById(R.id.btnTagEmergency)

        init {
            // whole item click -> view details
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) listener.onViewDetails(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_patient_queue, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvIndex.text = "#%02d".format(p.index)
        holder.tvName.text = p.name
        holder.tvSeverity.text = p.severity
        holder.tvPatientId.text = p.patientId
        holder.tvMeta.text = p.ageGender
        holder.tvSymptoms.text = p.symptoms
        holder.tvWait.text = "Est. Wait: ${p.estWait}"

        holder.ivInitial.setImageResource(R.drawable.ic_person_placeholder)

        holder.btnStart.setOnClickListener { listener.onStartConsultation(p) }
        holder.btnTag.setOnClickListener { listener.onTagEmergency(p) }
    }

    override fun getItemCount(): Int = items.size
}
