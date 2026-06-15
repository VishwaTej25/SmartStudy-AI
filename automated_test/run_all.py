import json
import os
import sys
import time
from argparse import ArgumentParser

import requests

from tests_templates import test_auth_bypass
from report_writer import write_reports


def load_input(path):
    if not os.path.exists(path):
        print("Missing input.json — create automated_test/input.json from automated_test/input.json.example")
        sys.exit(2)
    with open(path) as f:
        return json.load(f)


def load_discovered(path):
    if not os.path.exists(path):
        print("Missing discovered_endpoints.json — run discover_endpoints.py first")
        sys.exit(2)
    with open(path) as f:
        return json.load(f)


def main():
    parser = ArgumentParser()
    parser.add_argument("--confirm", action="store_true", help="Confirm to run tests after discovery")
    args = parser.parse_args()

    base = os.path.join(os.path.dirname(__file__), "input.json")
    cfg = load_input(base)
    discovered = load_discovered(os.path.join(os.path.dirname(__file__), "discovered_endpoints.json"))

    endpoints = discovered.get("endpoints", [])
    print(f"Loaded {len(endpoints)} endpoints from discovery.")
    for e in endpoints[:200]:
        print(f" - {e['method']} {e['path']}")

    if not args.confirm:
        print("Run aborted. Rerun with --confirm to execute tests.")
        sys.exit(0)

    tokens = {k: v for k, v in cfg.items() if k != "baseUrl"}

    session = requests.Session()
    records = []
    # throttle parameters
    delay = 0.2

    for ep in endpoints[:125]:
        # Skip actuator/health/metrics
        if any(s in ep["path"].lower() for s in ["/health", "/actuator", "/metrics"]):
            continue
        # Only non-destructive checks: GET or HEAD preferred; if spec gives other method, still attempt GET
        method = ep.get("method", "GET")
        if method not in ("GET", "HEAD", "POST"):
            method = "GET"

        # Auth bypass test
        recs = test_auth_bypass(session, cfg["baseUrl"], {"path": ep["path"], "method": method}, "user", tokens)
        records.extend(recs)
        time.sleep(delay)

    write_reports(records)
    print("Done. Reports written to automated_test/report.json and report.xlsx")


if __name__ == '__main__':
    main()
