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

        val ivRuleMinLength = findViewById<ImageView>(R.id.iv_signup_rule_min_length)
        val tvRuleMinLength = findViewById<TextView>(R.id.tv_signup_rule_min_length)
        val ivRuleUppercase = findViewById<ImageView>(R.id.iv_signup_rule_uppercase)
        val tvRuleUppercase = findViewById<TextView>(R.id.tv_signup_rule_uppercase)
        val ivRuleSpecial = findViewById<ImageView>(R.id.iv_signup_rule_special)
        val tvRuleSpecial = findViewById<TextView>(R.id.tv_signup_rule_special)
        val tvPasswordStatus = findViewById<TextView>(R.id.tv_signup_password_status)

        setupPasswordToggle(etPassword, ivPasswordToggle)
        setupPasswordToggle(etConfirmPassword, ivConfirmPasswordToggle)

        etPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pass = s.toString()
                val hasMin = PasswordValidator.hasMinLength(pass)
                val hasUpper = PasswordValidator.hasUppercase(pass)
                val hasSpec = PasswordValidator.hasSpecialChar(pass)

                updateRuleState(ivRuleMinLength, tvRuleMinLength, hasMin)
                updateRuleState(ivRuleUppercase, tvRuleUppercase, hasUpper)
                updateRuleState(ivRuleSpecial, tvRuleSpecial, hasSpec)

                if (pass.isEmpty()) {
                    tvPasswordStatus.text = "Password requirements:"
                    tvPasswordStatus.setTextColor(android.graphics.Color.parseColor("#555555"))
                } else if (hasMin && hasUpper && hasSpec) {
                    tvPasswordStatus.text = "Strong password"
                    tvPasswordStatus.setTextColor(android.graphics.Color.parseColor("#2ECC71"))
                } else {
                    tvPasswordStatus.text = "Weak password"
                    tvPasswordStatus.setTextColor(android.graphics.Color.parseColor("#E02424"))
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

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

            if (!PasswordValidator.isValid(password)) {
                val errorMsg = PasswordValidator.getValidationErrorMessage(password)
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                etPassword.error = errorMsg
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
                    val client = OkHttpClient.Builder()
                        .connectTimeout(2, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(2, java.util.concurrent.TimeUnit.SECONDS)
                        .writeTimeout(2, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
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
                                AuthManager.registerUser(this@SignUpActivity, name, hospital, email, password)
                                Toast.makeText(this@SignUpActivity, "Account created successfully! Please sign in.", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@SignUpActivity, jsonObject.optString("error", "Registration failed"), Toast.LENGTH_LONG).show()
                            }
                        } else {
                            performFallbackRegister(name, hospital, email, password)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnCreateAccount.isEnabled = true
                        btnCreateAccount.text = "Create Account"
                        performFallbackRegister(name, hospital, email, password)
                    }
                }
            }
        }
    }

    private fun performFallbackRegister(name: String, hospital: String, email: String, pass: String) {
        AuthManager.registerUser(this, name, hospital, email, pass)
        Toast.makeText(this, "Account created successfully! Please sign in.", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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

    private fun updateRuleState(imageView: ImageView, textView: TextView, isSatisfied: Boolean) {
        if (isSatisfied) {
            imageView.setImageResource(R.drawable.ic_check_circle_green)
            imageView.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2ECC71"))
            textView.setTextColor(android.graphics.Color.parseColor("#2ECC71"))
        } else {
            imageView.setImageResource(R.drawable.ic_circle_outline)
            imageView.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#888888"))
            textView.setTextColor(android.graphics.Color.parseColor("#888888"))
        }
    }
}
