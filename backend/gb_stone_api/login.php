<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$email    = $data["email"] ?? "";
$password = $data["password"] ?? "";

// ✅ FIXED: Changed password_hash to password (column name in table)
$stmt = $pdo->prepare(
    "SELECT id, password, full_name, hospital, specialization
     FROM doctors WHERE email = ?"
);
$stmt->execute([$email]);
$row = $stmt->fetch();

// ✅ FIXED: Changed password_hash to password
if (!$row || !password_verify($password, $row["password"])) {
    http_response_code(401);
    echo json_encode([
        "success" => false,
        "error"   => "Invalid credentials",
        "doctor"  => null
    ]);
    exit;
}

echo json_encode([
    "success" => true,
    "error"   => null,
    "doctor"  => [
        "id"             => (int)$row["id"],
        "email"          => $email,
        "full_name"      => $row["full_name"],
        "hospital"       => $row["hospital"],
        "specialization" => $row["specialization"]
    ]
]);
