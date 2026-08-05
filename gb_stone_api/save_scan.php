<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$doctorId       = $data["doctor_id"] ?? null;
$patientId      = $data["patient_id"] ?? "";  // ✅ FIXED - removed space
$patientName    = $data["patient_name"] ?? "";
$scanDate       = $data["scan_date"] ?? "";
$isPositive     = !empty($data["is_positive"]) ? 1 : 0;
$stoneCount     = $data["stone_count"] ?? 0;
$largestStoneMm = $data["largest_stone_mm"] ?? 0.0;
$aiConfidence   = $data["ai_confidence"] ?? 0.0;
$notes          = $data["radiologist_text"] ?? null;
$annotUrl       = $data["annotated_image_url"] ?? null;
$origUrl        = $data["original_image_url"] ?? null;
$patientAge     = $data["patient_age"] ?? 0;
$patientGender  = $data["patient_gender"] ?? "";

if (!$doctorId || !$patientId || !$patientName || !$scanDate) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error"   => "Missing fields"
    ]);
    exit;
}

// Parse ISO 8601 date format to MySQL DATE format
if (strtotime($scanDate)) {
    $scanDate = date('Y-m-d', strtotime($scanDate));
}

// Check for duplicates
$checkDuplicate = $pdo->prepare(
    "SELECT id FROM scans 
     WHERE doctor_id = ? AND patient_id = ? AND scan_date = ? AND stone_count = ? 
     LIMIT 1"
);
$checkDuplicate->execute([$doctorId, $patientId, $scanDate, $stoneCount]);

if ($checkDuplicate->rowCount() > 0) {
    $existingScanId = $checkDuplicate->fetch()['id'];
    echo json_encode([
        "success" => true,
        "error"   => null,
        "scan_id" => $existingScanId,
        "note"    => "Duplicate scan detected"
    ]);
    exit;
}

$stmt = $pdo->prepare("
    INSERT INTO scans
      (doctor_id, patient_id, patient_name, scan_date, is_positive,
       stone_count, largest_stone_mm, ai_confidence, radiologist_text,
       annotated_image_url, original_image_url, patient_age, patient_gender)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
");

try {
    $stmt->execute([
        $doctorId, $patientId, $patientName, $scanDate, $isPositive,
        $stoneCount, $largestStoneMm, $aiConfidence, $notes,
        $annotUrl, $origUrl, $patientAge, $patientGender
    ]);

    echo json_encode([
        "success" => true,
        "error"   => null,
        "scan_id" => $pdo->lastInsertId()
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error"   => "Database error: " . $e->getMessage()
    ]);
}

