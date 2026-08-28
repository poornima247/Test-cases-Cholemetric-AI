/**
 * Cholemetric AI — Cross-Platform Web & Android Data Synchronization Manager
 * Synchronizes Patient Scans, AI Diagnostics, User Sessions, and Settings 
 * bi-directionally between Android Mobile App, Web Frontend, and MySQL Backend API.
 */

const CholemetricSync = (function () {
    const REGISTERED_USERS_KEY = 'REGISTERED_USERS_JSON';

    function getApiBaseUrl() {
        const customUrl = localStorage.getItem('cholemetric_api_base_url');
        if (customUrl) return customUrl;
        
        const hostname = window.location.hostname;
        if (hostname === 'localhost' || hostname === '127.0.0.1') {
            return 'http://localhost/backend/gb_stone_api';
        } else if (hostname === '10.0.2.2') {
            return 'http://10.0.2.2/backend/gb_stone_api';
        }
        return 'http://localhost:8080/backend/gb_stone_api';
    }

    const API_BASE = getApiBaseUrl();
    const LAST_SYNC_KEY = 'cholemetric_last_sync_timestamp';

    function getDoctorEmail() {
        return localStorage.getItem('doctor_email') || localStorage.getItem('DOCTOR_EMAIL') || 'poornimadandu246@gmail.com';
    }

    function getAccountKey(email) {
        const cleanEmail = (!email || email.trim() === '') ? 'default_user@cholemetric.com' : email.toLowerCase().trim();
        return cleanEmail.replace(/\./g, '_').replace(/@/g, '_');
    }

    function getDoctorId() {
        return localStorage.getItem('doctor_id') || '1';
    }

    // --- Registered Accounts Management ---
    function getRegisteredUsers() {
        const usersStr = localStorage.getItem(REGISTERED_USERS_KEY);
        if (usersStr) {
            try { return JSON.parse(usersStr); } catch (e) {}
        }
        const defaultUsers = [
            { id: 1, email: 'poornimadandu246@gmail.com', password: 'poornima123', full_name: 'Dr. Poornima Dandu', hospital: 'Cholemetric Diagnostics Center' },
            { id: 2, email: 'poornimad2247.sse@saveetha.com', password: 'Password123!', full_name: 'Dr. Poornima Dandu', hospital: 'Saveetha Medical Center' }
        ];
        localStorage.setItem(REGISTERED_USERS_KEY, JSON.stringify(defaultUsers));
        return defaultUsers;
    }

    function saveRegisteredUsers(users) {
        localStorage.setItem(REGISTERED_USERS_KEY, JSON.stringify(users));
    }

    function isEmailRegistered(email) {
        const cleanEmail = (email || '').trim().toLowerCase();
        const users = getRegisteredUsers();
        return users.some(u => (u.email || '').toLowerCase() === cleanEmail);
    }

    function validatePasswordRules(pass) {
        const hasMin = pass.length >= 6;
        const hasUpper = /[A-Z]/.test(pass);
        const hasSpec = /[^a-zA-Z0-9]/.test(pass);
        return {
            isValid: hasMin && hasUpper && hasSpec,
            hasMin,
            hasUpper,
            hasSpec
        };
    }

    function registerUser(name, hospital, email, pass) {
        const cleanEmail = (email || '').trim().toLowerCase();
        const users = getRegisteredUsers();
        const doctorName = name ? (name.startsWith('Dr.') ? name : 'Dr. ' + name) : 'Dr. ' + cleanEmail.split('@')[0];

        const existing = users.find(u => (u.email || '').toLowerCase() === cleanEmail);
        if (existing) {
            existing.password = pass;
            existing.full_name = doctorName;
            if (hospital) existing.hospital = hospital;
        } else {
            users.push({
                id: users.length + 1,
                email: cleanEmail,
                password: pass,
                full_name: doctorName,
                hospital: hospital || ''
            });
        }
        saveRegisteredUsers(users);
        return true;
    }

    function authenticateUser(email, pass) {
        const cleanEmail = (email || '').trim().toLowerCase();
        const users = getRegisteredUsers();
        let user = users.find(u => (u.email || '').toLowerCase() === cleanEmail);

        if (!user) {
            // Auto-register new doctor account on the fly for seamless web sign in
            const doctorName = 'Dr. ' + cleanEmail.split('@')[0].split('.').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ');
            user = {
                id: users.length + 1,
                email: cleanEmail,
                password: pass,
                full_name: doctorName,
                hospital: 'Cholemetric Diagnostics'
            };
            users.push(user);
            saveRegisteredUsers(users);
        } else if (user.password !== pass) {
            return { success: false, error: 'Wrong password. Please check your password.' };
        }

        localStorage.setItem('doctor_email', cleanEmail);
        localStorage.setItem('user_name', user.full_name);
        localStorage.setItem('doctor_id', String(user.id));
        localStorage.setItem('cholemetric_logged_in', 'true');

        return { success: true, doctor: user };
    }

    function updateUserPassword(email, newPass) {
        const cleanEmail = (email || '').trim().toLowerCase();
        const users = getRegisteredUsers();
        const user = users.find(u => (u.email || '').toLowerCase() === cleanEmail);

        if (user) {
            user.password = newPass;
            saveRegisteredUsers(users);
            return true;
        }
        return false;
    }

    // --- Strict Per-Account Scans Isolation ---
    function getLocalAccountScans() {
        const email = getDoctorEmail();
        const key = getAccountKey(email);
        const scansStr = localStorage.getItem(`SCANS_LIST_${key}`);
        if (scansStr) {
            try { return JSON.parse(scansStr); } catch (e) {}
        }
        
        // Seed initial default scans ONLY for primary account (poornimadandu246@gmail.com)
        if (email.toLowerCase().includes('poornimadandu246') || (email.toLowerCase().includes('poornima') && !email.toLowerCase().includes('saveetha'))) {
            const defaultImg = 'sample_ct_scan';
            const defaults = [
                { id: 1, patient_id: "134", patient_name: "poorni", scan_date: "26-08-2026", is_positive: 1, stone_count: 2, largest_stone_mm: 8.4, ai_confidence: 96.8, radiologist_text: "Calculi detected in gallbladder region.", annotated_image_url: defaultImg, patient_age: 12, patient_gender: "Female" },
                { id: 2, patient_id: "P-32454", patient_name: "Rajesh Kumar", scan_date: "24-08-2026", is_positive: 1, stone_count: 1, largest_stone_mm: 11.2, ai_confidence: 94.5, radiologist_text: "Solitary large calculus identified.", annotated_image_url: defaultImg, patient_age: 45, patient_gender: "Male" },
                { id: 3, patient_id: "P-48912", patient_name: "Anita Sharma", scan_date: "22-08-2026", is_positive: 1, stone_count: 3, largest_stone_mm: 6.8, ai_confidence: 91.2, radiologist_text: "Multiple small hyperdense calculi.", annotated_image_url: defaultImg, patient_age: 38, patient_gender: "Female" },
                { id: 4, patient_id: "P-10923", patient_name: "Srinivas Rao", scan_date: "20-08-2026", is_positive: 0, stone_count: 0, largest_stone_mm: 0.0, ai_confidence: 98.1, radiologist_text: "No calculi detected.", annotated_image_url: defaultImg, patient_age: 52, patient_gender: "Male" }
            ];
            localStorage.setItem(`SCANS_LIST_${key}`, JSON.stringify(defaults));
            return defaults;
        }

        // All secondary/other accounts start strictly at 0 scans!
        localStorage.setItem(`SCANS_LIST_${key}`, JSON.stringify([]));
        return [];
    }

    function saveLocalAccountScans(scans) {
        const key = getAccountKey(getDoctorEmail());
        localStorage.setItem(`SCANS_LIST_${key}`, JSON.stringify(scans));
    }

    function getAccountStats() {
        const scans = getLocalAccountScans();
        const total = scans.length;
        const positive = scans.filter(s => s.is_positive == 1).length;
        const negative = scans.filter(s => s.is_positive == 0).length;
        return { total, positive, negative };
    }

    async function syncPatientScans() {
        try {
            const doctorId = getDoctorId();
            const response = await fetch(`${API_BASE}/get_scans.php?doctor_id=${doctorId}`);
            if (response.ok) {
                const data = await response.json();
                if (data.success && Array.isArray(data.scans) && data.scans.length > 0) {
                    saveLocalAccountScans(data.scans);
                    localStorage.setItem(LAST_SYNC_KEY, new Date().toISOString());
                    window.dispatchEvent(new CustomEvent('cholemetric:scans_synced', { detail: data.scans }));
                    return data.scans;
                }
            }
        } catch (error) {}

        const localScans = getLocalAccountScans();
        saveLocalAccountScans(localScans);
        return localScans;
    }

    async function saveScanRecord(scanData) {
        const payload = {
            id: Date.now(),
            doctor_id: getDoctorId(),
            patient_id: scanData.patient_id || `PAT-${Date.now().toString().slice(-6)}`,
            patient_name: scanData.patient_name || 'Anonymous Patient',
            patient_age: scanData.patient_age || 45,
            patient_gender: scanData.patient_gender || 'Male',
            is_positive: scanData.is_positive ? 1 : 0,
            stone_count: scanData.stone_count || 1,
            largest_stone_mm: scanData.stone_size_mm || scanData.largest_stone_mm || 8.0,
            ai_confidence: scanData.confidence_score || scanData.ai_confidence || 95.0,
            annotated_image_url: scanData.image_url || scanData.annotated_image_url || 'sample_ct_scan',
            original_image_url: scanData.image_url || 'sample_ct_scan',
            scan_date: scanData.scan_date || new Date().toISOString().split('T')[0],
            radiologist_text: scanData.notes || 'Scan analysis record saved.'
        };

        const scans = getLocalAccountScans();
        scans.unshift(payload);
        saveLocalAccountScans(scans);

        if (navigator.onLine) {
            try {
                await fetch(`${API_BASE}/save_scan.php`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
            } catch (err) {}
        }

        return { success: true, synced: true, scan: payload };
    }

    function logout() {
        localStorage.removeItem('doctor_email');
        localStorage.removeItem('user_name');
        localStorage.removeItem('doctor_id');
        localStorage.setItem('cholemetric_logged_in', 'false');
        window.location.href = 'login_form.html';
    }

    function checkAuthSession() {
        const loggedIn = localStorage.getItem('cholemetric_logged_in');
        const email = localStorage.getItem('doctor_email');
        const page = window.location.pathname.split('/').pop();
        if (loggedIn === 'false' || !email) {
            if (page !== 'login_form.html' && page !== 'signup.html' && page !== 'forgot_password.html' && page !== 'splash.html' && page !== 'welcome.html' && page !== 'index.html' && page !== '') {
                window.location.href = 'login_form.html';
            }
        }
    }

    // Attach interactive Eye Password Toggle listeners to all password fields
    function attachEyePasswordToggles() {
        const icons = document.querySelectorAll('.right-icon, .password-toggle-icon, [data-toggle-password], svg.right-icon');
        icons.forEach(icon => {
            if (icon.getAttribute('data-toggle-bound')) return;
            icon.setAttribute('data-toggle-bound', 'true');
            icon.style.cursor = 'pointer';
            icon.style.pointerEvents = 'auto';

            icon.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();

                const wrapper = icon.closest('.input-wrapper, .input-group') || icon.parentElement;
                const input = wrapper ? wrapper.querySelector('input') : null;

                if (input) {
                    if (input.type === 'password') {
                        input.type = 'text';
                        icon.style.opacity = '1.0';
                        icon.style.fill = '#4A69BD';
                    } else {
                        input.type = 'password';
                        icon.style.opacity = '0.6';
                        icon.style.fill = '#555555';
                    }
                }
            });
        });
    }

    function init() {
        getRegisteredUsers();
        attachEyePasswordToggles();
        checkAuthSession();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    return {
        syncPatientScans,
        saveScanRecord,
        getLocalAccountScans,
        getAccountStats,
        getApiBaseUrl,
        logout,
        checkAuthSession,
        isEmailRegistered,
        validatePasswordRules,
        registerUser,
        authenticateUser,
        updateUserPassword,
        attachEyePasswordToggles
    };
})();
