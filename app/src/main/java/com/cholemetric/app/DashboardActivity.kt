package com.cholemetric.app

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private var doctorId: Int = -1
    private var tvTotalScans: TextView? = null
    private var tvPositiveScans: TextView? = null
    private var tvNegativeScans: TextView? = null
    private var tvDetectionRate: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvName = findViewById<TextView>(R.id.tv_dashboard_name)
        val tvDate = findViewById<TextView>(R.id.tv_dashboard_date)
        
        tvTotalScans = findViewById(R.id.tv_total_scans)
        tvPositiveScans = findViewById(R.id.tv_positive_scans)
        tvNegativeScans = findViewById(R.id.tv_negative_scans)
        tvDetectionRate = findViewById(R.id.tv_detection_rate)

        val userName = intent.getStringExtra("USER_NAME") ?: "Doctor"
        doctorId = intent.getIntExtra("DOCTOR_ID", -1)
        
        tvName?.text = userName

        val currentDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())
        tvDate?.text = currentDate

        val llNewAnalysis = findViewById<android.widget.LinearLayout>(R.id.ll_new_analysis)
        llNewAnalysis.setOnClickListener {
            val intent = android.content.Intent(this, NewAnalysisActivity::class.java)
            startActivity(intent)
        }

        val llPatientHistory = findViewById<android.widget.LinearLayout>(R.id.ll_patient_history)
        llPatientHistory.setOnClickListener {
            val intent = android.content.Intent(this, PatientScansActivity::class.java)
            startActivity(intent)
        }

        val llDashboardSettings = findViewById<android.widget.LinearLayout>(R.id.ll_dashboard_settings)
        llDashboardSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("USER_NAME", "Doctor")
        val doctorEmail = sharedPref.getString("DOCTOR_EMAIL", "poornimadandu246@gmail.com") ?: "poornimadandu246@gmail.com"
        val tvName = findViewById<TextView>(R.id.tv_dashboard_name)
        tvName?.text = userName

        fetchDashboardStats(doctorId, doctorEmail, tvTotalScans, tvPositiveScans, tvNegativeScans, tvDetectionRate)
    }
    
    private fun fetchDashboardStats(
        doctorId: Int,
        doctorEmail: String,
        tvTotalScans: TextView?,
        tvPositiveScans: TextView?,
        tvNegativeScans: TextView?,
        tvDetectionRate: TextView?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(ApiConfig.BASE_URL + "dashboard_stats.php?doctor_id=$doctorId")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseString != null) {
                        val jsonObject = JSONObject(responseString)
                        if (jsonObject.optBoolean("success", false)) {
                            tvTotalScans?.text = jsonObject.getInt("total_scans").toString()
                            tvPositiveScans?.text = jsonObject.getInt("positive_scans").toString()
                            tvNegativeScans?.text = jsonObject.getInt("negative_scans").toString()
                            tvDetectionRate?.text = "${jsonObject.getDouble("detection_rate")}%"
                            return@withContext
                        }
                    }
                    displayAccountStats(doctorEmail)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    displayAccountStats(doctorEmail)
                }
            }
        }
    }

    private fun displayAccountStats(email: String) {
        val (total, positive, negative) = AccountScanManager.getStats(this, email)
        val rate = if (total > 0) String.format(Locale.US, "%.1f%%", (positive.toDouble() / total) * 100.0) else "0.0%"
        
        tvTotalScans?.text = total.toString()
        tvPositiveScans?.text = positive.toString()
        tvNegativeScans?.text = negative.toString()
        tvDetectionRate?.text = rate
    }
}
