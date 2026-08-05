<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$email       = $data["email"] ?? "";
$newPassword = $data["new_password"] ?? "";

if (!$email || !$newPassword) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error"   => "Email and new_password are required"
    ]);
    exit;
}

$stmt = $pdo->prepare("SELECT id FROM doctors WHERE email = ?");
$stmt->execute([$email]);
$row = $stmt->fetch();

if (!$row) {
    http_response_code(404);
    echo json_encode([
        "success" => false,
        "error"   => "Email not found"
    ]);
    exit;
}

$hash = password_hash($newPassword, PASSWORD_BCRYPT);
// ✅ FIXED: Changed password_hash to password
$update = $pdo->prepare("UPDATE doctors SET password = ? WHERE email = ?");
$update->execute([$hash, $email]);

echo json_encode([
    "success" => true,
    "error"   => null
]);
