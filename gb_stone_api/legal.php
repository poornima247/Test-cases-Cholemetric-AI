<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CholeMetric - Legal Information</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            line-height: 1.6;
            color: #333;
            background: #f5f5f5;
        }
        
        .container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            min-height: 100vh;
        }
        
        header {
            text-align: center;
            padding: 30px 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        header h1 {
            font-size: 2em;
            margin-bottom: 10px;
        }
        
        header p {
            font-size: 0.9em;
            opacity: 0.9;
        }
        
        .tabs {
            display: flex;
            background: #fff;
            border-bottom: 2px solid #e0e0e0;
            position: sticky;
            top: 0;
            z-index: 100;
        }
        
        .tab {
            flex: 1;
            padding: 15px 20px;
            text-align: center;
            cursor: pointer;
            border: none;
            background: none;
            font-size: 1em;
            font-weight: 600;
            color: #666;
            transition: all 0.3s;
        }
        
        .tab:hover {
            background: #f5f5f5;
        }
        
        .tab.active {
            color: #667eea;
            border-bottom: 3px solid #667eea;
        }
        
        .content {
            padding: 30px 20px;
        }
        
        .tab-content {
            display: none;
        }
        
        .tab-content.active {
            display: block;
        }
        
        section {
            margin-bottom: 30px;
        }
        
        h2 {
            color: #667eea;
            font-size: 1.5em;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e0e0e0;
        }
        
        h3 {
            color: #555;
            font-size: 1.2em;
            margin-top: 20px;
            margin-bottom: 10px;
        }
        
        p {
            margin-bottom: 15px;
            text-align: justify;
        }
        
        ul, ol {
            margin-left: 30px;
            margin-bottom: 15px;
        }
        
        li {
            margin-bottom: 8px;
        }
        
        .disclaimer {
            background: #fff3cd;
            border-left: 4px solid #ff9800;
            padding: 20px;
            margin: 30px 0;
        }
        
        .disclaimer h3 {
            color: #ff9800;
            margin-top: 0;
        }
        
        .important {
            background: #ffebee;
            border-left: 4px solid #f44336;
            padding: 15px;
            margin: 20px 0;
        }
        
        .contact {
            background: #e8f5e9;
            padding: 20px;
            border-radius: 8px;
            margin-top: 30px;
        }
        
        .contact h3 {
            color: #4CAF50;
            margin-bottom: 10px;
        }
        
        footer {
            text-align: center;
            padding: 20px;
            margin-top: 40px;
            border-top: 1px solid #e0e0e0;
            color: #7f8c8d;
            font-size: 0.9em;
        }
        
        @media (max-width: 768px) {
            header h1 {
                font-size: 1.5em;
            }
            
            .tabs {
                flex-direction: column;
            }
            
            h2 {
                font-size: 1.2em;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>CholeMetric</h1>
            <p>Legal Information & Policies</p>
        </header>

        <div class="tabs">
            <button class="tab active" onclick="showTab('privacy')">Privacy Policy</button>
            <button class="tab" onclick="showTab('terms')">Terms of Service</button>
        </div>

        <div class="content">
            <!-- PRIVACY POLICY TAB -->
            <div id="privacy" class="tab-content active">
                <section>
                    <h2>Privacy Policy</h2>
                    <p style="color: #7f8c8d; font-style: italic;">Last Updated: December 2025</p>
                </section>

                <section>
                    <h3>Introduction</h3>
                    <p>
                        CholeMetric ("we," "our," or "us") is committed to protecting your privacy. This Privacy Policy 
                        explains how we collect, use, disclose, and safeguard your information when you use our mobile 
                        application for gallbladder stone detection and analysis.
                    </p>
                </section>

                <section>
                    <h3>Information We Collect</h3>
                    <p><strong>Account Information:</strong></p>
                    <ul>
                        <li>Email address</li>
                        <li>Full name</li>
                        <li>Hospital or clinic affiliation</li>
                        <li>Medical specialization</li>
                        <li>Password (encrypted)</li>
                    </ul>

                    <p><strong>Medical Data:</strong></p>
                    <ul>
                        <li>CT scan images of gallbladder</li>
                        <li>Patient identifiers</li>
                        <li>Scan dates and results</li>
                        <li>AI analysis results</li>
                        <li>Radiologist notes</li>
                    </ul>
                </section>

                <section>
                    <h3>How We Use Your Information</h3>
                    <p>CholeMetric provides the following features:</p>
                    <ul>
                        <li>AI-powered gallbladder stone detection from CT scan images</li>
                        <li>To create and manage your user account</li>
                        <li>To securely store scan results</li>
                        <li>To improve our AI model (optional, with your consent)</li>
                        <li>To send important service updates</li>
                        <li>To protect against unauthorized access</li>
                    </ul>
                </section>

                <section>
                    <h3>Data Processing and Storage</h3>
                    <p><strong>On-Device Processing:</strong> All AI image analysis is performed on-device using TensorFlow Lite. Your medical images are processed locally and are not transmitted to external servers during analysis.</p>
                    
                    <p><strong>Server Storage:</strong> User account information and scan results are stored on our secure servers with industry-standard encryption.</p>
                </section>

                <section>
                    <h3>Federated Learning (Optional)</h3>
                    <ul>
                        <li>Opt-in only - disabled by default</li>
                        <li>Privacy-preserving - only anonymized updates computed</li>
                        <li>No images leave your device</li>
                        <li>No personal data included</li>
                        <li>Can opt-out at any time</li>
                    </ul>
                </section>

                <section>
                    <h3>Data Security</h3>
                    <ul>
                        <li>End-to-end encryption for data transmission</li>
                        <li>Secure password hashing (bcrypt)</li>
                        <li>Regular security audits</li>
                        <li>Access controls and authentication</li>
                        <li>HIPAA-compliant security measures</li>
                    </ul>
                </section>

                <section>
                    <h3>Your Rights</h3>
                    <ul>
                        <li>Access your personal data</li>
                        <li>Request correction of inaccurate information</li>
                        <li>Request deletion of your account and data</li>
                        <li>Export your scan data</li>
                        <li>Opt-out of federated learning</li>
                        <li>Withdraw consent for data processing</li>
                    </ul>
                </section>

                <div class="contact">
                    <h3>Contact Us</h3>
                    <p>For privacy-related questions or data requests:</p>
                    <p>
                        <strong>Email:</strong>maramakhila0549.sse@saveetha.com<br>
                        <strong>Institution:</strong>SIMATS UNIVERSITY
                    </p>
                </div>
            </div>

            <!-- TERMS OF SERVICE TAB -->
            <div id="terms" class="tab-content">
                <section>
                    <h2>Terms of Service</h2>
                    <p style="color: #7f8c8d; font-style: italic;">Last Updated: December 2025</p>
                </section>

                <div class="important">
                    <h3>⚠️ IMPORTANT MEDICAL DISCLAIMER</h3>
                    <p>
                        <strong>CholeMetric is a clinical decision support tool designed to ASSIST healthcare 
                        professionals, NOT to replace clinical judgment, professional diagnosis, or medical expertise.</strong>
                    </p>
                    <p>
                        All AI-generated analysis results must be independently verified by qualified medical 
                        professionals before making any clinical decisions.
                    </p>
                </div>

                <section>
                    <h3>1. Acceptance of Terms</h3>
                    <p>
                        By accessing and using CholeMetric, you accept and agree to be bound by these Terms of Service. 
                        If you do not agree to these Terms, you must not use this Service.
                    </p>
                </section>

                <section>
                    <h3>2. Eligibility</h3>
                    <p>This Service is intended exclusively for:</p>
                    <ul>
                        <li>Licensed medical doctors (MD, DO)</li>
                        <li>Board-certified radiologists</li>
                        <li>Qualified healthcare professionals</li>
                        <li>Medical residents under supervision</li>
                        <li>Authorized medical staff at accredited institutions</li>
                    </ul>
                </section>

                <section>
                    <h3>3. User Responsibilities</h3>
                    <p>You agree to:</p>
                    <ul>
                        <li>Provide accurate information during registration</li>
                        <li>Maintain confidentiality of login credentials</li>
                        <li>Use the app only for legitimate medical purposes</li>
                        <li>Comply with HIPAA and relevant regulations</li>
                        <li>Exercise independent professional judgment</li>
                        <li>Verify all AI results before clinical use</li>
                    </ul>
                </section>

                <section>
                    <h3>4. Prohibited Uses</h3>
                    <p>You may NOT:</p>
                    <ul>
                        <li>Provide diagnosis based solely on AI results</li>
                        <li>Share patient data with unauthorized parties</li>
                        <li>Reverse engineer the AI models or software</li>
                        <li>Copy or distribute the application</li>
                        <li>Use for competing services</li>
                        <li>Violate any applicable laws or regulations</li>
                    </ul>
                </section>

                <section>
                    <h3>5. Intellectual Property</h3>
                    <p>
                        All AI models, algorithms, and content are proprietary and protected by intellectual 
                        property laws. You may not copy, reproduce, or create derivative works.
                    </p>
                </section>

                <div class="disclaimer">
                    <h3>6. Disclaimer of Warranties</h3>
                    <p>
                        THE SERVICE IS PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND. We do not warrant:
                    </p>
                    <ul>
                        <li>Accuracy or completeness of AI analysis</li>
                        <li>Uninterrupted or error-free operation</li>
                        <li>Fitness for a particular purpose</li>
                    </ul>
                </div>

                <section>
                    <h3>7. Limitation of Liability</h3>
                    <p>
                        TO THE MAXIMUM EXTENT PERMITTED BY LAW, WE SHALL NOT BE LIABLE FOR:
                    </p>
                    <ul>
                        <li>Medical malpractice claims</li>
                        <li>Misdiagnosis or delayed diagnosis</li>
                        <li>Patient harm from reliance on AI results</li>
                        <li>Data loss or business interruption</li>
                        <li>Lost profits or revenue</li>
                    </ul>
                </section>

                <section>
                    <h3>8. Indemnification</h3>
                    <p>
                        You agree to indemnify CholeMetric from any claims arising from your use of the Service, 
                        violation of these Terms, or medical malpractice related to your professional practice.
                    </p>
                </section>

                <section>
                    <h3>9. Account Termination</h3>
                    <p>We may suspend or terminate your account if you:</p>
                    <ul>
                        <li>Violate these Terms</li>
                        <li>Engage in fraudulent activities</li>
                        <li>Pose a security risk</li>
                        <li>Abuse the Service</li>
                    </ul>
                </section>

                <section>
                    <h3>10. Changes to Terms</h3>
                    <p>
                        We may update these Terms at any time. Continued use of the Service after modifications 
                        constitutes acceptance of the updated Terms.
                    </p>
                </section>

                <div class="contact">
                    <h3>Contact Information</h3>
                    <p>For questions about these Terms:</p>
                    <p>
                        <strong>Email:</strong>maramakhila0549.sse@saveetha.com<br>
                        <strong>Institution:</strong>SIMATS UNIVERSITY 
                    </p>
                </div>
            </div>
        </div>

        <footer>
            <p>&copy; 2025 CholeMetric. All rights reserved.</p>
            <p>This application is intended for use by licensed medical professionals only.</p>
        </footer>
    </div>

    <script>
        function showTab(tabName) {
            // Hide all tab contents
            var contents = document.getElementsByClassName('tab-content');
            for (var i = 0; i < contents.length; i++) {
                contents[i].classList.remove('active');
            }
            
            // Remove active class from all tabs
            var tabs = document.getElementsByClassName('tab');
            for (var i = 0; i < tabs.length; i++) {
                tabs[i].classList.remove('active');
            }
            
            // Show selected tab content
            document.getElementById(tabName).classList.add('active');
            
            // Mark selected tab as active
            event.target.classList.add('active');
        }
    </script>
</body>
</html>
