package com.cholemetric.app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

class SettingsActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var doctorId: Int = -1
    private var doctorEmail: String = ""
    
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvSpecialization: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Read session
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        doctorId = sharedPref.getInt("DOCTOR_ID", -1)
        doctorEmail = sharedPref.getString("DOCTOR_EMAIL", "") ?: ""

        if (doctorId == -1) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        // Initialize views
        tvName = findViewById(R.id.tv_settings_name)
        tvEmail = findViewById(R.id.tv_settings_email)
        tvSpecialization = findViewById(R.id.tv_settings_specialization)

        val llBack = findViewById<LinearLayout>(R.id.ll_settings_back)
        val llProfile = findViewById<LinearLayout>(R.id.ll_settings_profile)
        val llFaq = findViewById<LinearLayout>(R.id.ll_settings_faq)
        val llContact = findViewById<LinearLayout>(R.id.ll_settings_contact)
        val llLegal = findViewById<LinearLayout>(R.id.ll_settings_legal)
        
        val btnDelete = findViewById<Button>(R.id.btn_settings_delete)
        val btnLogout = findViewById<Button>(R.id.btn_settings_logout)

        // Bind simple clicks
        llBack.setOnClickListener {
            finish()
        }

        llProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        llFaq.setOnClickListener {
            val intent = Intent(this, HelpFaqActivity::class.java)
            startActivity(intent)
        }

        llContact.setOnClickListener {
            contactSupport()
        }

        llLegal.setOnClickListener {
            Toast.makeText(this, "Opening Privacy Policy & Terms of Service...", Toast.LENGTH_SHORT).show()
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://172.23.19.66:8080/backend/gb_stone_api/legal.php"))
                startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnLogout.setOnClickListener {
            confirmLogout()
        }

        btnDelete.setOnClickListener {
            confirmDeleteAccount()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh doctor profile details on return
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
                    val spec = doctor.optString("specialization", "Radiologist")
                    val cleanSpec = if (spec.isEmpty()) "Radiologist" else spec

                    doctorEmail = email // keep synced

                    withContext(Dispatchers.Main) {
                        tvName.text = name
                        tvEmail.text = email
                        tvSpecialization.text = cleanSpec
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@choliscan.com"))
            putExtra(Intent.EXTRA_SUBJECT, "CholeMetric App Support Request")
        }
        try {
            startActivity(Intent.createChooser(intent, "Send Email..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out of CholeMetric?")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun confirmDeleteAccount() {
        // Show dialog asking for credentials confirmation (password verification)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_delete_confirm_password)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("To confirm deletion, please enter your password. This action will permanently remove your account and all associated patient records.")
            .setView(dialogView)
            .setPositiveButton("Permanently Delete", null) // Overridden below to prevent auto-closing
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Customize button to handle validation
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val password = etPassword.text.toString()
            if (password.isEmpty()) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }

            dialog.dismiss()
            executeDeleteAccount(password)
        }
    }

    private fun executeDeleteAccount(password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("email", doctorEmail)
                    put("password", password)
                }
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                
                val request = Request.Builder()
                    .url("http://172.23.19.66:8080/backend/gb_stone_api/delete.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val json = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && json.getBoolean("success")) {
                        Toast.makeText(this@SettingsActivity, "Account deleted successfully", Toast.LENGTH_LONG).show()
                        logout() // clears preferences and routes back to login
                    } else {
                        val err = json.optString("error", "Failed to delete account")
                        Toast.makeText(this@SettingsActivity, err, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "Network error deleting account", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
