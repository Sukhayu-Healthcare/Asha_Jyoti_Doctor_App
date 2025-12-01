package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ashajoyti_doctor_app.R
import com.example.ashajoyti_doctor_app.model.LoginRequest
import com.example.ashajoyti_doctor_app.model.LoginResponse
import com.example.ashajoyti_doctor_app.network.ApiClient
import com.example.ashajoyti_doctor_app.utils.AuthPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CHOLoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cho_login)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // read role passed from RoleSelectionActivity
        val selectedRole = intent.getStringExtra("role") ?: "CHO"

        // Optional: update UI hint to show role (makes UX clearer)
        edtUsername.hint = when (selectedRole) {
            "MO" -> "MO Login ID (e.g. MO001)"
            "CIVIL" -> "Hospital Doctor ID (e.g. CD001)"
            "EMERGENCY" -> "Emergency Doctor ID"
            else -> "CHO001"
        }

        btnLogin.setOnClickListener {
            // Hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent double taps
            btnLogin.isEnabled = false
            btnLogin.alpha = 0.6f

            val request = LoginRequest(
                doc_id = username,
                doc_phone = null,
                password = password
            )

            ApiClient.api.loginDoctor(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnLogin.isEnabled = true
                    btnLogin.alpha = 1f

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!

                        // Save token and doctor info
                        AuthPref.saveToken(this@CHOLoginActivity, "Bearer ${data.token}")
                        AuthPref.saveDoctorName(this@CHOLoginActivity, data.doctor.doc_name)
                        try { AuthPref.saveDoctorId(this@CHOLoginActivity, data.doctor.doc_id) } catch (_: Exception) {}
                        AuthPref.saveDoctorSpeciality(this@CHOLoginActivity, data.doctor.doc_speciality)

                        // SAVE the selected role too
                        AuthPref.saveRole(this@CHOLoginActivity, data.doctor.doc_role)

                        Toast.makeText(this@CHOLoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Navigate to Dashboard
                        val intent = Intent(this@CHOLoginActivity, CHODashboardActivity::class.java)
                        intent.putExtra("extra_username", data.doctor.doc_name)
                        startActivity(intent)
                        finish()
                    } else {
                        // error handling
                        if (response.code() == 401) {
                            Toast.makeText(this@CHOLoginActivity, "Invalid ID or Password", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@CHOLoginActivity, "Login failed (${response.code()})", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    btnLogin.isEnabled = true
                    btnLogin.alpha = 1f
                    Log.e("LOGIN_FAIL", "Login request failed: ${t::class.java.simpleName} - ${t.message}", t)
                    Toast.makeText(this@CHOLoginActivity, "Network error, try again", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
