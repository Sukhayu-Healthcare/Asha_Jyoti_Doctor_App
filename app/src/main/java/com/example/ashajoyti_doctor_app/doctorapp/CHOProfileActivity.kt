package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView

class CHOProfileActivity : AppCompatActivity() {

    private val TAG = "CHOProfileActivity"

    private lateinit var ivAvatar: CircleImageView
    private lateinit var btnChangePhoto: ImageButton

    // keep text inputs that are TextInputEditText in layout
    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    // etDoctorId is a regular TextView in your layout (not a TextInputEditText)
    private lateinit var etDoctorId: TextView
    private lateinit var etDesignation: TextInputEditText
    private lateinit var etRole: TextInputEditText

    private lateinit var btnEditProfile: MaterialButton
    private lateinit var btnLogout: MaterialButton

    // Activity result launcher for picking image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                ivAvatar.setImageURI(it)
                // persist URI to preferences for reloading later:
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                prefs.edit().putString("cho_avatar_uri", it.toString()).apply()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to set avatar: ${t.message}")
                Toast.makeText(this, "Couldn't load selected image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cho_profile)

        // bind views (types now match the layout)
        ivAvatar = findViewById(R.id.ivAvatar)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)

        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etDoctorId = findViewById(R.id.etDoctorId) // TEXTVIEW in layout
        etDesignation = findViewById(R.id.etDesignation)
        etRole = findViewById(R.id.etRole)

        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnLogout = findViewById(R.id.btnLogout)

        // load persisted avatar (if any) safely
        try {
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.getString("cho_avatar_uri", null)?.let { uriString ->
                runCatching {
                    ivAvatar.setImageURI(Uri.parse(uriString))
                }.onFailure {
                    Log.w(TAG, "Couldn't load saved avatar URI: ${it.message}")
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Error loading avatar from prefs: ${t.message}")
        }

        // sample / real data: populate these from your user/session model as needed
        etFullName.setText("Dr. Amit Kumar")
        etPhone.setText("+91-9876543213")
        etDoctorId.text = "CHO001"                  // TextView: set text this way
        etDesignation.setText("Chief Health Officer")
        etRole.setText("CHO")

        // change photo click -> open gallery
        btnChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // edit profile (placeholder) -> open edit activity when ready
        btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Open Edit Profile (todo)", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // logout -> go to RoleSelectionActivity and clear back stack
        btnLogout.setOnClickListener {
            // perform any sign-out logic here (clear tokens, prefs, etc.)
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().remove("user_token").apply()

            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        Log.i(TAG, "CHOProfileActivity initialized")
    }
}
