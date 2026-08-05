package com.cholemetric.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class EditProfileActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var doctorId: Int = -1

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etHospital: EditText
    private lateinit var etSpecialization: EditText
    
    private lateinit var tvPreviewName: TextView
    private lateinit var tvPreviewSpec: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Read doctor session
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        doctorId = sharedPref.getInt("DOCTOR_ID", -1)

        if (doctorId == -1) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        etName = findViewById(R.id.et_edit_profile_name)
        etEmail = findViewById(R.id.et_edit_profile_email)
        etHospital = findViewById(R.id.et_edit_profile_hospital)
        etSpecialization = findViewById(R.id.et_edit_profile_specialization)
        
        tvPreviewName = findViewById(R.id.tv_edit_profile_preview_name)
        tvPreviewSpec = findViewById(R.id.tv_edit_profile_preview_specialization)

        val llBack = findViewById<LinearLayout>(R.id.ll_edit_profile_back)
        val llChangePassword = findViewById<LinearLayout>(R.id.ll_edit_profile_change_password)
        btnSave = findViewById(R.id.btn_edit_profile_save)

        // Bind clicks
        llBack.setOnClickListener {
            finish()
        }

        llChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        btnSave.setOnClickListener {
            saveChanges()
        }

        // Fetch current profile details
        fetchProfile()
    }

    private fun fetchProfile() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://172.23.19.66:8080/backend/gb_stone_api/get_profile.php?doctor_id=$doctorId")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val json = JSONObject(responseStr)

                if (response.isSuccessful && json.getBoolean("success")) {
                    val doctor = json.getJSONObject("doctor")
                    val name = doctor.getString("full_name")
                    val email = doctor.getString("email")
                    val hospital = doctor.optString("hospital", "")
                    val spec = doctor.optString("specialization", "Radiologist")

                    withContext(Dispatchers.Main) {
                        etName.setText(name)
                        etEmail.setText(email)
                        etHospital.setText(hospital)
                        etSpecialization.setText(spec)

                        tvPreviewName.text = name
                        tvPreviewSpec.text = if (spec.isEmpty()) "Radiologist" else spec
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveChanges() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val hospital = etHospital.text.toString().trim()
        val specialization = etSpecialization.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required.", Toast.LENGTH_SHORT).show()
            return
        }

        btnSave.isEnabled = false
        btnSave.text = "Saving..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("doctor_id", doctorId)
                    put("full_name", name)
                    put("email", email)
                    put("hospital", hospital)
                    put("specialization", specialization)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("http://172.23.19.66:8080/backend/gb_stone_api/update_profile.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val json = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    btnSave.isEnabled = true
                    btnSave.text = "Save Changes"

                    if (response.isSuccessful && json.getBoolean("success")) {
                        // Keep shared preferences synced with new values
                        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
                        sharedPref.edit().apply {
                            putString("USER_NAME", name)
                            putString("DOCTOR_EMAIL", email)
                            apply()
                        }

                        Toast.makeText(this@EditProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val err = json.optString("error", "Failed to update profile")
                        Toast.makeText(this@EditProfileActivity, err, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    btnSave.isEnabled = true
                    btnSave.text = "Save Changes"
                    Toast.makeText(this@EditProfileActivity, "Connection error updating profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
