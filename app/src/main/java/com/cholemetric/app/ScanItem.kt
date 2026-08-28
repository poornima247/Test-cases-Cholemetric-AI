package com.cholemetric.app

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
