<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
require_once "db.php";

$doctorId = $_GET["doctor_id"] ?? $_POST["doctor_id"] ?? null;

if (!$doctorId) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Missing doctor_id parameter"
    ]);
    exit;
}

try {
    $stmt = $pdo->prepare("SELECT id, email, full_name, hospital, specialization FROM doctors WHERE id = ?");
    $stmt->execute([$doctorId]);
    $doctor = $stmt->fetch();

    if (!$doctor) {
        http_response_code(404);
        echo json_encode([
            "success" => false,
            "error" => "Doctor profile not found"
        ]);
        exit;
    }

    echo json_encode([
        "success" => true,
        "error" => null,
        "doctor" => [
            "id" => (int)$doctor["id"],
            "email" => $doctor["email"],
            "full_name" => $doctor["full_name"],
            "hospital" => $doctor["hospital"] ?? "",
            "specialization" => $doctor["specialization"] ?? ""
        ]
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Database error: " . $e->getMessage()
    ]);
}
