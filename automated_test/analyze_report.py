import json
import os
from collections import Counter, defaultdict

import pandas as pd


def load(path):
    with open(path) as f:
        return json.load(f)


def main():
    root = os.path.dirname(__file__)
    report_path = os.path.join(root, "report.json")
    if not os.path.exists(report_path):
        print("report.json not found — run run_tests_full.py first")
        return

    records = load(report_path)
    total = len(records)
    findings = [r for r in records if r.get("finding")]
    num_findings = len(findings)

    by_category = Counter(r.get("test_category") for r in records)
    by_severity = Counter(r.get("severity") for r in records)
    by_role = Counter(r.get("role") for r in records)

    # top failing endpoints
    fail_counter = Counter()
    for r in findings:
        key = (r.get("endpoint"), r.get("method"), r.get("test_category"))
        fail_counter[key] += 1

    top_failures = []
    for (endpoint, method, cat), cnt in fail_counter.most_common(50):
        top_failures.append({"endpoint": endpoint, "method": method, "category": cat, "fail_count": cnt})

    summary = {
        "total_tests": total,
        "total_findings": num_findings,
        "by_category": dict(by_category),
        "by_severity": dict(by_severity),
        "by_role": dict(by_role),
        "top_failures": top_failures,
    }

    out_json = os.path.join(root, "report_summary.json")
    with open(out_json, "w") as f:
        json.dump(summary, f, indent=2)

    # write Excel: sheet1=summary, sheet2=failures, sheet3=records (truncated)
    out_xlsx = os.path.join(root, "report_summary.xlsx")
    with pd.ExcelWriter(out_xlsx, engine="openpyxl") as writer:
        # summary frame
        df_sum = pd.DataFrame([
            ["total_tests", total],
            ["total_findings", num_findings]
        ], columns=["metric", "value"])
        df_sum.to_excel(writer, sheet_name="summary", index=False)

        # by category
        pd.DataFrame(list(summary["by_category"].items()), columns=["category", "count"]).to_excel(writer, sheet_name="by_category", index=False)
        pd.DataFrame(list(summary["by_severity"].items()), columns=["severity", "count"]).to_excel(writer, sheet_name="by_severity", index=False)
        pd.DataFrame(list(summary["by_role"].items()), columns=["role", "count"]).to_excel(writer, sheet_name="by_role", index=False)

        # top failures
        pd.DataFrame(summary["top_failures"]).to_excel(writer, sheet_name="top_failures", index=False)

        # failures detail
        if findings:
            pd.DataFrame(findings).to_excel(writer, sheet_name="failures", index=False)

        # write full records (may be large) — include first 1000 rows
        pd.DataFrame(records[:1000]).to_excel(writer, sheet_name="records_sample", index=False)

    print(f"Wrote summary to {out_json} and {out_xlsx}")


if __name__ == '__main__':
    main()
