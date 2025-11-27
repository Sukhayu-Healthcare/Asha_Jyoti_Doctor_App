package com.example.ashajoyti_doctor_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Force app to always use light mode (ignores system dark mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Apply the app theme. Use R.style with underscores (dots in style name become underscores).
        // Your style in res/values/themes.xml is named "Theme.ASHA_JYOTI_DOCTOR_APP"
        // so the generated id is R.style.Theme_ASHA_JYOTI_DOCTOR_APP
        try {
            setTheme(R.style.Theme_ASHA_JYOTI_DOCTOR_APP)
        } catch (t: Throwable) {
            Log.w(TAG, "Theme R.style.Theme_ASHA_JYOTI_DOCTOR_APP not found: ${t.message}")
        }

        super.onCreate(savedInstanceState)

        // Try to start RoleSelectionActivity (multiple possible package names used historically)
        val candidates = listOf(
            "com.example.ashajoyti_doctor_app.doctorapp.RoleSelectionActivity",
            "com.example.ashajoyti_doctor_app.RoleSelectionActivity",
            "com.ashajyoti.doctorapp.RoleSelectionActivity",
            "com.ashajyoti_doctor_app.doctorapp.RoleSelectionActivity"
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

        // Fallback UI if role activity not found
        if (!started) {
            Log.w(TAG, "RoleSelectionActivity not found. Loading activity_main as fallback.")
            Toast.makeText(this, "Loading main screen", Toast.LENGTH_SHORT).show()
            setContentView(R.layout.activity_main)
        }
    }
}
