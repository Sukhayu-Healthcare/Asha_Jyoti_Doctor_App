//package com.example.ashajoyti_doctor_app.doctorapp

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.ashajoyti_doctor_app.R

//data class Medicine(
  //  val name: String,
    //val dosage: String,
    //val frequency: String,
    //val duration: String
//)

//class MedicineAdapter(private val list: MutableList<Medicine> = mutableListOf()) :
  //  RecyclerView.Adapter<MedicineAdapter.VH>() {

    //inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
      //  val tvName: TextView = itemView.findViewById(R.id.tvMedicineName)
        //val tvDosage: TextView = itemView.findViewById(R.id.tvMedicineDosage)
       // val tvFreq: TextView = itemView.findViewById(R.id.tvMedicineFrequency)
      //  val tvDur: TextView = itemView.findViewById(R.id.tvMedicineDuration)
   // }

    //override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
      //  val v = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
        //return VH(v)
    //}
    //override fun getItemCount(): Int = list.size

    //override fun onBindViewHolder(holder: VH, position: Int) {
      //  val m = list[position]
      //  holder.tvName.text = m.name
      //  holder.tvDosage.text = m.dosage
      //  holder.tvFreq.text = m.frequency
      //  holder.tvDur.text = m.duration
    //}

    //fun add(m: Medicine) {
      //  list.add(m)
        //notifyItemInserted(list.size - 1)
   // }

    //fun clear() {
     //   list.clear()
       // notifyDataSetChanged()
    //}
//}
