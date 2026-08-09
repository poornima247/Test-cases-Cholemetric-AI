package com.cholemetric.app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
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

class ScanResultsActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_results)

        // Read intent data
        val patientId = intent.getStringExtra("PATIENT_ID") ?: ""
        val scanDate = intent.getStringExtra("SCAN_DATE") ?: ""
        val patientName = intent.getStringExtra("PATIENT_NAME") ?: ""
        val patientAge = intent.getIntExtra("PATIENT_AGE", 0)
        val patientGender = intent.getStringExtra("PATIENT_GENDER") ?: ""
        
        val result = intent.getStringExtra("RESULT") ?: "Positive"
        val stoneCount = intent.getIntExtra("STONE_COUNT", 1)
        val maxSizeMm = intent.getDoubleExtra("MAX_SIZE_MM", 8.2)
        val confidence = intent.getDoubleExtra("CONFIDENCE", 78.3)
        val notes = intent.getStringExtra("NOTES") ?: ""
        val originalImageUrl = intent.getStringExtra("ORIGINAL_IMAGE_URL") ?: ""
        val annotatedImageUrl = intent.getStringExtra("ANNOTATED_IMAGE_URL") ?: ""
        Log.d("Image_test", annotatedImageUrl)

        // Find views
        val tvPatientId = findViewById<TextView>(R.id.tv_res_patient_id)
        val tvScanDate = findViewById<TextView>(R.id.tv_res_scan_date)
        val ivAnnotatedImage = findViewById<ImageView>(R.id.iv_res_annotated_image)
        val tvStoneCount = findViewById<TextView>(R.id.tv_res_stone_count)
        val tvMaxSize = findViewById<TextView>(R.id.tv_res_max_size)
        val tvConfidence = findViewById<TextView>(R.id.tv_res_confidence)
        val vProgressBar = findViewById<View>(R.id.v_res_progress_bar)
        val etNotes = findViewById<EditText>(R.id.et_res_notes)
        val btnSaveReport = findViewById<Button>(R.id.btn_save_report)
        val btnClose = findViewById<Button>(R.id.btn_close_results)
        val llBack = findViewById<LinearLayout>(R.id.ll_results_back)

        // Bind data
        tvPatientId.text = patientId
        
        // Format Scan Date nicely (e.g. 26-10-2025)
        var formattedDate = scanDate
        try {
            if (scanDate.contains("-") && scanDate.length >= 10) {
                val parts = scanDate.split("-")
                if (parts.size == 3) {
                    if (parts[0].length == 4) { // yyyy-mm-dd
                        formattedDate = "${parts[2]}-${parts[1]}-${parts[0]}"
                    }
                }
            }
        } catch (e: Exception) {}
        tvScanDate.text = formattedDate

        tvStoneCount.text = stoneCount.toString()
        tvMaxSize.text = "$maxSizeMm mm"
        tvConfidence.text = "$confidence%"
        etNotes.setText(notes)

        // Dynamically set Positive/Negative badge
        val tvResBadge = findViewById<TextView>(R.id.tv_res_badge)
        tvResBadge.text = result
        if (result.equals("Positive", ignoreCase = true)) {
            tvResBadge.setTextColor(android.graphics.Color.parseColor("#E02424"))
            tvResBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FDE8E8"))
        } else {
            tvResBadge.setTextColor(android.graphics.Color.parseColor("#03543F"))
            tvResBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#DEF7EC"))
        }

        // Set progress bar width based on confidence
        vProgressBar.post {
            val totalWidth = (vProgressBar.parent as View).width
            val progressWidth = (totalWidth * (confidence / 100.0)).toInt()
            val params = vProgressBar.layoutParams
            params.width = progressWidth
            vProgressBar.layoutParams = params
        }

        // Load annotated image asynchronously
        if (annotatedImageUrl.isNotEmpty()) {
            loadImage(annotatedImageUrl, ivAnnotatedImage)
        }

        // Back button action
        llBack.setOnClickListener {
            finish()
        }

        // Close action
        btnClose.setOnClickListener {
            finish()
        }

        // Save Report action
        btnSaveReport.setOnClickListener {
            saveReport(
                patientId, patientName, scanDate, result, stoneCount,
                maxSizeMm, confidence, etNotes.text.toString(),
                originalImageUrl, annotatedImageUrl, patientAge, patientGender
            )
        }
    }

    private fun loadImage(url: String, imageView: ImageView) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveReport(
        patientId: String, patientName: String, scanDate: String, result: String,
        stoneCount: Int, maxSizeMm: Double, confidence: Double, notes: String,
        originalImageUrl: String, annotatedImageUrl: String, patientAge: Int, patientGender: String
    ) {
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        val doctorId = sharedPref.getInt("DOCTOR_ID", -1)

        if (doctorId == -1) {
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        val btnSaveReport = findViewById<Button>(R.id.btn_save_report)
        btnSaveReport.isEnabled = false
        btnSaveReport.text = "Saving..."

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = JSONObject().apply {
                    put("doctor_id", doctorId)
                    put("patient_id", patientId)
                    put("patient_name", if (patientName.isEmpty()) "Anonymous" else patientName)
                    put("scan_date", scanDate)
                    put("is_positive", if (result.equals("Positive", ignoreCase = true)) 1 else 0)
                    put("stone_count", stoneCount)
                    put("largest_stone_mm", maxSizeMm)
                    put("ai_confidence", confidence)
                    put("radiologist_text", notes)
                    put("annotated_image_url", annotatedImageUrl)
                    put("original_image_url", originalImageUrl)
                    put("patient_age", patientAge)
                    put("patient_gender", patientGender)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = payload.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(ApiConfig.BASE_URL + "save_scan.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    btnSaveReport.isEnabled = true;
                    btnSaveReport.text = "Save Report"

                    if (response.isSuccessful && responseJson.optBoolean("success", false)) {
                        Toast.makeText(this@ScanResultsActivity, "Scan report saved successfully!", Toast.LENGTH_SHORT).show()
                        // Finish both analysis and results, navigate back to Dashboard
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@ScanResultsActivity, "Failed to save: " + responseJson.optString("error", "Unknown error"), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    btnSaveReport.isEnabled = true;
                    btnSaveReport.text = "Save Report"
                    Toast.makeText(this@ScanResultsActivity, "Connection error. Failed to save report.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
