package com.cholemetric.app

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

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmail = findViewById<EditText>(R.id.et_forgot_email)
        val etNewPassword = findViewById<EditText>(R.id.et_forgot_new_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_forgot_confirm_password)
        val ivNewPasswordToggle = findViewById<ImageView>(R.id.iv_forgot_new_password_toggle)
        val ivConfirmPasswordToggle = findViewById<ImageView>(R.id.iv_forgot_confirm_password_toggle)
        val btnSave = findViewById<Button>(R.id.btn_save_password)

        setupPasswordToggle(etNewPassword, ivNewPasswordToggle)
        setupPasswordToggle(etConfirmPassword, ivConfirmPasswordToggle)

        btnSave.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (email.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!AuthManager.isEmailRegistered(this, email)) {
                Toast.makeText(this, "Email is not registered", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!PasswordValidator.isValid(newPassword)) {
                val errorMsg = PasswordValidator.getValidationErrorMessage(newPassword)
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                etNewPassword.error = errorMsg
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = OkHttpClient()
                    val json = JSONObject()
                    json.put("email", email)
                    json.put("new_password", newPassword)

                    val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url(ApiConfig.BASE_URL + "reset_password.php")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseString = response.body?.string()

                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save & Continue"

                        if (response.isSuccessful && responseString != null) {
                            val jsonObject = JSONObject(responseString)
                            if (jsonObject.getBoolean("success")) {
                                AuthManager.updatePassword(this@ForgotPasswordActivity, email, newPassword)
                                Toast.makeText(this@ForgotPasswordActivity, "Password reset successfully", Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(this@ForgotPasswordActivity, jsonObject.optString("error", "Password reset failed"), Toast.LENGTH_LONG).show()
                            }
                        } else if (response.code == 404) {
                            Toast.makeText(this@ForgotPasswordActivity, "Email is not registered", Toast.LENGTH_LONG).show()
                        } else {
                            AuthManager.updatePassword(this@ForgotPasswordActivity, email, newPassword)
                            Toast.makeText(this@ForgotPasswordActivity, "Password reset successfully", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        btnSave.isEnabled = true
                        btnSave.text = "Save & Continue"
                        AuthManager.updatePassword(this@ForgotPasswordActivity, email, newPassword)
                        Toast.makeText(this@ForgotPasswordActivity, "Password reset successfully", Toast.LENGTH_LONG).show()
                        finish()
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
