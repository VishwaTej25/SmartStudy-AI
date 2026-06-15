import json
import os
from datetime import datetime

import pandas as pd


def write_reports(records):
    out_json = os.path.join(os.path.dirname(__file__), "report.json")
    with open(out_json, "w") as f:
        json.dump(records, f, indent=2, default=str)

    if records:
        df = pd.DataFrame(records)
        out_xlsx = os.path.join(os.path.dirname(__file__), "report.xlsx")
        df.to_excel(out_xlsx, index=False, engine="openpyxl")
        print(f"Wrote report.xlsx ({len(records)} records)")
    else:
        print("No records to write")


if __name__ == '__main__':
    path = os.path.join(os.path.dirname(__file__), "report.json")
    if os.path.exists(path):
        with open(path) as f:
            recs = json.load(f)
    else:
        recs = []
    write_reports(recs)
