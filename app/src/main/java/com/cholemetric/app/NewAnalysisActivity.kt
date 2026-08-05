package com.cholemetric.app

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class NewAnalysisActivity : AppCompatActivity() {

    private var isDetailsExpanded = false
    private var selectedImageUri: Uri? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    private val calendar = Calendar.getInstance()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            val ivPreviewImage = findViewById<ImageView>(R.id.iv_preview_image)
            val llUploadArea = findViewById<LinearLayout>(R.id.ll_upload_area)
            
            ivPreviewImage.setImageURI(uri)
            ivPreviewImage.visibility = View.VISIBLE
            llUploadArea.visibility = View.GONE
        }
    }

    // Capture results screen completion
    private val resultsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_analysis)

        // Setup Back Button
        val llBack = findViewById<LinearLayout>(R.id.ll_back)
        llBack.setOnClickListener {
            finish()
        }

        // Setup Toggle Details Logic
        val llToggleDetails = findViewById<LinearLayout>(R.id.ll_toggle_details)
        val llAdditionalDetails = findViewById<LinearLayout>(R.id.ll_additional_details)
        val ivToggleIcon = findViewById<ImageView>(R.id.iv_toggle_icon)

        llToggleDetails.setOnClickListener {
            isDetailsExpanded = !isDetailsExpanded
            
            if (isDetailsExpanded) {
                llAdditionalDetails.visibility = View.VISIBLE
                ivToggleIcon.rotation = 90f // Rotate down
            } else {
                llAdditionalDetails.visibility = View.GONE
                ivToggleIcon.rotation = 0f // Rotate right
            }
        }
        
        // Setup Image Upload Logic
        val llUploadArea = findViewById<LinearLayout>(R.id.ll_upload_area)
        val ivPreviewImage = findViewById<ImageView>(R.id.iv_preview_image)
        
        llUploadArea.setOnClickListener {
            pickMedia.launch("image/*")
        }
        ivPreviewImage.setOnClickListener {
            pickMedia.launch("image/*")
        }

        // Setup Scan Date field and Calendar picker
        val etScanDate = findViewById<EditText>(R.id.et_scan_date)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        etScanDate.setText(dateFormat.format(calendar.time))

        etScanDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    etScanDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Setup Start AI Analysis Action
        val btnStartAnalysis = findViewById<Button>(R.id.btn_start_analysis)
        btnStartAnalysis.setOnClickListener {
            startAnalysis()
        }
    }

    private fun startAnalysis() {
        val uri = selectedImageUri
        if (uri == null) {
            Toast.makeText(this, "Please upload a CT Scan image first.", Toast.LENGTH_SHORT).show()
            return
        }

        val patientId = findViewById<EditText>(R.id.et_patient_id).text.toString().trim()
        val patientName = findViewById<EditText>(R.id.et_patient_name).text.toString().trim()
        val patientAgeStr = findViewById<EditText>(R.id.et_age).text.toString().trim()
        val patientAge = if (patientAgeStr.isEmpty()) 0 else patientAgeStr.toInt()
        val patientGender = findViewById<EditText>(R.id.et_gender).text.toString().trim()
        val scanDate = findViewById<EditText>(R.id.et_scan_date).text.toString().trim()

        val finalPatientId = if (patientId.isEmpty()) "P-" + (10000 + (Math.random() * 90000).toInt()) else patientId

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Analyzing scan image...")
            setCancelable(false)
            show()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Copy URI stream to a temporary cache file to upload
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File(cacheDir, "temp_upload_image.jpg")
                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }

                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "scan.jpg", requestFile)
                    .addFormDataPart("patient_id", finalPatientId)
                    .addFormDataPart("scan_date", scanDate)
                    .build()

                val request = Request.Builder()
                    .url("http://172.23.19.66:8080/backend/gb_stone_api/analyze.php")
                    .post(multipartBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""
                val json = JSONObject(responseStr)

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (response.isSuccessful && json.optString("status") == "success") {
                        val intent = Intent(this@NewAnalysisActivity, ScanResultsActivity::class.java).apply {
                            putExtra("PATIENT_ID", json.getString("patient_id"))
                            putExtra("SCAN_DATE", json.getString("scan_date"))
                            putExtra("PATIENT_NAME", patientName)
                            putExtra("PATIENT_AGE", patientAge)
                            putExtra("PATIENT_GENDER", patientGender)
                            
                            putExtra("RESULT", json.getString("result"))
                            putExtra("STONE_COUNT", json.getInt("stone_count"))
                            putExtra("MAX_SIZE_MM", json.getDouble("max_size_mm"))
                            putExtra("CONFIDENCE", json.getDouble("confidence"))
                            putExtra("NOTES", json.getString("notes"))
                            putExtra("ORIGINAL_IMAGE_URL", json.getString("original_image_url"))
                            putExtra("ANNOTATED_IMAGE_URL", json.getString("annotated_image_url"))
                        }
                        resultsLauncher.launch(intent)
                    } else {
                        Toast.makeText(this@NewAnalysisActivity, "Analysis failed: " + json.optString("error", "Unknown error"), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@NewAnalysisActivity, "Connection error. Failed to analyze.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

