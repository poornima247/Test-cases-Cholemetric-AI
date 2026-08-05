<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);

$email          = trim($data["email"] ?? "");
$password       = $data["password"] ?? "";
$fullName       = $data["full_name"] ?? "";
$hospital       = $data["hospital"] ?? null;
$specialization = $data["specialization"] ?? null;

// Validate Required Fields
if (!$email || !$password || !$fullName) {
    echo json_encode([
        "success" => false,
        "error"   => "Missing required fields"
    ]);
    exit;
}

// Normalize email
$email = strtolower($email);

// Check if Email Already Exists
$check = $pdo->prepare("SELECT id FROM doctors WHERE email = ?");
$check->execute([$email]);

if ($check->rowCount() > 0) {
    echo json_encode([
        "success" => false,
        "error"   => "Email already exists"
    ]);
    exit;
}

// Insert user into database
$hash = password_hash($password, PASSWORD_BCRYPT);

// ✅ FIXED: Changed password_hash to password (column name in table)
$stmt = $pdo->prepare(
    "INSERT INTO doctors (email, password, full_name, hospital, specialization)
     VALUES (?, ?, ?, ?, ?)"
);

try {
    $stmt->execute([$email, $hash, $fullName, $hospital, $specialization]);

    echo json_encode([
        "success" => true,
        "error"   => null
    ]);

} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "error"   => "Database error: " . $e->getMessage()
    ]);
}
