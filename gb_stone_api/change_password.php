<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$email = $data["email"] ?? null;
$oldPassword = $data["old_password"] ?? null;
$newPassword = $data["new_password"] ?? null;

if (!$email || !$oldPassword || !$newPassword) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Missing required fields"
    ]);
    exit;
}

try {
    // Verify old password
    $stmt = $pdo->prepare("SELECT password FROM doctors WHERE email = ?");
    $stmt->execute([$email]);
    $doctor = $stmt->fetch();
    
    if (!$doctor) {
        echo json_encode([
            "success" => false,
            "error" => "Doctor not found"
        ]);
        exit;
    }
    
    if (!password_verify($oldPassword, $doctor['password'])) {
        echo json_encode([
            "success" => false,
            "error" => "Current password is incorrect"
        ]);
        exit;
    }
    
    // Update to new password
    $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);
    $stmt = $pdo->prepare("UPDATE doctors SET password = ? WHERE email = ?");
    $stmt->execute([$hashedPassword, $email]);
    
    echo json_encode([
        "success" => true,
        "error" => null
    ]);
    
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Database error"
    ]);
}
