package com.cholemetric.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object AccountScanManager {

    fun getAccountKey(email: String): String {
        val cleanEmail = if (email.isBlank()) "default_user@cholemetric.com" else email.lowercase().trim()
        return cleanEmail.replace(".", "_").replace("@", "_")
    }

    fun getStats(context: Context, email: String): Triple<Int, Int, Int> {
        val prefs = context.getSharedPreferences("CholemetricPrefs", Context.MODE_PRIVATE)
        val key = getAccountKey(email)
        
        val isFirstTime = !prefs.contains("ACCOUNT_INITIALIZED_$key")
        if (isFirstTime) {
            prefs.edit().putBoolean("ACCOUNT_INITIALIZED_$key", true).apply()
            
            // Primary initial user gets sample 12 scans; ALL OTHER NEW ACCOUNTS START AT 0 SCANS!
            if (email.contains("poornima", ignoreCase = true) || email.contains("246", ignoreCase = true)) {
                prefs.edit()
                    .putInt("TOTAL_$key", 12)
                    .putInt("POSITIVE_$key", 8)
                    .putInt("NEGATIVE_$key", 4)
                    .apply()
                return Triple(12, 8, 4)
            } else {
                prefs.edit()
                    .putInt("TOTAL_$key", 0)
                    .putInt("POSITIVE_$key", 0)
                    .putInt("NEGATIVE_$key", 0)
                    .apply()
                return Triple(0, 0, 0)
            }
        }

        val total = prefs.getInt("TOTAL_$key", 0)
        val positive = prefs.getInt("POSITIVE_$key", 0)
        val negative = prefs.getInt("NEGATIVE_$key", 0)
        return Triple(total, positive, negative)
    }

    fun recordNewScan(
        context: Context,
        email: String,
        isPositive: Boolean,
        patientId: String,
        patientName: String,
        scanDate: String,
        stoneCount: Int,
        maxSizeMm: Double,
        confidence: Double,
        notes: String,
        imageUrl: String,
        patientAge: Int,
        patientGender: String
    ) {
        val prefs = context.getSharedPreferences("CholemetricPrefs", Context.MODE_PRIVATE)
        val key = getAccountKey(email)

        // Force initial scans to be loaded into SharedPreferences if not present
        val currentScans = getScansForAccount(context, email)

        val scansArrayStr = prefs.getString("SCANS_LIST_$key", "[]") ?: "[]"
        val scansArray = try { JSONArray(scansArrayStr) } catch (e: Exception) { JSONArray() }

        val newScanObj = JSONObject().apply {
            put("id", System.currentTimeMillis().toInt())
            put("patient_id", patientId)
            put("patient_name", if (patientName.isEmpty()) "Anonymous" else patientName)
            put("scan_date", scanDate)
            put("is_positive", if (isPositive) 1 else 0)
            put("stone_count", stoneCount)
            put("largest_stone_mm", maxSizeMm)
            put("ai_confidence", confidence)
            put("radiologist_text", notes)
            put("annotated_image_url", imageUrl)
            put("original_image_url", imageUrl)
            put("patient_age", patientAge)
            put("patient_gender", patientGender)
        }

        val updatedArray = JSONArray()
        updatedArray.put(newScanObj)
        for (i in 0 until scansArray.length()) {
            updatedArray.put(scansArray.getJSONObject(i))
        }

        // Calculate and save exact updated stats
        var totalPos = 0
        var totalNeg = 0
        for (i in 0 until updatedArray.length()) {
            val item = updatedArray.getJSONObject(i)
            if (item.optInt("is_positive", 0) == 1) totalPos++ else totalNeg++
        }

        prefs.edit()
            .putInt("TOTAL_$key", updatedArray.length())
            .putInt("POSITIVE_$key", totalPos)
            .putInt("NEGATIVE_$key", totalNeg)
            .putString("SCANS_LIST_$key", updatedArray.toString())
            .apply()
    }

    fun getScansForAccount(context: Context, email: String): List<ScanItem> {
        val prefs = context.getSharedPreferences("CholemetricPrefs", Context.MODE_PRIVATE)
        val key = getAccountKey(email)

        val isFirstTime = !prefs.contains("ACCOUNT_INITIALIZED_$key")
        if (isFirstTime) {
            getStats(context, email)
        }

        var scansArrayStr = prefs.getString("SCANS_LIST_$key", null)
        if (scansArrayStr.isNullOrEmpty()) {
            if (email.contains("poornima", ignoreCase = true) || email.contains("246", ignoreCase = true)) {
                val defaults = getInitialDefaultScans()
                val jsonArray = JSONArray()
                for (item in defaults) {
                    val obj = JSONObject().apply {
                        put("id", item.id)
                        put("patient_id", item.patientId)
                        put("patient_name", item.patientName)
                        put("scan_date", item.scanDate)
                        put("is_positive", if (item.isPositive) 1 else 0)
                        put("stone_count", item.stoneCount)
                        put("largest_stone_mm", item.largestStoneMm)
                        put("ai_confidence", item.aiConfidence)
                        put("radiologist_text", item.notes)
                        put("annotated_image_url", item.annotatedImageUrl)
                        put("original_image_url", item.originalImageUrl)
                        put("patient_age", item.patientAge)
                        put("patient_gender", item.patientGender)
                    }
                    jsonArray.put(obj)
                }
                scansArrayStr = jsonArray.toString()
                prefs.edit().putString("SCANS_LIST_$key", scansArrayStr).apply()
            } else {
                return emptyList()
            }
        }

        val list = mutableListOf<ScanItem>()
        try {
            val jsonArray = JSONArray(scansArrayStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ScanItem(
                        id = obj.optInt("id", i + 1),
                        patientId = obj.optString("patient_id", "P-100"),
                        patientName = obj.optString("patient_name", "Anonymous"),
                        scanDate = obj.optString("scan_date", "2026-08-25"),
                        isPositive = obj.optInt("is_positive", 1) == 1,
                        stoneCount = obj.optInt("stone_count", 0),
                        largestStoneMm = obj.optDouble("largest_stone_mm", 0.0),
                        aiConfidence = obj.optDouble("ai_confidence", 95.0),
                        notes = obj.optString("radiologist_text", ""),
                        annotatedImageUrl = obj.optString("annotated_image_url", ""),
                        originalImageUrl = obj.optString("original_image_url", ""),
                        patientAge = obj.optInt("patient_age", 0),
                        patientGender = obj.optString("patient_gender", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun getInitialDefaultScans(): List<ScanItem> {
        val defaultImg = "annotated_sample_ct_scan"
        val list = mutableListOf<ScanItem>()
        list.add(ScanItem(1, "134", "poorni", "26-08-2026", true, 2, 8.4, 96.8, "Calculi detected in gallbladder region. Hyperdense stone shadows identified.", defaultImg, defaultImg, 12, "Female"))
        list.add(ScanItem(2, "P-32454", "Rajesh Kumar", "24-08-2026", true, 1, 11.2, 94.5, "Solitary large calculus identified measuring 11.2mm in gallbladder neck.", defaultImg, defaultImg, 45, "Male"))
        list.add(ScanItem(3, "P-48912", "Anita Sharma", "22-08-2026", true, 3, 6.8, 91.2, "Multiple small hyperdense calculi within gallbladder lumen.", defaultImg, defaultImg, 38, "Female"))
        list.add(ScanItem(4, "P-10923", "Srinivas Rao", "20-08-2026", false, 0, 0.0, 98.1, "No calculi detected. Normal gallbladder wall thickness and luminal density.", defaultImg, defaultImg, 52, "Male"))
        list.add(ScanItem(5, "P-55210", "Priya Patel", "18-08-2026", true, 1, 9.5, 93.7, "Single calculus identified in gallbladder body.", defaultImg, defaultImg, 29, "Female"))
        list.add(ScanItem(6, "P-67123", "David Miller", "15-08-2026", false, 0, 0.0, 97.4, "Gallbladder lumen clear without hyperdense focus.", defaultImg, defaultImg, 41, "Male"))
        list.add(ScanItem(7, "P-88341", "Sunita Verma", "12-08-2026", true, 2, 7.2, 89.6, "Dual calculi observed with clear acoustic shadow.", defaultImg, defaultImg, 34, "Female"))
        list.add(ScanItem(8, "P-92311", "Venkatesh M", "10-08-2026", true, 1, 14.1, 95.8, "Large solitary calculus measuring 14.1mm.", defaultImg, defaultImg, 60, "Male"))
        list.add(ScanItem(9, "P-23145", "Kavitha Reddy", "08-08-2026", false, 0, 0.0, 99.0, "Normal CT study of gallbladder.", defaultImg, defaultImg, 47, "Female"))
        list.add(ScanItem(10, "P-74512", "Mohammed Ali", "05-08-2026", true, 3, 5.4, 88.3, "Multiple micro-calculi identified.", defaultImg, defaultImg, 33, "Male"))
        list.add(ScanItem(11, "P-61290", "Deepa Nair", "02-08-2026", true, 1, 8.9, 92.4, "Single gallstone detected measuring 8.9mm.", defaultImg, defaultImg, 26, "Female"))
        list.add(ScanItem(12, "P-34901", "Ramesh Gupta", "30-07-2026", false, 0, 0.0, 97.9, "No gallbladder calculi detected.", defaultImg, defaultImg, 58, "Male"))
        return list
    }
}
