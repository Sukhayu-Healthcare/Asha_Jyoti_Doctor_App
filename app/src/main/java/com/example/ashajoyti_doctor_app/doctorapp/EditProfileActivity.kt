package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private val TAG = "EditProfileActivity"
    private val PREFS = "app_prefs"
    private val KEY_AVATAR_URI = "cho_avatar_uri"
    private val KEY_NAME = "cho_name"
    private val KEY_PHONE = "cho_phone"

    private lateinit var ivAvatarEdit: CircleImageView
    private lateinit var btnChangePhotoEdit: ImageButton
    private lateinit var etFullNameEdit: TextInputEditText
    private lateinit var etPhoneEdit: TextInputEditText
    private lateinit var tvDoctorIdRO: TextView
    private lateinit var tvDesignationRO: TextView
    private lateinit var tvRoleRO: TextView
    private lateinit var btnCancelEdit: MaterialButton
    private lateinit var btnSaveEdit: MaterialButton
    private lateinit var btnLogoutEdit: MaterialButton

    // OpenDocument for persistable permission
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            ivAvatarEdit.setImageURI(uri)
            // Save temporary in prefs (will be saved alongside other values on Save)
            val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
            prefs.edit().putString(KEY_AVATAR_URI, uri.toString()).apply()
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to persist chosen image: ${t.message}", t)
            Toast.makeText(this, "Unable to use selected image.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ivAvatarEdit = findViewById(R.id.ivAvatarEdit)
        btnChangePhotoEdit = findViewById(R.id.btnChangePhotoEdit)
        etFullNameEdit = findViewById(R.id.etFullNameEdit)
        etPhoneEdit = findViewById(R.id.etPhoneEdit)
        tvDoctorIdRO = findViewById(R.id.tvDoctorIdRO)
        tvDesignationRO = findViewById(R.id.tvDesignationRO)
        tvRoleRO = findViewById(R.id.tvRoleRO)
        btnCancelEdit = findViewById(R.id.btnCancelEdit)
        btnSaveEdit = findViewById(R.id.btnSaveEdit)
        btnLogoutEdit = findViewById(R.id.btnLogoutEdit)

        // Load existing values from prefs
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val name = prefs.getString(KEY_NAME, "Dr. Amit Kumar")!!
        val phone = prefs.getString(KEY_PHONE, "+91-9876543213")!!
        val avatarUriStr = prefs.getString(KEY_AVATAR_URI, null)
        val docId = prefs.getString("cho_id", "CHO001") ?: "CHO001"
        val designation = prefs.getString("cho_designation", "Chief Health Officer") ?: "Chief Health Officer"
        val role = prefs.getString("cho_role", "CHO") ?: "CHO"

        etFullNameEdit.setText(name)
        etPhoneEdit.setText(phone)
        tvDoctorIdRO.text = docId
        tvDesignationRO.text = designation
        tvRoleRO.text = role

        // load avatar if exists
        avatarUriStr?.let {
            runCatching {
                ivAvatarEdit.setImageURI(Uri.parse(it))
            }.onFailure {
                Log.w(TAG, "Can't load saved avatar in edit: ${it.message}")
            }
        }

        btnChangePhotoEdit.setOnClickListener {
            // pick image and persist permission
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        btnCancelEdit.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        btnSaveEdit.setOnClickListener {
            // validate basic entries (you can expand validation)
            val newName = etFullNameEdit.text?.toString()?.trim().orEmpty()
            val newPhone = etPhoneEdit.text?.toString()?.trim().orEmpty()
            if (newName.isEmpty()) {
                etFullNameEdit.error = "Name required"
                return@setOnClickListener
            }
            if (newPhone.isEmpty()) {
                etPhoneEdit.error = "Phone required"
                return@setOnClickListener
            }

            // save to prefs
            prefs.edit()
                .putString(KEY_NAME, newName)
                .putString(KEY_PHONE, newPhone)
                .apply()

            // Return to profile; profile will refresh values in onResume
            setResult(RESULT_OK)
            finish()
        }

        btnLogoutEdit.setOnClickListener {
            // Release avatar and clear auth then go to role selection
            try {
                prefs.getString(KEY_AVATAR_URI, null)?.let { saved ->
                    runCatching {
                        contentResolver.releasePersistableUriPermission(Uri.parse(saved), Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            } catch (t: Throwable) {
                Log.w(TAG, "Error releasing avatar permission: ${t.message}")
            }
            prefs.edit()
                .remove(KEY_AVATAR_URI)
                .remove(KEY_NAME)
                .remove(KEY_PHONE)
                .remove("user_token")
                .apply()

            val intent = Intent(this, RoleSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
