package com.cholemetric.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

import java.util.Locale

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ApiConfig.initFromPrefs(this)

        val createAccountLayout = findViewById<LinearLayout>(R.id.create_account_layout)
        createAccountLayout.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPassword = findViewById<EditText>(R.id.et_login_password)
        val ivPasswordToggle = findViewById<ImageView>(R.id.iv_login_password_toggle)
        val btnSignIn = findViewById<Button>(R.id.btn_sign_in)
        
        setupPasswordToggle(etPassword, ivPasswordToggle)
        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)
        
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSignIn.isEnabled = false
            btnSignIn.text = "Signing In..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = OkHttpClient()
                    val json = JSONObject()
                    json.put("email", email)
                    json.put("password", password)

                    val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url(ApiConfig.BASE_URL + "login.php")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseString = response.body?.string()

                    withContext(Dispatchers.Main) {
                        btnSignIn.isEnabled = true
                        btnSignIn.text = "Sign In"

                        if (response.isSuccessful && responseString != null) {
                            val jsonObject = JSONObject(responseString)
                            if (jsonObject.getBoolean("success")) {
                                val doctor = jsonObject.getJSONObject("doctor")
                                val doctorName = doctor.getString("full_name")
                                val doctorEmail = doctor.getString("email")
                                val doctorId = doctor.getInt("id")

                                AuthManager.registerUser(this@LoginActivity, doctorName, "", doctorEmail, password)

                                val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
                                sharedPref.edit().apply {
                                    putInt("DOCTOR_ID", doctorId)
                                    putString("USER_NAME", doctorName)
                                    putString("DOCTOR_EMAIL", doctorEmail)
                                    apply()
                                }

                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                intent.putExtra("USER_NAME", doctorName)
                                intent.putExtra("DOCTOR_ID", doctorId)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                val serverError = jsonObject.optString("error", "")
                                handleLoginError(email, password, serverError)
                            }
                        } else if (response.code == 401) {
                            val jsonObject = try { JSONObject(responseString ?: "") } catch (e: Exception) { null }
                            val serverError = jsonObject?.optString("error", "") ?: ""
                            handleLoginError(email, password, serverError)
                        } else {
                            handleLoginError(email, password, null)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnSignIn.isEnabled = true
                        btnSignIn.text = "Sign In"
                        handleLoginError(email, password, null)
                    }
                }
            }
        }
    }

    private fun handleLoginError(email: String, password: String, serverError: String?) {
        if (!serverError.isNullOrBlank()) {
            if (serverError.contains("registered", ignoreCase = true) || serverError.contains("not found", ignoreCase = true) || serverError.contains("exist", ignoreCase = true)) {
                Toast.makeText(this, "Email is not registered", Toast.LENGTH_LONG).show()
                return
            } else if (serverError.contains("password", ignoreCase = true) || serverError.contains("incorrect", ignoreCase = true) || serverError.contains("invalid", ignoreCase = true)) {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG).show()
                return
            }
        }

        when (val result = AuthManager.authenticate(this, email, password)) {
            is AuthManager.AuthResult.EmailNotRegistered -> {
                Toast.makeText(this, "Email is not registered", Toast.LENGTH_LONG).show()
            }
            is AuthManager.AuthResult.WrongPassword -> {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG).show()
            }
            is AuthManager.AuthResult.Success -> {
                val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
                sharedPref.edit().apply {
                    putInt("DOCTOR_ID", result.doctorId)
                    putString("USER_NAME", result.fullName)
                    putString("DOCTOR_EMAIL", result.email)
                    apply()
                }

                Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USER_NAME", result.fullName)
                intent.putExtra("DOCTOR_ID", result.doctorId)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupPasswordToggle(editText: EditText, toggleIcon: ImageView) {
        toggleIcon.setOnClickListener {
            if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            editText.setSelection(editText.text.length)
        }
    }
}
