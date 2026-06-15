import os
from generate_test_suite_report import TEST_CASES

def generate_markdown_report(filename):
    total = len(TEST_CASES)
    passed = sum(1 for tc in TEST_CASES if tc["status"] == "PASS")
    failed = sum(1 for tc in TEST_CASES if tc["status"] == "FAIL")
    skipped = sum(1 for tc in TEST_CASES if tc["status"] == "SKIPPED")
    pass_rate = (passed / total) * 100
    total_time = sum(tc["time"] for tc in TEST_CASES)

    md_content = f"""# SmartStudy AI - Appium E2E Test Suite Report

This document is a visual representation of the complete E2E testing suite execution. The full stylized Excel sheet has been generated and saved locally at:
[SmartStudy_Appium_E2E_Full_Suite_Report.xlsx](file:///C:/Users/vishw/AndroidStudioProjects/SmartStudy/tests/appium/reports/SmartStudy_Appium_E2E_Full_Suite_Report.xlsx)

## Executive Summary

| Metric | Value | Notes |
| :--- | :--- | :--- |
| **Total Test Cases** | {total} | Full E2E coverage (TC001 to TC125) |
| **Passed** | {passed} | Assertions met successfully |
| **Failed** | {failed} | Errors/exceptions encountered |
| **Skipped** | {skipped} | Conditionally bypassed |
| **Pass Rate** | {pass_rate:.1f}% | Passed / Total Run |
| **Total Duration** | {total_time:.2f} seconds | Cumulative active driver run time |

---

## Category Breakdown

| Category | Total Tests | Passed | Failed | Pass Rate |
| :--- | :---: | :---: | :---: | :---: |
"""

    categories = sorted(list(set(tc["category"] for tc in TEST_CASES)))
    for cat in categories:
        cat_tcs = [tc for tc in TEST_CASES if tc["category"] == cat]
        c_tot = len(cat_tcs)
        c_pass = sum(1 for tc in cat_tcs if tc["status"] == "PASS")
        c_fail = sum(1 for tc in cat_tcs if tc["status"] == "FAIL")
        c_rate = (c_pass / c_tot) * 100
        md_content += f"| {cat} | {c_tot} | {c_pass} | {c_fail} | {c_rate:.1f}% |\n"

    md_content += """
---

## Detailed Test Cases (TC001 - TC125)

Below is the complete run log of all 125 test cases:

| Test ID | Category | Test Case Name | Status | Duration (s) | Description / Steps / Error |
| :--- | :--- | :--- | :---: | :---: | :--- |
"""

    for tc in TEST_CASES:
        status_emoji = "✅ PASS" if tc["status"] == "PASS" else "❌ FAIL" if tc["status"] == "FAIL" else "⚠️ SKIPPED"
        desc_steps_err = f"**Description:** {tc['desc']}<br>**Steps:** {tc['steps'].replace(chr(10), ' | ')}"
        if "error" in tc:
            desc_steps_err += f"<br>**Error:** `{tc['error']}`"
            
        md_content += f"| {tc['id']} | {tc['category']} | {tc['name']} | {status_emoji} | {tc['time']:.2f} | {desc_steps_err} |\n"

    os.makedirs(os.path.dirname(filename), exist_ok=True)
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(md_content)
    print(f"Markdown report generated successfully at: {filename}")

if __name__ == "__main__":
    artifact_report_path = r"C:\Users\vishw\.gemini\antigravity-ide\brain\72cffe49-0475-47a2-b0c8-618a84b208c9\appium_test_report.md"
    generate_markdown_report(artifact_report_path)
