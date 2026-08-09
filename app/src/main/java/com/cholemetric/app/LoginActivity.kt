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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
                                
                                // Save session to SharedPreferences
                                val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
                                sharedPref.edit().apply {
                                    putInt("DOCTOR_ID", doctor.getInt("id"))
                                    putString("USER_NAME", doctor.getString("full_name"))
                                    putString("DOCTOR_EMAIL", doctor.getString("email"))
                                    apply()
                                }

                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                intent.putExtra("USER_NAME", doctor.getString("full_name"))
                                intent.putExtra("DOCTOR_ID", doctor.getInt("id"))
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, jsonObject.getString("error"), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Server error: ${response.code} - ${responseString}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnSignIn.isEnabled = true
                        btnSignIn.text = "Sign In"
                        Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
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
