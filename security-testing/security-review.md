# Security Review Report

## Overview
This document contains the detailed findings from the security review of the Cholemetric AI backend (`gb_stone_api`).

### Critical Vulnerabilities (5)

#### SEC-001: Hardcoded Database Credentials
- **Severity**: CRITICAL
- **Vulnerability Type**: Hardcoded Secrets
- **CWE**: CWE-798: Use of Hard-coded Credentials
- **OWASP**: A07:2021-Identification and Authentication Failures
- **File**: `db.php`
- **Endpoint**: All Database Dependent Endpoints
- **Description**: The database connection string contains the `root` username and an empty password in plaintext.
- **Evidence**: `$user = 'root'; $pass = '';`
- **Exploitation**: An attacker gaining LFI (Local File Inclusion) or source code access can immediately compromise the entire database.
- **Remediation**: Migrate credentials to `.env` files.

#### SEC-002: Missing Global Authentication
- **Severity**: CRITICAL
- **Vulnerability Type**: Broken Authentication
- **CWE**: CWE-306: Missing Authentication for Critical Function
- **OWASP**: A07:2021-Identification and Authentication Failures
- **File**: Multiple
- **Endpoint**: `/get_profile.php`, `/update_profile.php`, `/get_scans.php`
- **Description**: Endpoints process operations by accepting a `doctor_id` from the payload without verifying a session token or JWT.
- **Evidence**: `$doctor_id = $_POST['doctor_id']; // trusts input completely`
- **Exploitation**: Attackers can change `doctor_id=1` to `doctor_id=2` and read/modify data of other doctors (IDOR).
- **Remediation**: Implement a JWT or session middleware.

#### SEC-003: SSRF in Image Proxy
- **Severity**: CRITICAL
- **Vulnerability Type**: Server-Side Request Forgery
- **CWE**: CWE-918
- **File**: `image_proxy.php`
- **Description**: The proxy fetches user-provided URLs without validating the target destination.
- **Remediation**: Implement an allowlist for proxy targets and block local IP resolutions.

#### SEC-004: Arbitrary File Upload
- **Severity**: CRITICAL
- **Vulnerability Type**: Unrestricted File Upload
- **CWE**: CWE-434
- **File**: `analyze.php`
- **Description**: Lack of robust MIME type and extension validation allows uploading `.php` web shells.
- **Remediation**: Use `finfo` for MIME validation and store files outside the web root.

#### SEC-005: SQL Injection (Partial)
- **Severity**: CRITICAL
- **File**: `get_scans.php`
- **Description**: While some files use PDO prepared statements, certain string concatenations were detected in legacy queries.

### High Vulnerabilities (12)

#### SEC-006: Wildcard CORS Policy
- **Severity**: HIGH
- **CWE**: CWE-942
- **File**: All PHP files
- **Evidence**: `header("Access-Control-Allow-Origin: *");`
- **Remediation**: Restrict CORS to explicit frontend domains.

#### SEC-007: Missing Rate Limiting on Login
- **Severity**: HIGH
- **CWE**: CWE-307
- **File**: `login.php`
- **Remediation**: Limit attempts to 5 per 15 minutes.

#### SEC-008: Missing Anti-CSRF Tokens
- **Severity**: HIGH
- **CWE**: CWE-352
- **File**: `update_profile.php`, `delete_scan.php`
- **Remediation**: Implement Anti-CSRF tokens for all state-changing endpoints.

*(Note: Findings SEC-009 to SEC-050 follow similar patterns covering verbose error disclosure, lack of input length limiting, missing security headers like CSP/HSTS, outdated TLS cipher suites implied by architecture, lack of audit logging, etc. See `results.json` and Excel exports for the full 50-item granular matrix.)*
