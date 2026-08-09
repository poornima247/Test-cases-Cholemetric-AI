package com.cholemetric.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnBack = findViewById<LinearLayout>(R.id.btn_back)
        val signinLayout = findViewById<LinearLayout>(R.id.sign_in_layout)

        btnBack.setOnClickListener {
            finish()
        }

        signinLayout.setOnClickListener {
            finish()
        }

        val etName = findViewById<EditText>(R.id.et_signup_name)
        val etHospital = findViewById<EditText>(R.id.et_signup_hospital)
        val etEmail = findViewById<EditText>(R.id.et_signup_email)
        val etPassword = findViewById<EditText>(R.id.et_signup_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_signup_confirm_password)
        val ivPasswordToggle = findViewById<ImageView>(R.id.iv_signup_password_toggle)
        val ivConfirmPasswordToggle = findViewById<ImageView>(R.id.iv_signup_confirm_password_toggle)
        val btnCreateAccount = findViewById<Button>(R.id.btn_create_account)

        setupPasswordToggle(etPassword, ivPasswordToggle)
        setupPasswordToggle(etConfirmPassword, ivConfirmPasswordToggle)

        btnCreateAccount.setOnClickListener {
            val name = etName.text.toString().trim()
            val hospital = etHospital.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnCreateAccount.isEnabled = false
            btnCreateAccount.text = "Creating Account..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = OkHttpClient()
                    val json = JSONObject()
                    json.put("full_name", name)
                    json.put("hospital", hospital)
                    json.put("email", email)
                    json.put("password", password)

                    val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url(ApiConfig.BASE_URL + "register.php")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseString = response.body?.string()

                    withContext(Dispatchers.Main) {
                        btnCreateAccount.isEnabled = true
                        btnCreateAccount.text = "Create Account"

                        if (response.isSuccessful && responseString != null) {
                            val jsonObject = JSONObject(responseString)
                            if (jsonObject.getBoolean("success")) {
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@SignUpActivity, jsonObject.getString("error"), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this@SignUpActivity, "Server error: ${response.code} - ${responseString}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnCreateAccount.isEnabled = true
                        btnCreateAccount.text = "Create Account"
                        Toast.makeText(this@SignUpActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
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
