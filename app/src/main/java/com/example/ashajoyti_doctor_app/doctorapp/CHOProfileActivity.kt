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
import java.util.Locale

class CHOProfileActivity : AppCompatActivity() {

    private val TAG = "CHOProfileActivity"
    private val PREFS = "app_prefs"
    private val KEY_AVATAR_URI = "cho_avatar_uri"

    private lateinit var ivAvatar: CircleImageView
    private lateinit var btnChangePhoto: ImageButton

    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etDoctorId: TextView
    private lateinit var etDesignation: TextInputEditText
    private lateinit var etRole: TextInputEditText

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

            // removed edit profile button (we no longer support edit screen)
            btnLogout = findViewById(R.id.btnLogout)

            btnLogout.setOnClickListener {
                val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
                try {
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
                    val editor = prefs.edit()
                    editor.remove(KEY_AVATAR_URI)
                    editor.remove("user_token")
                    editor.apply()
                }

                AuthPref.clear(this)
                val intent = Intent(this, RoleSelectionActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Unexpected error in CHOProfileActivity.onCreate(): ${e.message}", e)
            Toast.makeText(this, "Something went wrong opening profile — returning.", Toast.LENGTH_LONG).show()
            try { finish() } catch (_: Throwable) {}
        }
    }

    override fun onResume() {
        super.onResume()

        val name = AuthPref.getDoctorName(this) ?: "Dr. (Unknown)"
        val phone = AuthPref.getDoctorPhone(this) ?: ""
        val avatarUriStr = AuthPref.getProfilePic(this)

        val savedRoleStr = AuthPref.getRole(this)
        val role = Role.fromName(savedRoleStr)
        val cfg = RoleConfigFactory.get(role)

        val savedId = AuthPref.getDoctorId(this)
        val docId = if (savedId > 0) String.format(Locale.getDefault(), "%s%03d", role.name, savedId) else "${role.name}001"
        val designation = AuthPref.getDoctorSpeciality(this) ?: cfg.designationLabel

        try {
            etFullName.setText(name)
            etPhone.setText(phone)
            etDoctorId.text = docId
            etDesignation.setText(designation ?: "")
            etRole.setText(cfg.designationLabel)
        } catch (t: Throwable) {
            Log.w(TAG, "Error setting profile fields: ${t.message}")
        }

        // update the shared header's role text (so header stays accurate)
        try {
            val headerRole = findViewById<TextView>(R.id.headerRoleShort)
            headerRole?.text = cfg.designationLabel
        } catch (_: Throwable) { /* ignore if include not present */ }

        avatarUriStr?.let { uriStr ->
            runCatching {
                ivAvatar.setImageURI(Uri.parse(uriStr))
            }.onFailure {
                Log.w(TAG, "Unable to set avatar from saved uri: ${it.message}")
            }
        }
    }
}
