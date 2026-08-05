<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$doctorId = $data["doctor_id"] ?? $_GET["doctor_id"] ?? null;

if (!$doctorId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Missing doctor_id parameter"
    ]);
    exit;
}

try {
    $stmt = $pdo->prepare("DELETE FROM scans WHERE doctor_id = ?");
    $stmt->execute([$doctorId]);

    echo json_encode([
        "success" => true,
        "error" => null,
        "message" => "All scans cleared successfully"
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Database error: " . $e->getMessage()
    ]);
}
