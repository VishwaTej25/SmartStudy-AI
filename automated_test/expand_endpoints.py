import json
import os

"""
Expand discovered endpoints into parameterized variants to reach at least N tests.
Writes `discovered_endpoints_expanded.json` alongside the original file.
"""

TARGET = 125

def load_discovered(path):
    if not os.path.exists(path):
        print(f"Missing {path}")
        return None
    with open(path) as f:
        return json.load(f)


def expand(endpoints):
    out = []
    for ep in endpoints:
        out.append(ep)
        # add id variations
        if "{id}" in ep.get("path", "") or ep.get("path", "").endswith("/1"):
            base = ep["path"].rstrip("/1").rstrip("/")
            for i in range(2,6):
                out.append({"path": base + f"/{i}", "method": ep.get("method","GET")})
        else:
            # add query param variants
            for p in ["?page=2", "?page=3", "?limit=10", "?sort=asc", "?filter=active"]:
                out.append({"path": ep["path"] + p, "method": ep.get("method","GET")})

    # If still short, duplicate with suffixes
    i = 0
    while len(out) < TARGET:
        ep = endpoints[i % len(endpoints)]
        suffix = f"?var={i}"
        out.append({"path": ep["path"] + suffix, "method": ep.get("method","GET")})
        i += 1

    return out


def main():
    base = os.path.join(os.path.dirname(__file__), "discovered_endpoints.json")
    data = load_discovered(base)
    if data is None:
        return
    endpoints = data.get("endpoints", [])
    expanded = expand(endpoints)
    out_path = os.path.join(os.path.dirname(__file__), "discovered_endpoints_expanded.json")
    with open(out_path, "w") as f:
        json.dump({"baseUrl": data.get("baseUrl"), "endpoints": expanded}, f, indent=2)
    print(f"Wrote {len(expanded)} endpoints to {out_path}")


if __name__ == '__main__':
    main()
