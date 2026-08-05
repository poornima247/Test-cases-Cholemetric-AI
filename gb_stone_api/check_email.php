<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$email = trim($data["email"] ?? "");

if (!$email) {
    echo json_encode([
        "success" => false,
        "error"   => "Email is required"
    ]);
    exit;
}

$stmt = $pdo->prepare("SELECT id FROM doctors WHERE email = ?");
$stmt->execute([$email]);

echo json_encode([
    "success" => true,
    "exists"  => $stmt->rowCount() > 0
]);

