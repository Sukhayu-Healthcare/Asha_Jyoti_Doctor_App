package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.ashajoyti_doctor_app.config.RoleConfigFactory
import com.example.ashajoyti_doctor_app.model.Role
import com.example.ashajoyti_doctor_app.utils.AuthPref
import de.hdodenhof.circleimageview.CircleImageView


class CHOProfileActivity : AppCompatActivity() {

    private val TAG = "CHOProfileActivity"
    private val PREFS = "app_prefs"
    private val KEY_AVATAR_URI = "cho_avatar_uri"
    private val KEY_NAME = "cho_name"
    private val KEY_PHONE = "cho_phone"

    private lateinit var ivAvatar: CircleImageView
    private lateinit var btnChangePhoto: ImageButton

    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etDoctorId: TextView
    private lateinit var etDesignation: TextInputEditText
    private lateinit var etRole: TextInputEditText

    private lateinit var btnEditProfile: MaterialButton
    private lateinit var btnLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_cho_profile)

            ivAvatar = findViewById(R.id.ivAvatar)
            btnChangePhoto = findViewById(R.id.btnChangePhoto)

            etFullName = findViewById(R.id.etFullName)
            etPhone = findViewById(R.id.etPhone)
            etDoctorId = findViewById(R.id.etDoctorId)
            etDesignation = findViewById(R.id.etDesignation)
            etRole = findViewById(R.id.etRole)

            btnEditProfile = findViewById(R.id.btnEditProfile)
            btnLogout = findViewById(R.id.btnLogout)

            // When user taps Edit -> open dedicated EditProfileActivity
            btnEditProfile.setOnClickListener {
                try {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    startActivity(intent)
                } catch (t: Throwable) {
                    Log.e(TAG, "Failed to open EditProfileActivity: ${t.message}", t)
                    Toast.makeText(this, "Can't open edit screen.", Toast.LENGTH_SHORT).show()
                }
            }

            // Logout: safe behavior (only on explicit logout)
            btnLogout.setOnClickListener {
                val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
                try {
                    // release persisted avatar permission if present (non-fatal)
                    prefs.getString(KEY_AVATAR_URI, null)?.let { saved ->
                        runCatching {
                            contentResolver.releasePersistableUriPermission(Uri.parse(saved), Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            Log.i(TAG, "Released persisted permission for: $saved")
                        }.onFailure {
                            Log.w(TAG, "Failed to release persisted permission: ${it.message}")
                        }
                    }
                } catch (t: Throwable) {
                    Log.w(TAG, "Error while attempting to release avatar permission: ${t.message}")
                } finally {
                    // remove avatar key only; auth token removed because user chose to logout
                    val editor = prefs.edit()
                    editor.remove(KEY_AVATAR_URI)
                    editor.remove("user_token") // remove auth token on logout
                    editor.apply()
                }

                // Navigate to role selection and clear back stack
                val intent = Intent(this, RoleSelectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            // Don't set any static texts here; actual values will be loaded in onResume()
        } catch (e: Throwable) {
            Log.e(TAG, "Unexpected error in CHOProfileActivity.onCreate(): ${e.message}", e)
            Toast.makeText(this, "Something went wrong opening profile — returning.", Toast.LENGTH_LONG).show()
            try { finish() } catch (_: Throwable) {}
        }
    }

    override fun onResume() {
        super.onResume()

        // Load latest saved values from prefs so changes from EditProfileActivity appear immediately
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)

        val name = prefs.getString(KEY_NAME, "Dr. Amit Kumar") ?: "Dr. Amit Kumar"
        val phone = prefs.getString(KEY_PHONE, "+91-9876543213") ?: "+91-9876543213"
        val avatarUriStr = prefs.getString(KEY_AVATAR_URI, null)

        // Use saved role from AuthPref if available
        val savedRoleStr = AuthPref.getRole(this)
        val role = Role.fromName(savedRoleStr)
        val cfg = RoleConfigFactory.get(role)

        // docId and designation from prefs (fallbacks)
        val docId = prefs.getString("cho_id", "${role.name}001") ?: "${role.name}001"
        val designation = prefs.getString("cho_designation", cfg.designationLabel) ?: cfg.designationLabel

        try {
            etFullName.setText(name)
            etPhone.setText(phone)
            etDoctorId.text = docId
            etDesignation.setText(designation)
            etRole.setText(cfg.designationLabel)
        } catch (t: Throwable) {
            Log.w(TAG, "Error setting text fields: ${t.message}")
        }

        // set avatar safely (permission might be missing if user cleared it)
        avatarUriStr?.let {
            runCatching {
                ivAvatar.setImageURI(Uri.parse(it))
            }.onFailure {
                Log.w(TAG, "Unable to set avatar from saved uri: ${it.message}")
                // if permission missing, remove saved uri (non-critical)
                prefs.edit().remove(KEY_AVATAR_URI).apply()
            }
        }
    }
}
