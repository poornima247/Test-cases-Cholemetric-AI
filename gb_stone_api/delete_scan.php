<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$scanId = $data["scan_id"] ?? $_GET["scan_id"] ?? null;

if (!$scanId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Missing scan_id parameter"
    ]);
    exit;
}

try {
    $stmt = $pdo->prepare("DELETE FROM scans WHERE id = ?");
    $stmt->execute([$scanId]);

    echo json_encode([
        "success" => true,
        "error" => null,
        "message" => "Scan deleted successfully"
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Database error: " . $e->getMessage()
    ]);
}
