import json
import os

JSON_PATH = "Test Results/JSON/execution-results.json"
SUMMARY_FILE = os.environ.get("GITHUB_STEP_SUMMARY", "github_step_summary.md")

if not os.path.exists(JSON_PATH):
    print(f"JSON file not found at {JSON_PATH}")
    exit(0)

with open(JSON_PATH, "r") as f:
    data = json.load(f)

summary = data.get("summary", {})
results = data.get("results", [])

content = "## 📊 Appium Test Execution Summary\n\n"

content += "| Metric | Count |\n"
content += "|--------|-------|\n"
content += f"| **Total Tests** | {summary.get('total', 0)} |\n"
content += f"| 🟢 **Passed** | {summary.get('passed', 0)} |\n"
content += f"| 🔴 **Failed** | {summary.get('failed', 0)} |\n"
content += f"| 🟡 **Skipped** | {summary.get('skipped', 0)} |\n"
content += f"| **Pass Rate** | {summary.get('pass_rate', 0)}% |\n\n"

content += "### 🔴 Failed Tests\n\n"
failed_tests = [r for r in results if r["status"] == "FAIL"]
if failed_tests:
    for r in failed_tests:
        content += f"- ✗ **{r['test_id']}**: `{r['name']}` ({r['module']})\n"
else:
    content += "No failed tests! 🎉\n"

content += "\n### 🟢 Passed Tests\n\n"
passed_tests = [r for r in results if r["status"] == "PASS"]
if passed_tests:
    content += "<details><summary>Click to view passed tests</summary>\n\n"
    for r in passed_tests:
        content += f"- ✓ **{r['test_id']}**: `{r['name']}`\n"
    content += "\n</details>\n"

with open(SUMMARY_FILE, "a") as f:
    f.write(content)

print(f"Wrote summary to {SUMMARY_FILE}")
