<?php
header("Content-Type: application/json");
require_once "db.php";

$data = json_decode(file_get_contents("php://input"), true);
$email = $data["email"] ?? "";
$password = $data["password"] ?? "";

if (empty($email) || empty($password)) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => "Email and password are required"
    ]);
    exit;
}

try {
    // First, verify the user's credentials
    $stmt = $pdo->prepare("SELECT id, password FROM doctors WHERE email = ?");
    $stmt->execute([$email]);
    $row = $stmt->fetch();

    if (!$row || !password_verify($password, $row["password"])) {
        http_response_code(401);
        echo json_encode([
            "success" => false,
            "error" => "Invalid credentials"
        ]);
        exit;
    }

    $userId = $row["id"];

    // Delete all scan results associated with the user
    $stmt = $pdo->prepare("DELETE FROM scan_results WHERE doctor_email = ?");
    $stmt->execute([$email]);

    // Delete the user account
    $stmt = $pdo->prepare("DELETE FROM doctors WHERE id = ?");
    $stmt->execute([$userId]);

    echo json_encode([
        "success" => true,
        "message" => "Account deleted successfully"
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => "Failed to delete account: " . $e->getMessage()
    ]);
}
