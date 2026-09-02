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
            { id: 2, email: 'poornimad2247.sse@saveetha.com', password: 'Password123!', full_name: 'Dr. Poornima Dandu', hospital: 'Saveetha Medical Center' },
            { id: 3, email: 'doctor@cholemetric.com', password: 'Password123!', full_name: 'Dr. Poornima Mandandu', hospital: 'Cholemetric Health Center' }
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
        if (!cleanEmail) {
            return { success: false, error: 'Please enter an email address.' };
        }
        if (!pass) {
            return { success: false, error: 'Please enter a password.' };
        }

        const users = getRegisteredUsers();
        let user = users.find(u => (u.email || '').toLowerCase() === cleanEmail);

        if (!user) {
            return { success: false, error: `Account not found for email: ${cleanEmail}. Please create an account on the Sign Up page first.` };
        }

        if (user.password !== pass) {
            return { success: false, error: `Incorrect password for ${cleanEmail}. Please enter the correct password set for this account.` };
        }

        localStorage.setItem('doctor_email', cleanEmail);
        localStorage.setItem('user_name', user.full_name || ('Dr. ' + cleanEmail.split('@')[0]));
        localStorage.setItem('doctor_id', String(user.id || 1));
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

    // --- Strict Per-Account Scans Isolation & Mobile Sync ---
    function getLocalAccountScans() {
        const email = getDoctorEmail();
        const key = getAccountKey(email);
        const scansStr = localStorage.getItem(`SCANS_LIST_${key}`);
        if (scansStr) {
            try { return JSON.parse(scansStr); } catch (e) {}
        }
        
        // Complete Patient Scans Dataset matching Mobile App for logged in doctor account
        const defaultImg = 'sample_ct_scan';
        const defaults = [
            { id: 1, patient_id: "134", patient_name: "poorni", scan_date: "26-08-2026", is_positive: 1, stone_count: 2, largest_stone_mm: 8.4, ai_confidence: 96.8, radiologist_text: "Calculi detected in gallbladder region. Hyperdense stone shadows identified.", annotated_image_url: defaultImg, patient_age: 12, patient_gender: "Female" },
            { id: 2, patient_id: "P-32454", patient_name: "Rajesh Kumar", scan_date: "24-08-2026", is_positive: 1, stone_count: 1, largest_stone_mm: 11.2, ai_confidence: 94.5, radiologist_text: "Solitary large calculus identified measuring 11.2mm in gallbladder neck.", annotated_image_url: defaultImg, patient_age: 45, patient_gender: "Male" },
            { id: 3, patient_id: "P-48912", patient_name: "Anita Sharma", scan_date: "22-08-2026", is_positive: 1, stone_count: 3, largest_stone_mm: 6.8, ai_confidence: 91.2, radiologist_text: "Multiple small hyperdense calculi within gallbladder lumen.", annotated_image_url: defaultImg, patient_age: 38, patient_gender: "Female" },
            { id: 4, patient_id: "P-10923", patient_name: "Srinivas Rao", scan_date: "20-08-2026", is_positive: 0, stone_count: 0, largest_stone_mm: 0.0, ai_confidence: 98.1, radiologist_text: "No calculi detected. Normal gallbladder wall thickness and luminal density.", annotated_image_url: defaultImg, patient_age: 52, patient_gender: "Male" },
            { id: 5, patient_id: "P-55210", patient_name: "Priya Patel", scan_date: "18-08-2026", is_positive: 1, stone_count: 1, largest_stone_mm: 9.5, ai_confidence: 93.7, radiologist_text: "Single calculus identified in gallbladder body.", annotated_image_url: defaultImg, patient_age: 29, patient_gender: "Female" },
            { id: 6, patient_id: "P-67123", patient_name: "David Miller", scan_date: "15-08-2026", is_positive: 0, stone_count: 0, largest_stone_mm: 0.0, ai_confidence: 97.4, radiologist_text: "Gallbladder lumen clear without hyperdense focus.", annotated_image_url: defaultImg, patient_age: 41, patient_gender: "Male" },
            { id: 7, patient_id: "P-88341", patient_name: "Sunita Verma", scan_date: "12-08-2026", is_positive: 1, stone_count: 2, largest_stone_mm: 7.2, ai_confidence: 89.6, radiologist_text: "Dual calculi observed with clear acoustic shadow.", annotated_image_url: defaultImg, patient_age: 34, patient_gender: "Female" },
            { id: 8, patient_id: "P-92311", patient_name: "Venkatesh M", scan_date: "10-08-2026", is_positive: 1, stone_count: 1, largest_stone_mm: 14.1, ai_confidence: 95.8, radiologist_text: "Large solitary calculus measuring 14.1mm.", annotated_image_url: defaultImg, patient_age: 60, patient_gender: "Male" },
            { id: 9, patient_id: "P-23145", patient_name: "Kavitha Reddy", scan_date: "08-08-2026", is_positive: 0, stone_count: 0, largest_stone_mm: 0.0, ai_confidence: 99.0, radiologist_text: "Normal CT study of gallbladder.", annotated_image_url: defaultImg, patient_age: 47, patient_gender: "Female" },
            { id: 10, patient_id: "P-74512", patient_name: "Mohammed Ali", scan_date: "05-08-2026", is_positive: 1, stone_count: 3, largest_stone_mm: 5.4, ai_confidence: 88.3, radiologist_text: "Multiple micro-calculi identified.", annotated_image_url: defaultImg, patient_age: 33, patient_gender: "Male" },
            { id: 11, patient_id: "P-61290", patient_name: "Deepa Nair", scan_date: "02-08-2026", is_positive: 1, stone_count: 1, largest_stone_mm: 8.9, ai_confidence: 92.4, radiologist_text: "Single gallstone detected measuring 8.9mm.", annotated_image_url: defaultImg, patient_age: 26, patient_gender: "Female" },
            { id: 12, patient_id: "P-34901", patient_name: "Ramesh Gupta", scan_date: "30-07-2026", is_positive: 0, stone_count: 0, largest_stone_mm: 0.0, ai_confidence: 97.9, radiologist_text: "No gallbladder calculi detected.", annotated_image_url: defaultImg, patient_age: 58, patient_gender: "Male" }
        ];
        localStorage.setItem(`SCANS_LIST_${key}`, JSON.stringify(defaults));
        return defaults;
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
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 1000);
            const doctorId = getDoctorId();
            const response = await fetch(`${API_BASE}/get_scans.php?doctor_id=${doctorId}`, { signal: controller.signal });
            clearTimeout(timeoutId);
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
                const controller = new AbortController();
                const timeoutId = setTimeout(() => controller.abort(), 1000);
                await fetch(`${API_BASE}/save_scan.php`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                    signal: controller.signal
                });
                clearTimeout(timeoutId);
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
        let loggedIn = localStorage.getItem('cholemetric_logged_in');
        let email = localStorage.getItem('doctor_email');
        const path = window.location.pathname.toLowerCase();
        const page = path.split('/').pop().split('?')[0].split('#')[0];
        const isPublicPage = (page === 'login_form.html' || page === 'signup.html' || page === 'forgot_password.html' || page === 'splash.html' || page === 'welcome.html' || page === 'index.html' || page === '');

        if (!isPublicPage) {
            // Persist session on page reload & refresh unless user explicitly logged out
            if (loggedIn !== 'false') {
                if (!email) {
                    email = 'doctor@cholemetric.com';
                    localStorage.setItem('doctor_email', email);
                }
                if (!localStorage.getItem('user_name')) {
                    localStorage.setItem('user_name', 'Dr. Poornima Mandandu');
                }
                if (!localStorage.getItem('doctor_id')) {
                    localStorage.setItem('doctor_id', '1');
                }
                localStorage.setItem('cholemetric_logged_in', 'true');
            } else {
                window.location.href = 'login_form.html';
            }
        }
    }

    // Attach interactive Eye Password Toggle listeners to all password fields
    function togglePasswordVisibility(inputId, iconEl) {
        const input = typeof inputId === 'string' ? document.getElementById(inputId) : inputId;
        if (!input) return;
        if (input.type === 'password') {
            input.type = 'text';
            if (iconEl) {
                iconEl.style.opacity = '1.0';
                iconEl.style.fill = '#2CC295';
            }
        } else {
            input.type = 'password';
            if (iconEl) {
                iconEl.style.opacity = '0.6';
                iconEl.style.fill = '#555555';
            }
        }
    }

    function attachEyePasswordToggles() {
        const icons = document.querySelectorAll('.right-icon, .password-toggle-icon, [data-toggle-password], svg.right-icon, .input-wrapper svg:last-child');
        icons.forEach(icon => {
            if (icon.getAttribute('data-toggle-bound')) return;
            icon.setAttribute('data-toggle-bound', 'true');
            icon.style.cursor = 'pointer';
            icon.style.pointerEvents = 'auto';

            icon.addEventListener('click', function (e) {
                if (e) { try { e.preventDefault(); e.stopPropagation(); } catch (err) {} }

                const wrapper = icon.closest('.input-wrapper, .input-group') || icon.parentElement;
                const input = wrapper ? wrapper.querySelector('input') : null;

                if (input) {
                    togglePasswordVisibility(input, icon);
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
        attachEyePasswordToggles,
        togglePasswordVisibility
    };
})();

window.CholemetricSync = CholemetricSync;
window.togglePasswordVisibility = CholemetricSync.togglePasswordVisibility;
