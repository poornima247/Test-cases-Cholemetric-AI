package com.cholemetric.app

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class ScanReportActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_report)

        // Read intent data
        val patientId = intent.getStringExtra("PATIENT_ID") ?: ""
        val patientName = intent.getStringExtra("PATIENT_NAME") ?: ""
        val scanDate = intent.getStringExtra("SCAN_DATE") ?: ""
        val patientAge = intent.getIntExtra("PATIENT_AGE", 0)
        val patientGender = intent.getStringExtra("PATIENT_GENDER") ?: ""
        
        val result = intent.getStringExtra("RESULT") ?: "Positive"
        val stoneCount = intent.getIntExtra("STONE_COUNT", 0)
        val maxSizeMm = intent.getDoubleExtra("MAX_SIZE_MM", 0.0)
        val confidence = intent.getDoubleExtra("CONFIDENCE", 0.0)
        val annotatedImageUrl = intent.getStringExtra("ANNOTATED_IMAGE_URL") ?: ""

        // Fetch Doctor Name from Shared Preferences
        val sharedPref = getSharedPreferences("CholemetricPrefs", MODE_PRIVATE)
        val doctorName = sharedPref.getString("USER_NAME", "Johnson") ?: "Johnson"

        // Find views
        val tvPatId = findViewById<TextView>(R.id.tv_rep_patient_id)
        val tvPatName = findViewById<TextView>(R.id.tv_rep_patient_name)
        val tvScanDate = findViewById<TextView>(R.id.tv_rep_scan_date)
        val tvAge = findViewById<TextView>(R.id.tv_rep_patient_age)
        val tvGender = findViewById<TextView>(R.id.tv_rep_patient_gender)
        val tvDocName = findViewById<TextView>(R.id.tv_rep_doctor_name)
        
        val ivAnnotatedImage = findViewById<ImageView>(R.id.iv_rep_annotated_image)
        val ivWarningIcon = findViewById<ImageView>(R.id.iv_rep_warning_icon)
        val tvResultText = findViewById<TextView>(R.id.tv_rep_result_text)
        
        val tvStoneCount = findViewById<TextView>(R.id.tv_rep_stone_count)
        val tvLargestStone = findViewById<TextView>(R.id.tv_rep_largest_stone)
        val tvConfidence = findViewById<TextView>(R.id.tv_rep_confidence)
        
        val btnDownload = findViewById<Button>(R.id.btn_rep_download)
        val btnClose = findViewById<Button>(R.id.btn_rep_close)
        val llBack = findViewById<LinearLayout>(R.id.ll_report_back)

        // Bind demographics data
        tvPatId.text = patientId
        tvPatName.text = if (patientName.isEmpty()) "Anonymous" else patientName
        tvAge.text = if (patientAge > 0) patientAge.toString() else "N/A"
        tvGender.text = if (patientGender.isNotEmpty()) patientGender else "N/A"
        tvDocName.text = doctorName

        // Format Scan Date nicely (e.g. yyyy-mm-dd -> dd-mm-yyyy)
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

        // Bind clinical metrics
        tvStoneCount.text = stoneCount.toString()
        tvLargestStone.text = "$maxSizeMm mm"
        tvConfidence.text = "AI Confidence: $confidence%"

        // Bind dynamic warning and results text style
        if (result.equals("Positive", ignoreCase = true)) {
            tvResultText.text = "POSITIVE"
            tvResultText.setTextColor(android.graphics.Color.parseColor("#E02424"))
            ivWarningIcon.setImageResource(R.drawable.ic_warning_orange)
            ivWarningIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFA500"))
        } else {
            tvResultText.text = "NEGATIVE"
            tvResultText.setTextColor(android.graphics.Color.parseColor("#03543F"))
            ivWarningIcon.setImageResource(R.drawable.ic_check_circle_green)
            ivWarningIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2ECC71"))
        }

        // Asynchronously load annotated medical imaging
        loadImage(annotatedImageUrl, ivAnnotatedImage)

        // Action: Back click
        llBack.setOnClickListener {
            finish()
        }

        // Action: Close click
        btnClose.setOnClickListener {
            finish()
        }

        // Action: Download click
        btnDownload.setOnClickListener {
            generatePdf(
                patientId, patientName, formattedDate,
                patientAge, patientGender, doctorName,
                result, stoneCount, maxSizeMm, confidence,
                ivAnnotatedImage
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
                } else if (url.startsWith("uploads/") || url.startsWith("images/")) {
                    val fullUrl = ApiConfig.BASE_URL + url
                    val request = Request.Builder().url(fullUrl).build()
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
                    val file = File(url)
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

    private fun generatePdf(
        patientId: String, patientName: String, formattedDate: String,
        patientAge: Int, patientGender: String, doctorName: String,
        result: String, stoneCount: Int, maxSizeMm: Double, confidence: Double,
        ivAnnotatedImage: ImageView
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()

        // Background
        paint.color = Color.parseColor("#F5F6FA")
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Header Banner (Blue)
        paint.color = Color.parseColor("#4A69BD")
        canvas.drawRect(30f, 30f, 565f, 110f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Gallbladder Scan Report", 297.5f, 70f, paint)

        // Attending Info
        paint.color = Color.WHITE
        paint.textSize = 11f
        paint.isFakeBoldText = false
        canvas.drawText("CholeMetric Clinical Diagnostics Center", 297.5f, 92f, paint)

        // Content Section Border (Patient Information)
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.parseColor("#FFFFFF")
        canvas.drawRoundRect(RectF(30f, 130f, 565f, 310f), 12f, 12f, paint)

        // Patient Details Header
        paint.color = Color.parseColor("#111111")
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Patient Information", 50f, 160f, paint)

        // Draw clean divider line
        paint.color = Color.parseColor("#EFEFEF")
        canvas.drawLine(50f, 175f, 545f, 175f, paint)

        // Draw Patient Info Details (Two Columns)
        paint.color = Color.parseColor("#555555")
        paint.textSize = 12f
        paint.isFakeBoldText = false
        
        canvas.drawText("Patient ID:", 50f, 205f, paint)
        canvas.drawText("Patient Name:", 50f, 235f, paint)
        canvas.drawText("Scan Date:", 50f, 265f, paint)
        
        canvas.drawText("Age:", 310f, 205f, paint)
        canvas.drawText("Gender:", 310f, 235f, paint)
        canvas.drawText("Ref. Doctor:", 310f, 265f, paint)

        // Values
        paint.color = Color.parseColor("#111111")
        paint.isFakeBoldText = true
        canvas.drawText(patientId, 150f, 205f, paint)
        canvas.drawText(if (patientName.isEmpty()) "Anonymous" else patientName, 150f, 235f, paint)
        canvas.drawText(formattedDate, 150f, 265f, paint)
        
        canvas.drawText(if (patientAge > 0) patientAge.toString() else "N/A", 410f, 205f, paint)
        canvas.drawText(if (patientGender.isNotEmpty()) patientGender else "N/A", 410f, 235f, paint)
        canvas.drawText(doctorName, 410f, 265f, paint)

        // Medical Imaging Box
        paint.color = Color.parseColor("#FFFFFF")
        canvas.drawRoundRect(RectF(30f, 330f, 565f, 550f), 12f, 12f, paint)

        paint.color = Color.parseColor("#111111")
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Medical Imaging", 50f, 360f, paint)

        // Draw clean divider line
        paint.color = Color.parseColor("#EFEFEF")
        canvas.drawLine(50f, 375f, 545f, 375f, paint)

        // Draw CT scan image
        val drawable = ivAnnotatedImage.drawable
        val destRect = RectF(160f, 390f, 435f, 535f)
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            canvas.drawBitmap(drawable.bitmap, null, destRect, null)
        } else {
            val fallbackBitmap = BitmapFactory.decodeResource(resources, R.drawable.annotated_sample_ct_scan)
            if (fallbackBitmap != null) {
                canvas.drawBitmap(fallbackBitmap, null, destRect, null)
            }
        }

        // Clinical Analysis Box
        paint.color = Color.parseColor("#FFFFFF")
        canvas.drawRoundRect(RectF(30f, 570f, 565f, 770f), 12f, 12f, paint)

        paint.color = Color.parseColor("#111111")
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Clinical Analysis", 50f, 600f, paint)

        // Draw clean divider line
        paint.color = Color.parseColor("#EFEFEF")
        canvas.drawLine(50f, 615f, 545f, 615f, paint)

        // Warning Result Text
        paint.textAlign = Paint.Align.CENTER
        if (result.equals("Positive", ignoreCase = true)) {
            paint.color = Color.parseColor("#E02424")
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("Result: POSITIVE", 297.5f, 645f, paint)
        } else {
            paint.color = Color.parseColor("#03543F")
            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("Result: NEGATIVE", 297.5f, 645f, paint)
        }

        // Stones detected and size
        paint.color = Color.parseColor("#555555")
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("Stones Detected: $stoneCount", 160f, 685f, paint)
        canvas.drawText("Largest Stone Size: $maxSizeMm mm", 415f, 685f, paint)
        
        paint.color = Color.parseColor("#9B59B6")
        paint.textSize = 13f
        paint.isFakeBoldText = true
        canvas.drawText("AI Confidence: $confidence%", 297.5f, 725f, paint)

        // Footer note
        paint.color = Color.parseColor("#AAAAAA")
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("CholeMetric Gallbladder Scan Report â€¢ Generated automatically by AI Diagnostics", 297.5f, 810f, paint)

        pdfDocument.finishPage(page)

        // Save PDF to downloads folder
        try {
            val fileName = "Cholemetric_Report_${patientId}.pdf"
            var outputStream: java.io.OutputStream? = null

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val uri = contentResolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    outputStream = contentResolver.openOutputStream(uri)
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)
                outputStream = FileOutputStream(file)
            }

            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()
                Toast.makeText(this, "Report downloaded to Downloads folder", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to create file", Toast.LENGTH_LONG).show()
                pdfDocument.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to download PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
