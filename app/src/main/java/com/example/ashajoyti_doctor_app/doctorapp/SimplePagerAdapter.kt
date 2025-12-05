package com.example.ashajoyti_doctor_app.doctorapp

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

// Simple adapter returning three fragments — keep class name exactly SimplePagerAdapter
class SimplePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> VitalsFragment()
        1 -> ExaminationFragment()
        else -> PrescriptionFragment()
    }
}
