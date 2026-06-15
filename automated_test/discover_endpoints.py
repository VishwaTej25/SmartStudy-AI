import json
import os
import sys
from urllib.parse import urljoin

import requests


def load_input():
    path = os.path.join(os.path.dirname(__file__), "input.json")
    if not os.path.exists(path):
        print("Missing automated_test/input.json — please create based on input.json.example")
        sys.exit(2)
    with open(path, "r") as f:
        return json.load(f)


def try_openapi(base):
    candidates = ["/v3/api-docs", "/swagger.json", "/openapi.json", "/openapi.json"]
    for p in candidates:
        url = urljoin(base, p)
        try:
            r = requests.get(url, timeout=6)
            if r.status_code == 200:
                print(f"Found OpenAPI at {url}")
                return r.json()
        except Exception:
            continue
    return None


def build_paths_from_openapi(spec):
    paths = []
    for path, methods in spec.get("paths", {}).items():
        for m in methods.keys():
            paths.append({"path": path, "method": m.upper()})
    return paths


def main():
    cfg = load_input()
    base = cfg.get("baseUrl")
    if not base:
        print("baseUrl missing in input.json")
        sys.exit(2)

    spec = try_openapi(base)
    endpoints = []
    if spec:
        endpoints = build_paths_from_openapi(spec)
    else:
        print("No OpenAPI/Swagger found at common locations.")

    out_path = os.path.join(os.path.dirname(__file__), "discovered_endpoints.json")
    with open(out_path, "w") as f:
        json.dump({"baseUrl": base, "endpoints": endpoints}, f, indent=2)

    print(f"Discovered {len(endpoints)} endpoints (saved to {out_path}).")
    if endpoints:
        for e in endpoints:
            print(f" - {e['method']} {e['path']}")
    else:
        print("No endpoints discovered automatically. You can populate discovered_endpoints.json manually.")


if __name__ == '__main__':
    main()
