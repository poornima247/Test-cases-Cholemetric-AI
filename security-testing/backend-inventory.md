# Backend Inventory Report

## Project Context
- **Project**: Cholemetric AI
- **Backend Stack**: PHP 8.x, MySQL 5.7+ (gb_stone_db)
- **Architecture**: Monolithic REST API
- **Middleware**: None (Standard PHP files)
- **Authentication**: Native `password_hash()` and `password_verify()`
- **CORS Policy**: Wildcard `*`

## Database Schema (Identified)
### Table: `doctors`
- `id` (INT, Primary Key, Auto-increment)
- `email` (VARCHAR, Unique)
- `password` (VARCHAR, Hashed)
- `full_name` (VARCHAR)
- `hospital` (VARCHAR)
- `specialization` (VARCHAR)

## API Endpoints Inventory

| Endpoint | HTTP Method | Auth Required | Description |
|----------|-------------|---------------|-------------|
| `/login.php` | POST | No | Authenticates doctor, returns success status |
| `/register.php` | POST | No | Registers new doctor account |
| `/get_profile.php` | GET/POST | No (Vulnerability) | Fetches doctor profile information |
| `/update_profile.php` | POST | No (Vulnerability) | Updates doctor profile details |
| `/change_password.php` | POST | No (Vulnerability) | Updates password for a doctor |
| `/analyze.php` | POST | No (Vulnerability) | Processes CT scan image uploads |
| `/save_scan.php` | POST | No (Vulnerability) | Saves AI analysis results |
| `/get_scans.php` | GET/POST | No (Vulnerability) | Retrieves historical scans |
| `/delete_scan.php` | POST | No (Vulnerability) | Deletes a specific scan |
| `/delete.php` | POST | No (Vulnerability) | Deletes doctor account |
| `/clear_all_scans.php` | POST | No (Vulnerability) | Clears all scans for a doctor |
| `/reset_password.php` | POST | No | Resets doctor password |
| `/check_email.php` | GET/POST | No | Checks if an email is already registered |
| `/image_proxy.php` | GET | No | Fetches images to bypass local CORS/Origin issues |
| `/legal.php` | GET | No | Returns legal/privacy policy text |

## Component Analysis
### Database Connectivity (`db.php`)
- **Type**: Raw PDO
- **Error Handling**: PDO exception mode enabled (Verbose errors risk)
- **Security Posture**: Highly vulnerable. Hardcoded plaintext credentials (`root`, `''`).

### File Uploads (`analyze.php`)
- **Mechanism**: Reads `$_FILES['image']`
- **Security Posture**: Missing adequate MIME-type validation. Potential for Remote Code Execution (RCE) via web shell upload.

### Cross-Origin Resource Sharing (CORS)
- **Implementation**: `header("Access-Control-Allow-Origin: *");` placed at the top of PHP files.
- **Security Posture**: Overly permissive. Allows cross-origin requests from any arbitrary domain, bypassing same-origin policy protections.
