package com.cholemetric.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class PatientScansActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var doctorId: Int = -1
    private lateinit var rvScans: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var scansList: MutableList<ScanItem>
    private lateinit var filteredList: MutableList<ScanItem>
    private lateinit var adapter: ScansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_scans)

        // Read doctor session
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        doctorId = sharedPref.getInt("DOCTOR_ID", -1)

        if (doctorId == -1) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        rvScans = findViewById(R.id.rv_scans)
        etSearch = findViewById(R.id.et_search_patient)
        val llBack = findViewById<LinearLayout>(R.id.ll_scans_back)
        val ivClearAll = findViewById<ImageView>(R.id.iv_clear_all_scans)

        rvScans.layoutManager = LinearLayoutManager(this)
        scansList = mutableListOf()
        filteredList = mutableListOf()
        adapter = ScansAdapter(filteredList)
        rvScans.adapter = adapter

        // Bind back button
        llBack.setOnClickListener {
            finish()
        }

        // Bind clear all button
        ivClearAll.setOnClickListener {
            confirmClearAll()
        }

        // Bind real-time search filtering
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterScans(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Fetch scans list
        fetchScans()
    }

    private fun fetchScans() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(ApiConfig.BASE_URL + "get_scans.php?doctor_id=$doctorId")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)

                if (response.isSuccessful && responseJson.getBoolean("success")) {
                    val scansArray = responseJson.getJSONArray("scans")
                    scansList.clear()

                    for (i in 0 until scansArray.length()) {
                        val obj = scansArray.getJSONObject(i)
                        scansList.add(
                            ScanItem(
                                id = obj.getInt("id"),
                                patientId = obj.getString("patient_id"),
                                patientName = obj.getString("patient_name"),
                                scanDate = obj.getString("scan_date"),
                                isPositive = obj.getInt("is_positive") == 1,
                                stoneCount = obj.getInt("stone_count"),
                                largestStoneMm = obj.getDouble("largest_stone_mm"),
                                aiConfidence = obj.getDouble("ai_confidence"),
                                notes = obj.optString("radiologist_text", ""),
                                annotatedImageUrl = obj.optString("annotated_image_url", ""),
                                originalImageUrl = obj.optString("original_image_url", ""),
                                patientAge = obj.optInt("patient_age", 0),
                                patientGender = obj.optString("patient_gender", "")
                            )
                        )
                    }

                    withContext(Dispatchers.Main) {
                        filterScans(etSearch.text.toString())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PatientScansActivity, "Failed to load scans", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientScansActivity, "Network error loading scans", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun filterScans(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(scansList)
        } else {
            val lowercaseQuery = query.lowercase(Locale.getDefault())
            for (item in scansList) {
                if (item.patientId.lowercase(Locale.getDefault()).contains(lowercaseQuery) ||
                    item.patientName.lowercase(Locale.getDefault()).contains(lowercaseQuery)) {
                    filteredList.add(item)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(item: ScanItem, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Scan Report")
            .setMessage("Are you sure you want to delete the report for Patient ${item.patientId}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteScan(item, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteScan(item: ScanItem, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("scan_id", item.id)
                }
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(ApiConfig.BASE_URL + "delete_scan.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseJson.getBoolean("success")) {
                        Toast.makeText(this@PatientScansActivity, "Scan deleted successfully", Toast.LENGTH_SHORT).show()
                        scansList.remove(item)
                        filteredList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    } else {
                        Toast.makeText(this@PatientScansActivity, "Failed to delete scan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientScansActivity, "Error deleting scan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmClearAll() {
        if (scansList.isEmpty()) {
            Toast.makeText(this, "No records to clear.", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Clear All History")
            .setMessage("Are you sure you want to delete ALL patient scan records? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                clearAllScans()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearAllScans() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("doctor_id", doctorId)
                }
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(ApiConfig.BASE_URL + "clear_all_scans.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseJson.getBoolean("success")) {
                        Toast.makeText(this@PatientScansActivity, "All history cleared!", Toast.LENGTH_SHORT).show()
                        scansList.clear()
                        filteredList.clear()
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@PatientScansActivity, "Failed to clear history", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PatientScansActivity, "Error clearing history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Data model for list records
    data class ScanItem(
        val id: Int,
        val patientId: String,
        val patientName: String,
        val scanDate: String,
        val isPositive: Boolean,
        val stoneCount: Int,
        val largestStoneMm: Double,
        val aiConfidence: Double,
        val notes: String,
        val annotatedImageUrl: String,
        val originalImageUrl: String,
        val patientAge: Int,
        val patientGender: String
    )

    // RecyclerView Adapter
    inner class ScansAdapter(private val list: List<ScanItem>) : RecyclerView.Adapter<ScansAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvPatientId: TextView = view.findViewById(R.id.tv_item_patient_id)
            val tvPatientName: TextView = view.findViewById(R.id.tv_item_patient_name)
            val tvBadge: TextView = view.findViewById(R.id.tv_item_badge)
            val tvScanDate: TextView = view.findViewById(R.id.tv_item_scan_date)
            val tvMaxMeasurement: TextView = view.findViewById(R.id.tv_item_max_measurement)
            val tvConfidence: TextView = view.findViewById(R.id.tv_item_confidence)
            val llViewReport: LinearLayout = view.findViewById(R.id.ll_item_view_report)
            val llDeleteScan: LinearLayout = view.findViewById(R.id.ll_item_delete_scan)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient_scan, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]

            holder.tvPatientId.text = "Patient ID: ${item.patientId}"
            holder.tvPatientName.text = if (item.patientName.isEmpty()) "Anonymous" else item.patientName
            holder.tvMaxMeasurement.text = "${item.largestStoneMm} mm"
            holder.tvConfidence.text = "${item.aiConfidence}%"

            // Format date nicely (e.g. yyyy-mm-dd -> dd-mm-yyyy)
            var displayDate = item.scanDate
            try {
                if (item.scanDate.contains("-") && item.scanDate.length >= 10) {
                    val parts = item.scanDate.split("-")
                    if (parts.size == 3) {
                        if (parts[0].length == 4) { // yyyy-mm-dd
                            displayDate = "${parts[2]}-${parts[1]}-${parts[0]}"
                        }
                    }
                }
            } catch (e: Exception) {}
            holder.tvScanDate.text = displayDate

            // Style Badge programmatically
            if (item.isPositive) {
                holder.tvBadge.text = "Positive"
                holder.tvBadge.setTextColor(android.graphics.Color.parseColor("#E02424"))
                holder.tvBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FDE8E8"))
            } else {
                holder.tvBadge.text = "Negative"
                holder.tvBadge.setTextColor(android.graphics.Color.parseColor("#03543F"))
                holder.tvBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#DEF7EC"))
            }

            // Click View Report
            holder.llViewReport.setOnClickListener {
                val intent = Intent(this@PatientScansActivity, ScanReportActivity::class.java).apply {
                    putExtra("PATIENT_ID", item.patientId)
                    putExtra("PATIENT_NAME", item.patientName)
                    putExtra("SCAN_DATE", item.scanDate)
                    putExtra("PATIENT_AGE", item.patientAge)
                    putExtra("PATIENT_GENDER", item.patientGender)
                    putExtra("RESULT", if (item.isPositive) "Positive" else "Negative")
                    putExtra("STONE_COUNT", item.stoneCount)
                    putExtra("MAX_SIZE_MM", item.largestStoneMm)
                    putExtra("CONFIDENCE", item.aiConfidence)
                    putExtra("ANNOTATED_IMAGE_URL", item.annotatedImageUrl)
                }
                startActivity(intent)
            }

            // Click Delete Scan
            holder.llDeleteScan.setOnClickListener {
                confirmDelete(item, holder.adapterPosition)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
