package com.example.ashajoyti_doctor_app.doctorapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R

/**
 * Simple CHO profile activity.
 *
 * IMPORTANT: This uses the layout file named "activity_cho_profile.xml".
 * If your layout file has a different name, replace R.layout.activity_cho_profile accordingly.
 */
class CHOProfileActivity : AppCompatActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDesignation: TextView
    private lateinit var tvId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // <--- Make sure the layout filename matches this id:
        setContentView(R.layout.activity_cho_profile)

        // Try to find sensible views — these are optional, safe-find with try/catch isn't necessary
        // but will not crash if you keep the ids consistent with your XML.
        try {
            ivAvatar = findViewById(R.id.headerAvatar)
            tvName = findViewById(R.id.tvName)
            tvDesignation = findViewById(R.id.tvDesignation)
            tvId = findViewById(R.id.tvId)

            // populate sample data (replace with real model/data)
            tvName.text = "Dr. Amit Kumar"
            tvDesignation.text = "Chief Health Officer"
            tvId.text = "CHO001"

            ivAvatar.setOnClickListener {
                Toast.makeText(this, "Avatar clicked (stub)", Toast.LENGTH_SHORT).show()
            }
        } catch (t: Throwable) {
            // If any view id isn't present, avoid crashing: show a toast and continue.
            // This helps during iterative layout renames.
            Toast.makeText(this, "Profile UI partially missing. Check layout ids.", Toast.LENGTH_SHORT).show()
        }
    }
}
