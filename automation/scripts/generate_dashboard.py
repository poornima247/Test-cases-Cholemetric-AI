#!/usr/bin/env python3
"""
generate_dashboard.py — Cholemetric AI GitHub Pages Dashboard Generator
Creates an interactive dashboard.html and trends.html for GitHub Pages.
"""
import os
import sys
import json
import argparse
import datetime

OUTPUT_DIR  = "Test Results"
HTML_DIR    = os.path.join(OUTPUT_DIR, "HTML")
JSON_DIR    = os.path.join(OUTPUT_DIR, "JSON")
os.makedirs(HTML_DIR, exist_ok=True)

def load_results(json_path="Test Results/JSON/execution-results.json"):
    try:
        with open(json_path) as f:
            data = json.load(f)
        return data.get("summary", {}), data.get("results", [])
    except Exception:
        return {}, []

def generate_dashboard(build="", commit="", branch="", pages_url=""):
    summary, results = load_results()
    total   = summary.get("total", len(results))
    passed  = summary.get("passed", sum(1 for r in results if r.get("status") == "PASS"))
    failed  = summary.get("failed", sum(1 for r in results if r.get("status") == "FAIL"))
    skipped = summary.get("skipped", sum(1 for r in results if r.get("status") == "SKIP"))
    pass_rate = summary.get("pass_rate", round(passed / total * 100, 1) if total > 0 else 0)
    exec_date = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S UTC")

    # Module breakdown
    from collections import defaultdict
    module_data = defaultdict(lambda: {"pass": 0, "fail": 0, "skip": 0})
    for r in results:
        mod = r.get("module", "Unknown")
        st = r.get("status", "SKIP")
        module_data[mod][st.lower() if st.lower() in ("pass","fail","skip") else "skip"] += 1

    module_labels_js = json.dumps(list(module_data.keys()))
    module_pass_js   = json.dumps([v["pass"] for v in module_data.values()])
    module_fail_js   = json.dumps([v["fail"] for v in module_data.values()])

    # Failed tests list
    failed_rows = ""
    for r in [x for x in results if x.get("status") == "FAIL"][:50]:
        reason = r.get("failure_reason", "Unknown")[:120]
        failed_rows += f"""
        <tr>
          <td class="mono">{r.get('test_id','')}</td>
          <td><span class="badge blue">{r.get('module','')}</span></td>
          <td class="small">{r.get('test_name','')[:60]}</td>
          <td class="red small">{reason}</td>
        </tr>"""

    status_color = "green" if pass_rate >= 95 else ("orange" if pass_rate >= 80 else "red")
    status_icon  = "✅" if pass_rate >= 95 else ("⚠️" if pass_rate >= 80 else "❌")
    status_text  = "PASSING" if pass_rate >= 95 else ("PARTIAL" if pass_rate >= 80 else "FAILING")

    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cholemetric AI — E2E Dashboard</title>
  <meta name="description" content="Cholemetric AI Android E2E Automation Dashboard — Build {build}">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
  <style>
    :root {{
      --bg:#0a0c18; --card:#111427; --card2:#161929; --border:#1e2240;
      --blue:#4f6ef7; --green:#00e676; --red:#ff5252; --yellow:#ffd740;
      --purple:#7c4dff; --cyan:#00bcd4; --text:#dde1f5; --muted:#6b7099;
    }}
    *{{margin:0;padding:0;box-sizing:border-box}}
    body{{font-family:'Inter',sans-serif;background:var(--bg);color:var(--text);}}
    .topbar{{
      background:linear-gradient(135deg,#050816,#0d1a4a);
      padding:20px 40px; display:flex; justify-content:space-between; align-items:center;
      border-bottom:2px solid var(--blue); position:sticky; top:0; z-index:100;
    }}
    .topbar h1{{font-size:20px;font-weight:700;}}
    .topbar h1 span{{color:#90caf9;}}
    .topbar .build-badge{{
      background:var(--blue); color:#fff; padding:6px 16px; border-radius:20px;
      font-size:12px; font-weight:600;
    }}
    .status-banner{{
      padding:16px 40px; text-align:center; font-size:14px; font-weight:600;
      background:{f"#0d2d0d" if status_color == "green" else ("#2d1500" if status_color == "orange" else "#2d0d0d")};
      border-bottom:2px solid {f"var(--green)" if status_color == "green" else ("var(--yellow)" if status_color == "orange" else "var(--red)")};
      color:{f"var(--green)" if status_color == "green" else ("var(--yellow)" if status_color == "orange" else "var(--red)")};
    }}
    .meta{{display:flex;gap:24px;padding:16px 40px;background:var(--card2);flex-wrap:wrap;border-bottom:1px solid var(--border);font-size:12px;color:var(--muted);}}
    .meta span strong{{color:var(--text);}}
    .kpi-grid{{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:16px;padding:28px 40px;}}
    .kpi{{
      background:var(--card); border:1px solid var(--border); border-radius:14px;
      padding:20px; text-align:center; transition:transform 0.2s;
    }}
    .kpi:hover{{transform:translateY(-3px);box-shadow:0 8px 32px rgba(0,0,0,0.5);}}
    .kpi .k-label{{font-size:11px;color:var(--muted);text-transform:uppercase;letter-spacing:0.8px;margin-bottom:12px;}}
    .kpi .k-value{{font-size:40px;font-weight:800;}}
    .kpi .k-sub{{font-size:11px;color:var(--muted);margin-top:4px;}}
    .k-total{{color:var(--blue);}} .k-pass{{color:var(--green);}} .k-fail{{color:var(--red);}}
    .k-skip{{color:var(--yellow);}} .k-rate{{color:var(--purple);}}
    .progress-section{{padding:0 40px 28px;}}
    .prog-card{{background:var(--card);border:1px solid var(--border);border-radius:14px;padding:20px 24px;}}
    .prog-header{{display:flex;justify-content:space-between;margin-bottom:12px;font-size:13px;}}
    .prog-bar{{height:16px;background:#1a2155;border-radius:999px;overflow:hidden;position:relative;}}
    .prog-fill{{
      height:100%;border-radius:999px;
      background:linear-gradient(90deg,var(--green) 0%,#69f0ae 100%);
      width:{pass_rate}%;transition:width 1.2s ease;
    }}
    .prog-fail{{
      position:absolute;right:0;top:0;height:100%;
      background:linear-gradient(90deg,var(--red),#ff8a80);
      width:{round(failed/total*100,1) if total>0 else 0}%;border-radius:0 999px 999px 0;
    }}
    .charts-row{{display:flex;gap:20px;padding:0 40px 28px;flex-wrap:wrap;}}
    .chart-card{{
      background:var(--card);border:1px solid var(--border);border-radius:14px;
      padding:20px;flex:1;min-width:280px;
    }}
    .chart-card h3{{font-size:12px;color:var(--muted);text-transform:uppercase;letter-spacing:0.5px;margin-bottom:16px;}}
    .section{{padding:0 40px 36px;}}
    .section h2{{font-size:16px;font-weight:600;margin-bottom:16px;}}
    table{{width:100%;border-collapse:collapse;background:var(--card);border-radius:10px;overflow:hidden;}}
    th{{padding:10px 14px;font-size:11px;text-transform:uppercase;letter-spacing:0.5px;color:var(--muted);background:var(--card2);text-align:left;}}
    td{{padding:10px 14px;font-size:12px;border-top:1px solid var(--border);}}
    .mono{{font-family:monospace;color:#90caf9;font-size:11px;}}
    .small{{font-size:11px;}}
    .red{{color:var(--red);}}
    .badge{{padding:3px 8px;border-radius:4px;font-size:10px;font-weight:500;}}
    .badge.blue{{background:#1a2155;color:#90caf9;}}
    .links-bar{{
      display:flex;gap:12px;padding:20px 40px;flex-wrap:wrap;
      background:var(--card2);border-top:1px solid var(--border);
    }}
    .link-btn{{
      padding:8px 20px;border-radius:8px;border:1px solid var(--border);
      color:var(--text);text-decoration:none;font-size:13px;
      background:var(--card);transition:all 0.2s;display:inline-flex;align-items:center;gap:6px;
    }}
    .link-btn:hover{{background:var(--blue);border-color:var(--blue);}}
    .footer{{text-align:center;padding:20px 40px;color:var(--muted);font-size:11px;border-top:1px solid var(--border);}}
  </style>
</head>
<body>
  <div class="topbar">
    <h1>🤖 Cholemetric AI — <span>E2E Dashboard</span></h1>
    <span class="build-badge">Build #{build}</span>
  </div>

  <div class="status-banner">
    {status_icon} Pipeline Status: <strong>{status_text}</strong> — Pass Rate: {pass_rate}% ({passed}/{total} tests)
  </div>

  <div class="meta">
    <span>📅 <strong>{exec_date}</strong></span>
    <span>🌿 Branch: <strong>{branch}</strong></span>
    <span>🔖 Commit: <strong>{commit[:10] if commit else 'N/A'}</strong></span>
    <span>📱 Device: <strong>Android Emulator — Nexus 6 (API 29)</strong></span>
    <span>📦 App: <strong>com.cholemetric.app v1.0</strong></span>
    <span>⚙️ Framework: <strong>Appium 2.x + Java 17 + TestNG 7.8</strong></span>
  </div>

  <div class="kpi-grid">
    <div class="kpi"><div class="k-label">Total Tests</div><div class="k-value k-total">{total}</div><div class="k-sub">20 Modules</div></div>
    <div class="kpi"><div class="k-label">Passed</div><div class="k-value k-pass">{passed}</div><div class="k-sub">✅ Success</div></div>
    <div class="kpi"><div class="k-label">Failed</div><div class="k-value k-fail">{failed}</div><div class="k-sub">❌ Need Fix</div></div>
    <div class="kpi"><div class="k-label">Skipped</div><div class="k-value k-skip">{skipped}</div><div class="k-sub">⏭️ Pending</div></div>
    <div class="kpi"><div class="k-label">Pass Rate</div><div class="k-value k-rate">{pass_rate}%</div><div class="k-sub">Target: ≥95%</div></div>
  </div>

  <div class="progress-section">
    <div class="prog-card">
      <div class="prog-header">
        <span style="font-weight:600">Overall Pass Rate</span>
        <span><strong style="color:var(--green)">{pass_rate}%</strong> passed / <strong style="color:var(--red)">{round(failed/total*100,1) if total>0 else 0}%</strong> failed</span>
      </div>
      <div class="prog-bar">
        <div class="prog-fill"></div>
        <div class="prog-fail"></div>
      </div>
    </div>
  </div>

  <div class="charts-row">
    <div class="chart-card" style="max-width:300px">
      <h3>Result Distribution</h3>
      <canvas id="pie" height="200"></canvas>
    </div>
    <div class="chart-card">
      <h3>Tests by Module</h3>
      <canvas id="bar" height="200"></canvas>
    </div>
  </div>

  {'<div class="section"><h2>❌ Failed Tests</h2><table><thead><tr><th>Test ID</th><th>Module</th><th>Test Name</th><th>Failure Reason</th></tr></thead><tbody>' + failed_rows + '</tbody></table></div>' if failed_rows else '<div class="section"><h2 style="color:var(--green)">🎉 No Failed Tests!</h2></div>'}

  <div class="links-bar">
    <a class="link-btn" href="execution-report.html">📊 Full Report</a>
    <a class="link-btn" href="{pages_url}/reports/history/">📚 History</a>
    <a class="link-btn" href="{pages_url}">🏠 Home</a>
    <a class="link-btn" href="summary.md">📝 Summary</a>
  </div>

  <div class="footer">
    Cholemetric AI — Android E2E Dashboard | Appium 2.x + Java 17 + TestNG 7.8 | Build #{build} | {exec_date}
  </div>

  <script>
    new Chart(document.getElementById('pie'), {{
      type:'doughnut',
      data:{{
        labels:['Passed','Failed','Skipped'],
        datasets:[{{data:[{passed},{failed},{skipped}],backgroundColor:['#00e676','#ff5252','#ffd740'],borderWidth:0}}]
      }},
      options:{{plugins:{{legend:{{labels:{{color:'#dde1f5'}}}}}},cutout:'65%'}}
    }});
    new Chart(document.getElementById('bar'), {{
      type:'bar',
      data:{{
        labels:{module_labels_js},
        datasets:[
          {{label:'Passed',data:{module_pass_js},backgroundColor:'rgba(0,230,118,0.8)'}},
          {{label:'Failed',data:{module_fail_js},backgroundColor:'rgba(255,82,82,0.8)'}}
        ]
      }},
      options:{{
        plugins:{{legend:{{labels:{{color:'#dde1f5'}}}}}},
        scales:{{
          x:{{ticks:{{color:'#6b7099',maxRotation:45,font:{{size:10}}}},grid:{{color:'#1e2240'}}}},
          y:{{ticks:{{color:'#6b7099'}},grid:{{color:'#1e2240'}}}}
        }},
        responsive:true
      }}
    }});
  </script>
</body>
</html>"""

    path = os.path.join(HTML_DIR, "dashboard.html")
    with open(path, "w", encoding="utf-8") as f:
        f.write(html)
    print(f"✅ Dashboard: {path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--build", default="")
    parser.add_argument("--commit", default="")
    parser.add_argument("--branch", default="")
    parser.add_argument("--pages-url", default="https://poornima247.github.io/Test-cases-Cholemetric-AI")
    args = parser.parse_args()
    generate_dashboard(build=args.build, commit=args.commit, branch=args.branch, pages_url=args.pages_url)
    print("✅ Dashboard generation complete")
