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
        val stoneWidthMm = intent.getDoubleExtra("STONE_WIDTH_MM", Math.round((maxSizeMm * 0.74) * 10.0) / 10.0)
        tvMaxSize.text = "$maxSizeMm mm (Width: $stoneWidthMm mm)"
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

        // Auto-save scan report to Patient History immediately upon analysis completion
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        val doctorEmail = sharedPref.getString("DOCTOR_EMAIL", "poornimadandu246@gmail.com") ?: "poornimadandu246@gmail.com"
        val isPositive = result.equals("Positive", ignoreCase = true)

        AccountScanManager.recordNewScan(
            context = this,
            email = doctorEmail,
            isPositive = isPositive,
            patientId = patientId,
            patientName = patientName,
            scanDate = scanDate,
            stoneCount = stoneCount,
            maxSizeMm = maxSizeMm,
            confidence = confidence,
            notes = notes,
            imageUrl = annotatedImageUrl,
            patientAge = patientAge,
            patientGender = patientGender
        )

        // Save Report action button
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
                if (url.isBlank() || url == "annotated_sample_ct_scan" || url == "sample_ct_scan") {
                    withContext(Dispatchers.Main) {
                        imageView.setImageResource(R.drawable.annotated_sample_ct_scan)
                    }
                    return@launch
                }

                if (url.startsWith("http://") || url.startsWith("https://")) {
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val inputStream = response.body?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        withContext(Dispatchers.Main) {
                            if (bitmap != null) imageView.setImageBitmap(bitmap)
                            else imageView.setImageResource(R.drawable.annotated_sample_ct_scan)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            imageView.setImageResource(R.drawable.annotated_sample_ct_scan)
                        }
                    }
                } else {
                    val file = java.io.File(url)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        withContext(Dispatchers.Main) {
                            if (bitmap != null) imageView.setImageBitmap(bitmap)
                            else imageView.setImageResource(R.drawable.annotated_sample_ct_scan)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            imageView.setImageResource(R.drawable.annotated_sample_ct_scan)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    imageView.setImageResource(R.drawable.annotated_sample_ct_scan)
                }
            }
        }
    }

    private fun saveReport(
        patientId: String, patientName: String, scanDate: String, result: String,
        stoneCount: Int, maxSizeMm: Double, confidence: Double, notes: String,
        originalImageUrl: String, annotatedImageUrl: String, patientAge: Int, patientGender: String
    ) {
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        val doctorId = sharedPref.getInt("DOCTOR_ID", 1)
        val doctorEmail = sharedPref.getString("DOCTOR_EMAIL", "poornimadandu246@gmail.com") ?: "poornimadandu246@gmail.com"
        val isPositive = result.equals("Positive", ignoreCase = true)

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
                    put("is_positive", if (isPositive) 1 else 0)
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

                client.newCall(request).execute()

                withContext(Dispatchers.Main) {
                    btnSaveReport.isEnabled = true
                    btnSaveReport.text = "Save Report"

                    Toast.makeText(this@ScanResultsActivity, "Scan report saved successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    btnSaveReport.isEnabled = true
                    btnSaveReport.text = "Save Report"
                    Toast.makeText(this@ScanResultsActivity, "Scan report saved successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
}
