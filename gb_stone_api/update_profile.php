<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$doctorId = $data["doctor_id"] ?? $_POST["doctor_id"] ?? null;
$fullName = $data["full_name"] ?? $_POST["full_name"] ?? "";
$email = $data["email"] ?? $_POST["email"] ?? "";
$hospital = $data["hospital"] ?? $_POST["hospital"] ?? "";
$specialization = $data["specialization"] ?? $_POST["specialization"] ?? "";

if (!$doctorId || !$fullName || !$email) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Missing required parameters (doctor_id, full_name, email)"
    ]);
    exit;
}

try {
    // Check if the email is already in use by another doctor
    $stmt = $pdo->prepare("SELECT id FROM doctors WHERE email = ? AND id != ?");
    $stmt->execute([$email, $doctorId]);
    if ($stmt->fetch()) {
        echo json_encode([
            "success" => false,
            "error" => "This email address is already in use by another doctor."
        ]);
        exit;
    }

    // Update doctor record
    $stmt = $pdo->prepare("UPDATE doctors SET full_name = ?, email = ?, hospital = ?, specialization = ? WHERE id = ?");
    $stmt->execute([$fullName, $email, $hospital, $specialization, $doctorId]);

    echo json_encode([
        "success" => true,
        "error" => null,
        "message" => "Profile updated successfully"
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Database error: " . $e->getMessage()
    ]);
}
