# Dependency & Environment Security Report

## 1. PHP Runtime Environment
**Detected Setup**: PHP 8.x via XAMPP (Windows)
**Risk Level**: Medium-High

### Security Considerations for PHP 8.x
- Ensure PHP is strictly updated to the latest minor/patch version (e.g., 8.2.16 or 8.3.3) to mitigate known CVEs.
- **Configuration Risks**: Default XAMPP `php.ini` often has `display_errors = On`. This must be strictly set to `Off` in production to prevent information disclosure.
- `expose_php = On` is likely enabled. It should be disabled to prevent banner grabbing.

### Known Recent PHP CVEs (Monitor)
- CVE-2023-3823: XML External Entity (XXE) processing.
- CVE-2023-3824: Phar directory parsing buffer overflow.
- *Mitigation: Keep PHP patched.*

## 2. XAMPP Server Architecture
**Risk Level**: High
XAMPP is designed for **development**, not production. 
- Apache modules might be enabled unnecessarily (e.g., mod_status, mod_info).
- XAMPP default pages (`/dashboard`) often remain accessible if not explicitly removed.
- **Recommendation**: Do not use XAMPP in production. Migrate to a hardened Linux server (Ubuntu/Debian) running Nginx or a stripped-down Apache instance with PHP-FPM.

## 3. Database: MySQL 5.7+ (gb_stone_db)
**Risk Level**: Medium
- MySQL 5.7 reached End of Life (EOL) in October 2023. It will no longer receive security updates.
- **Recommendation**: Upgrade immediately to MySQL 8.0.x or migrate to MariaDB.
- **Configuration Risks**: The backend uses the `root` user with an empty password. This violates the principle of least privilege.
- **Recommendation**: Create a dedicated database user (e.g., `cholemetric_db_user`) with scoped permissions (SELECT, INSERT, UPDATE, DELETE) only for the `gb_stone_db` database.

## 4. Package Management (Composer)
**Status**: No `composer.json` found.
**Risk Level**: Low
- The application currently relies entirely on vanilla PHP without external dependencies. 
- While this reduces the supply-chain attack surface, it heavily increases the burden on developers to manually implement complex features securely (e.g., JWT signing, robust CORS, CSRF tokens).
- **Recommendation**: Introduce Composer to safely integrate industry-standard libraries like `firebase/php-jwt` for authentication and `vlucas/phpdotenv` for secrets management.
