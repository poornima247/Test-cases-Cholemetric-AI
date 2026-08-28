package com.cholemetric.app

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var doctorEmail: String = ""

    private lateinit var etCurrent: EditText
    private lateinit var etNew: EditText
    private lateinit var etConfirm: EditText
    private lateinit var btnExecute: Button

    private var isCurrentVisible = false
    private var isNewVisible = false
    private var isConfirmVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        // Read session
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        doctorEmail = sharedPref.getString("DOCTOR_EMAIL", "") ?: ""

        if (doctorEmail.isEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        etCurrent = findViewById(R.id.et_change_password_current)
        etNew = findViewById(R.id.et_change_password_new)
        etConfirm = findViewById(R.id.et_change_password_confirm)

        val ivCurrentToggle = findViewById<ImageView>(R.id.iv_change_password_current_toggle)
        val ivNewToggle = findViewById<ImageView>(R.id.iv_change_password_new_toggle)
        val ivConfirmToggle = findViewById<ImageView>(R.id.iv_change_password_confirm_toggle)

        val llCancel = findViewById<LinearLayout>(R.id.ll_change_password_cancel)
        btnExecute = findViewById(R.id.btn_change_password_execute)

        val ivRuleMinLength = findViewById<ImageView>(R.id.iv_change_rule_min_length)
        val tvRuleMinLength = findViewById<TextView>(R.id.tv_change_rule_min_length)
        val ivRuleUppercase = findViewById<ImageView>(R.id.iv_change_rule_uppercase)
        val tvRuleUppercase = findViewById<TextView>(R.id.tv_change_rule_uppercase)
        val ivRuleSpecial = findViewById<ImageView>(R.id.iv_change_rule_special)
        val tvRuleSpecial = findViewById<TextView>(R.id.tv_change_rule_special)
        val tvPasswordStatus = findViewById<TextView>(R.id.tv_change_password_status)

        etNew.addTextChangedListener(object : android.text.TextWatcher {
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
                    tvPasswordStatus.text = "New Password requirements:"
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

        // Bind simple cancel navigation
        llCancel.setOnClickListener {
            finish()
        }

        // Bind eye visibility toggles
        ivCurrentToggle.setOnClickListener {
            isCurrentVisible = !isCurrentVisible
            togglePasswordVisibility(etCurrent, ivCurrentToggle, isCurrentVisible)
        }

        ivNewToggle.setOnClickListener {
            isNewVisible = !isNewVisible
            togglePasswordVisibility(etNew, ivNewToggle, isNewVisible)
        }

        ivConfirmToggle.setOnClickListener {
            isConfirmVisible = !isConfirmVisible
            togglePasswordVisibility(etConfirm, ivConfirmToggle, isConfirmVisible)
        }

        btnExecute.setOnClickListener {
            executePasswordChange()
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

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.alpha = 1.0f // full opacity
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.alpha = 0.5f // translucent opacity
        }
        // Move selection to the end of the text
        editText.setSelection(editText.text.length)
    }

    private fun executePasswordChange() {
        val currentPass = etCurrent.text.toString()
        val newPass = etNew.text.toString()
        val confirmPass = etConfirm.text.toString()

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "All password fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!PasswordValidator.isValid(newPass)) {
            val errorMsg = PasswordValidator.getValidationErrorMessage(newPass)
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            etNew.error = errorMsg
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        btnExecute.isEnabled = false
        btnExecute.text = "Changing..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("email", doctorEmail)
                    put("old_password", currentPass)
                    put("new_password", newPass)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(ApiConfig.BASE_URL + "change_password.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val json = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    btnExecute.isEnabled = true
                    btnExecute.text = "Change Password"

                    if (response.isSuccessful && json.getBoolean("success")) {
                        AuthManager.updatePassword(this@ChangePasswordActivity, doctorEmail, newPass)
                        Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // Check local password match if network error/offline
                        val authResult = AuthManager.authenticate(this@ChangePasswordActivity, doctorEmail, currentPass)
                        if (authResult is AuthManager.AuthResult.Success) {
                            AuthManager.updatePassword(this@ChangePasswordActivity, doctorEmail, newPass)
                            Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val err = json.optString("error", "Incorrect current password.")
                            Toast.makeText(this@ChangePasswordActivity, err, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    btnExecute.isEnabled = true
                    btnExecute.text = "Change Password"
                    val authResult = AuthManager.authenticate(this@ChangePasswordActivity, doctorEmail, currentPass)
                    if (authResult is AuthManager.AuthResult.Success) {
                        AuthManager.updatePassword(this@ChangePasswordActivity, doctorEmail, newPass)
                        Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, "Incorrect current password.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
