import json
import os
import time
from urllib.parse import urljoin

import requests
import pandas as pd


def load_json(path):
    with open(path) as f:
        return json.load(f)


def expected_access(path):
    protected_keywords = ["profile","settings","payments","orders","enroll","enrollments","admin","reports","files","uploads","download","auth","login"]
    for k in protected_keywords:
        if k in path.lower():
            return "requires-auth"
    return "public"


def run_request(session, method, url, headers=None, timeout=10):
    start = time.time()
    try:
        r = session.request(method, url, headers=headers or {}, timeout=timeout)
        elapsed = int((time.time() - start) * 1000)
        return r.status_code, elapsed, len(r.content)
    except Exception as e:
        elapsed = int((time.time() - start) * 1000)
        return None, elapsed, 0


def main():
    root = os.path.dirname(__file__)
    input_path = os.path.join(root, "input.json")
    discovered_path = os.path.join(root, "discovered_endpoints_expanded.json")

    if not os.path.exists(input_path):
        print("Missing automated_test/input.json")
        return
    if not os.path.exists(discovered_path):
        print("Missing discovered_endpoints_expanded.json — run expand_endpoints.py first")
        return

    cfg = load_json(input_path)
    data = load_json(discovered_path)
    base = data.get("baseUrl") or cfg.get("baseUrl")
    endpoints = data.get("endpoints", [])

    # tokens: all keys except baseUrl
    tokens = {k: v for k, v in cfg.items() if k != "baseUrl"}

    session = requests.Session()
    records = []

    max_tests = max(125, len(endpoints))
    count = 0
    for ep in endpoints:
        if count >= max_tests:
            break
        path = ep.get("path")
        method = ep.get("method", "GET")
        full = urljoin(base.rstrip('/') + '/', path.lstrip('/'))

        exp = expected_access(path)

        # unauthenticated request
        status, rt, size = run_request(session, method, full, headers=None)
        finding = False
        note = ""
        if exp == "requires-auth" and status and 200 <= status < 300:
            finding = True
            note = "Auth required but endpoint accessible without auth"

        records.append({
            "endpoint": path,
            "method": method,
            "role": "none",
            "status": status,
            "expected_status": exp,
            "finding": finding,
            "severity": "high" if finding else "info",
            "response_time_ms": rt,
            "response_size": size,
            "test_category": "auth_bypass",
            "note": note,
            "timestamp": time.time()
        })

        # authenticated requests for each token
        for role, token in tokens.items():
            headers = {"Authorization": f"Bearer {token}"}
            status2, rt2, size2 = run_request(session, method, full, headers=headers)
            # simple pass/fail: if requires-auth -> expect 2xx; if public -> 2xx or 401 ok
            finding2 = False
            note2 = ""
            if exp == "requires-auth" and not (status2 and 200 <= status2 < 300):
                finding2 = True
                note2 = "Authenticated request did not return success for protected endpoint"

            records.append({
                "endpoint": path,
                "method": method,
                "role": role,
                "status": status2,
                "expected_status": "200/allowed" if exp == "requires-auth" else "200/any",
                "finding": finding2,
                "severity": "medium" if finding2 else "info",
                "response_time_ms": rt2,
                "response_size": size2,
                "test_category": "auth_validation",
                "note": note2,
                "timestamp": time.time()
            })

        count += 1
        time.sleep(0.15)

    # write report.json and report.xlsx
    out_json = os.path.join(root, "report.json")
    with open(out_json, "w") as f:
        json.dump(records, f, indent=2)

    if records:
        df = pd.DataFrame(records)
        out_xlsx = os.path.join(root, "report.xlsx")
        df.to_excel(out_xlsx, index=False, engine="openpyxl")
        print(f"Wrote {len(records)} records to report.json and report.xlsx")
    else:
        print("No records produced")


if __name__ == '__main__':
    main()
