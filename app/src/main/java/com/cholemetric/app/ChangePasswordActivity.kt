package com.cholemetric.app

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

        if (newPass != confirmPass) {
            Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val err = json.optString("error", "Incorrect current password.")
                        Toast.makeText(this@ChangePasswordActivity, err, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    btnExecute.isEnabled = true
                    btnExecute.text = "Change Password"
                    Toast.makeText(this@ChangePasswordActivity, "Network error updating password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
