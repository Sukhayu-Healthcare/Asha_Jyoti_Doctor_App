package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ashajoyti_doctor_app.R

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Force light mode for the whole app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Apply theme before super.onCreate (optional but keeps things consistent)
        try {
            setTheme(R.style.Theme_ASHA_JYOTI_DOCTOR_APP)
        } catch (t: Throwable) {
            Log.w(TAG, "Theme R.style.Theme_ASHA_JYOTI_DOCTOR_APP not found: ${t.message}")
        }

        super.onCreate(savedInstanceState)

        // Try to start your RoleSelectionActivity (FQCN must match actual file)
        val candidates = listOf(
            "com.example.ashajoyti_doctor_app.doctorapp.RoleSelectionActivity",
            "com.example.ashajoyti_doctor_app.RoleSelectionActivity"
        )

        var started = false
        for (fqcn in candidates) {
            try {
                val clazz = Class.forName(fqcn)
                startActivity(Intent(this, clazz))
                Log.i(TAG, "Started role activity: $fqcn")
                finish()
                started = true
                break
            } catch (cnf: ClassNotFoundException) {
                Log.d(TAG, "Not found: $fqcn")
            } catch (ex: Exception) {
                Log.e(TAG, "Error starting $fqcn: ${ex.message}", ex)
            }
        }

        if (!started) {
            Log.w(TAG, "RoleSelectionActivity not found. Loading local fallback layout.")
            Toast.makeText(this, "Loading main screen", Toast.LENGTH_SHORT).show()
            setContentView(R.layout.activity_main)
        }
    }
}
