const fs = require('fs');
const path = require('path');

console.log('============================================================');
console.log('  Building Cholemetric AI Web Application for Production');
console.log('============================================================');

const distDir = path.join(__dirname, 'dist');
const reportsDir = path.join(distDir, 'reports', 'latest');

fs.mkdirSync(reportsDir, { recursive: true });

// Copy all static frontend files to dist/
const filesToCopy = [
  'index.html',
  'welcome.html',
  'login_form.html',
  'signup.html',
  'dashboard.html',
  'new_analysis.html',
  'scan_report.html',
  'patient_history.html',
  'settings.html',
  'edit_profile.html',
  'faq.html',
  'forgot_password.html',
  'splash.html',
  'sync_manager.js',
  'logo.png',
  'favicon.png',
  'sample_ct_scan.png',
  'annotated_sample_ct_scan.png'
];

filesToCopy.forEach(file => {
  const srcPath = path.join(__dirname, file);
  const destPath = path.join(distDir, file);
  if (fs.existsSync(srcPath)) {
    fs.copyFileSync(srcPath, destPath);
    console.log(`[BUILD] Copied ${file} -> dist/`);
  }
});

// Copy test automation reports into dist/reports/latest/ and dist/
const reportSrcDirs = [
  path.join(__dirname, 'reports', 'latest'),
  path.join(__dirname, 'automation', 'reports', 'latest')
];

reportSrcDirs.forEach(srcDir => {
  if (fs.existsSync(srcDir)) {
    const reportFiles = fs.readdirSync(srcDir);
    reportFiles.forEach(file => {
      const srcFile = path.join(srcDir, file);
      const stat = fs.statSync(srcFile);
      if (stat.isFile()) {
        fs.copyFileSync(srcFile, path.join(reportsDir, file));
      }
    });
  }
});

// Copy key report files directly to dist/ root for convenient URLs
const mirrorFiles = ['execution-report.html', 'dashboard.html', 'Execution_Report.xlsx', 'execution-results.json', 'summary.md'];
mirrorFiles.forEach(file => {
  const src = path.join(reportsDir, file);
  const dest = path.join(distDir, file);
  if (fs.existsSync(src)) {
    fs.copyFileSync(src, dest);
  }
});

console.log('============================================================');
console.log('  [SUCCESS] Cholemetric AI Production Build Complete!');
console.log('  Dist Directory: ' + distDir);
console.log('============================================================');
