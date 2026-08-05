# Remediation Guide

This guide provides step-by-step instructions to fix the security vulnerabilities identified in the Cholemetric AI backend.

## 1. Hardcoded DB Credentials (`db.php`)
**Problem:** Plaintext `root` and empty password are used.
**Before:**
```php
$host = 'localhost';
$db = 'gb_stone_db';
$user = 'root';
$pass = '';
```
**After (Using Environment Variables):**
Install `vlucas/phpdotenv` or read from `$_ENV`.
```php
$host = getenv('DB_HOST') ?: 'localhost';
$db = getenv('DB_NAME') ?: 'gb_stone_db';
$user = getenv('DB_USER') ?: 'secure_user';
$pass = getenv('DB_PASS') ?: 'secure_password';
```
*Verification:* Check that `.env` is listed in `.gitignore` and credentials are not in code.

## 2. Authentication Middleware
**Problem:** Endpoints like `update_profile.php` trust inputs without verifying the user.
**After (Implementing JWT Verification):**
Create `auth_middleware.php`:
```php
require_once 'vendor/autoload.php';
use \Firebase\JWT\JWT;
use \Firebase\JWT\Key;

function authenticate() {
    $headers = apache_request_headers();
    if (!isset($headers['Authorization'])) {
        http_response_code(401);
        die(json_encode(["error" => "Unauthorized"]));
    }
    $token = str_replace('Bearer ', '', $headers['Authorization']);
    try {
        $decoded = JWT::decode($token, new Key(getenv('JWT_SECRET'), 'HS256'));
        return $decoded->user_id;
    } catch (Exception $e) {
        http_response_code(401);
        die(json_encode(["error" => "Invalid token"]));
    }
}
```
*Include `authenticate();` at the top of protected files.*

## 3. Fixing Overly Permissive CORS
**Problem:** `header("Access-Control-Allow-Origin: *");` allows anyone to hit the API.
**After:**
```php
$allowed_origins = ['https://cholemetric-ai.com', 'http://localhost:3000'];
$origin = $_SERVER['HTTP_ORIGIN'] ?? '';

if (in_array($origin, $allowed_origins)) {
    header("Access-Control-Allow-Origin: $origin");
}
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Credentials: true");
```

## 4. Securing File Uploads (`analyze.php`)
**Problem:** Trusting extensions and missing MIME validation.
**After:**
```php
$finfo = new finfo(FILEINFO_MIME_TYPE);
$mime = $finfo->file($_FILES['image']['tmp_name']);
$allowed_mimes = ['image/jpeg', 'image/png', 'application/dicom'];

if (!in_array($mime, $allowed_mimes)) {
    die(json_encode(["error" => "Invalid file type."]));
}

// Ensure random filename
$ext = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
$filename = bin2hex(random_bytes(16)) . '.' . $ext;
move_uploaded_file($_FILES['image']['tmp_name'], "uploads/" . $filename);
```

## 5. Adding Rate Limiting
**Problem:** Brute forcing `login.php`.
**Fix:** Implement a database or Redis backed rate limiter locking out IPs after 5 failed attempts for 15 minutes.

## 6. Server-Side Request Forgery (SSRF) in `image_proxy.php`
**Problem:** Script fetches arbitrary URLs based on user input.
**Fix:** 
- Restrict fetched URLs to an allowlist of trusted domains (e.g., your own S3 bucket).
- Validate the URL scheme (only `https://`).
- Ensure the destination IP does not resolve to internal network spaces (`127.0.0.1`, `10.x.x.x`, `192.168.x.x`).
