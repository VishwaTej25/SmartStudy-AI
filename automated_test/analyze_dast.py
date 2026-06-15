#!/usr/bin/env python3
import json
import pandas as pd

# Load JSON
with open('dast_report.json') as f:
    data = json.load(f)

print(f"Total tests: {len(data)}")
print(f"Tests with status codes: {len([d for d in data if d['status'] is not None])}")
print(f"Tests with null status: {len([d for d in data if d['status'] is None])}")

if data:
    print("\nFirst record structure:")
    print(json.dumps(data[0], indent=2))
    
    print("\n\nSample record WITH status:")
    for d in data:
        if d['status'] is not None:
            print(json.dumps(d, indent=2))
            break
    
    # Check for findings
    findings = [d for d in data if d.get('finding') == True]
    print(f"\n\nTotal findings: {len(findings)}")
    for f in findings:
        print(f"  - {f['endpoint']} ({f['test_category']}): {f.get('note', 'N/A')}")
