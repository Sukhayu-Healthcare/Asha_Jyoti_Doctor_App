package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import android.widget.Button
import android.widget.Toast
import android.content.Intent

class ConsultationDetailActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val tabTitles = listOf("Vitals", "Examination", "Prescription")
    private val fragments: List<Fragment> by lazy {
        listOf(VitalsFragment(), ExaminationFragment(), PrescriptionFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_detail)

        // toolbar (optional)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarConsultation)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // find tab/viewpager
        tabLayout = findViewById(R.id.tabLayoutVitals)
        viewPager = findViewById(R.id.viewPagerVitals)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // optional: wire communication card buttons (if present)
        findViewById<Button?>(R.id.btnCommVideo)?.setOnClickListener {
            Toast.makeText(this, "Start Video (placeholder)", Toast.LENGTH_SHORT).show()
            // start VideoCallActivity if required:
            try {
                val i = Intent(this, VideoCallActivity::class.java)
                startActivity(i)
            } catch (_: Throwable) { /* ignore if activity not present */ }
        }
        findViewById<Button?>(R.id.btnCommVoice)?.setOnClickListener {
            Toast.makeText(this, "Start Voice (placeholder)", Toast.LENGTH_SHORT).show()
            try {
                val i = Intent(this, VoiceCallActivity::class.java)
                startActivity(i)
            } catch (_: Throwable) { /* ignore */ }
        }
        findViewById<Button?>(R.id.btnCommChat)?.setOnClickListener {
            Toast.makeText(this, "Open Chat (placeholder)", Toast.LENGTH_SHORT).show()
            try {
                val i = Intent(this, ChatFallbackActivity::class.java)
                startActivity(i)
            } catch (_: Throwable) { /* ignore */ }
        }
    }
}
