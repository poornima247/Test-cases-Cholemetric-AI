# 🚀 How to Enable GitHub Pages from GitHub Actions

Your CI/CD workflow deploys to GitHub Pages. Before the workflow can publish:

## Step 1 — Enable GitHub Pages with "GitHub Actions" Source

> [!IMPORTANT]
> You must do this ONCE before the workflow will work.

1. **Open your GitHub repository** in a browser
2. Click **Settings** (top nav)
3. In the left sidebar → scroll down to **Pages**
4. Under **Build and deployment → Source**, change it from "Deploy from a branch" → **GitHub Actions**
5. Click **Save** ✅

That's it! No branch selection needed. The workflow does everything automatically.

---

## Step 2 — Add Required Permissions to Workflow

Your workflow already has these set (no action needed):
```yaml
permissions:
  contents: write
  pages: write
  id-token: write
```

---

## Step 3 — Trigger the Workflow

Push any commit to `main`:
```bash
git add .
git commit -m "Enable GitHub Pages CI/CD"
git push origin main
```

---

## What Happens After Each Push

```
push to main
     │
     ▼
┌─────────────────────────┐
│ Job 1: Deploy           │  ~ 2-3 min
│  ✅ Checkout            │
│  ✅ HTML Validation     │
│  ✅ Upload to Pages     │
│  ✅ Deploy live URL     │
└────────────┬────────────┘
             │ outputs: page_url
     ┌───────┴───────┐
     ▼               ▼
┌─────────┐   ┌──────────────┐
│ Job 2   │   │ Job 3        │
│ Selenium│   │ k6 Load Test │
│ E2E     │   │ 100 VUs×1min │
│ 280+    │   │              │
│ tests   │   │              │
└────┬────┘   └──────┬───────┘
     │               │
     ▼               ▼
📊 HTML Report    ⚡ RPS / Response Time
📊 Excel Report   📊 P95 Latency
📤 Artifacts      📤 Artifacts
📋 Step Summary   📋 Step Summary
```

---

## Viewing Results

| What | Where |
|------|-------|
| Live site | `https://<username>.github.io/<repo>/` |
| Test reports | Actions tab → your run → Artifacts |
| Step summary | Actions tab → your run → scroll down |
| Pass/fail badges | GitHub Step Summary table |

---

## Troubleshooting

### ❌ "Error: RequestedEntityTooLargeException" on deploy
- Split large assets out of `frontend/` directory

### ❌ "HTTP Status 404" on deployment wait
- Increase the wait loop retries in Stage 10 (currently 12 × 5s = 60s)

### ❌ Selenium tests all fail with "BASE_URL not found"
- Ensure `frontend/login_form.html` exists (the deploy must succeed first)

### ❌ "No such file: surefire-reports"
- This means Maven compilation failed — check Java source for syntax errors

### ❌ k6 not found
- The workflow installs k6 from GitHub releases automatically

---

## Local Testing

```bash
cd web-automation

# Run all tests locally (requires Chrome)
mvn test -DBASE_URL=https://your-username.github.io/your-repo/ -DBROWSER=chrome -DHEADLESS=false

# Generate reports after running
python scripts/generate_web_reports.py

# View HTML report
start "Test Results/HTML/execution-report.html"
```
