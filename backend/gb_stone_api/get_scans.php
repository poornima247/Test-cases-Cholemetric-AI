<?php
header("Content-Type: application/json");
require_once "db.php";

// Get doctor_id from query parameter
$doctorId = $_GET["doctor_id"] ?? null;

if (!$doctorId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Missing doctor_id parameter"
    ]);
    exit;
}

try {
    // Fetch all scans for this doctor, ordered by most recent first
    $stmt = $pdo->prepare("
        SELECT 
            id,
            patient_id,
            patient_name,
            scan_date,
            is_positive,
            stone_count,
            largest_stone_mm,
            ai_confidence,
            radiologist_text,
            annotated_image_url,
            original_image_url,
            patient_age,
            patient_gender,
            created_at
        FROM scans
        WHERE doctor_id = ?
        ORDER BY created_at DESC
    ");
    
    $stmt->execute([$doctorId]);
    $scans = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        "success" => true,
        "error" => null,
        "scans" => $scans
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Database error while fetching scans"
    ]);
}

