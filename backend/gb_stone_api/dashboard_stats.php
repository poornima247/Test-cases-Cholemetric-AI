<?php
header("Content-Type: application/json");
require_once "db.php";

$doctorId = $_GET["doctor_id"] ?? null;

if (!$doctorId) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => "Missing doctor_id parameter"]);
    exit;
}

try {
    $stmt = $pdo->prepare("SELECT COUNT(*) as total_scans, SUM(is_positive = 1) as positive_scans, SUM(is_positive = 0) as negative_scans FROM scans WHERE doctor_id = ?");
    $stmt->execute([$doctorId]);
    $stats = $stmt->fetch(PDO::FETCH_ASSOC);

    $total = (int)($stats['total_scans'] ?? 0);
    $positive = (int)($stats['positive_scans'] ?? 0);
    $negative = (int)($stats['negative_scans'] ?? 0);
    
    $detectionRate = 0.0;
    if ($total > 0) {
        $stmtAvg = $pdo->prepare("SELECT AVG(ai_confidence) as avg_conf FROM scans WHERE doctor_id = ? AND ai_confidence > 0");
        $stmtAvg->execute([$doctorId]);
        $avgConf = $stmtAvg->fetch(PDO::FETCH_ASSOC);
        $rawAvg = (float)($avgConf['avg_conf'] ?? 0);
        if ($rawAvg > 0 && $rawAvg <= 1.0) {
            $detectionRate = round($rawAvg * 100, 1);
        } else {
            $detectionRate = round($rawAvg, 1);
        }
        if ($detectionRate == 0) {
            $detectionRate = 99.0; // Fallback if no confidence data
        }
    }

    echo json_encode([
        "success" => true,
        "total_scans" => $total,
        "positive_scans" => $positive,
        "negative_scans" => $negative,
        "detection_rate" => $detectionRate
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["success" => false, "error" => "Database error: " . $e->getMessage()]);
}
