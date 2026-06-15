import time
import json
from urllib.parse import urljoin

import requests


def safe_request(session, method, url, headers=None, params=None, timeout=10):
    start = time.time()
    try:
        r = session.request(method, url, headers=headers, params=params, timeout=timeout)
        elapsed = int((time.time() - start) * 1000)
        return {"status": r.status_code, "time_ms": elapsed, "body": r.text}
    except Exception as e:
        elapsed = int((time.time() - start) * 1000)
        return {"status": None, "time_ms": elapsed, "body": str(e)}


def test_auth_bypass(session, base, endpoint, token_name, tokens):
    url = urljoin(base, endpoint["path"])
    recs = []
    # No auth
    r = safe_request(session, endpoint["method"], url)
    recs.append({
        "endpoint": endpoint["path"],
        "method": endpoint["method"],
        "role": "none",
        "status": r["status"],
        "expected_status": "requires-auth",
        "finding": r["status"] and 200 <= r["status"] < 300,
        "severity": "high" if r["status"] and 200 <= r["status"] < 300 else "info",
        "response_time_ms": r["time_ms"],
        "test_category": "auth_bypass",
        "note": "No auth provided",
        "timestamp": time.time()
    })

    # If token provided for role, test with token
    if token_name in tokens and tokens[token_name]:
        headers = {"Authorization": f"Bearer {tokens[token_name]}"}
        r2 = safe_request(session, endpoint["method"], url, headers=headers)
        recs.append({
            "endpoint": endpoint["path"],
            "method": endpoint["method"],
            "role": token_name,
            "status": r2["status"],
            "expected_status": "200/allowed",
            "finding": False,
            "severity": "info",
            "response_time_ms": r2["time_ms"],
            "test_category": "auth_validation",
            "note": "Checked with provided token",
            "timestamp": time.time()
        })

    return recs
