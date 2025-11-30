package com.example.ashajoyti_doctor_app.doctorapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

            // Build request object (ensure your LoginRequest uses 'password')
            val request = LoginRequest(
                doc_id = username,   // using doc_id; change if backend expects different key
                doc_phone = null,
                password = password
            )

            ApiClient.api.loginDoctor(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnLogin.isEnabled = true
                    btnLogin.alpha = 1f

                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null && !data.token.isNullOrBlank()) {
                            // Save token (and optionally doctor info)
                            AuthPref.saveToken(this@CHOLoginActivity, "Bearer ${data.token}")
                            // Save username/display name if you want for later
                            data.doctor?.let { doc ->
                                AuthPref.saveDoctorName(this@CHOLoginActivity, doc.doc_name ?: username)
                            }

                            Toast.makeText(this@CHOLoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                            // Navigate to Dashboard
                            val intent = Intent(this@CHOLoginActivity, CHODashboardActivity::class.java)
                            intent.putExtra("extra_username", data.doctor?.doc_name ?: username)
                            startActivity(intent)
                            finish()
                        } else {
                            // Successful HTTP but missing body/token
                            Toast.makeText(this@CHOLoginActivity, "Login failed: invalid server response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Try to provide a more descriptive message based on status code
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
                    Toast.makeText(this@CHOLoginActivity, "Network error, try again", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
