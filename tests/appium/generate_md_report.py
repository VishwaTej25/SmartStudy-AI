import os
import sys

# Ensure tests/appium is in sys.path so we can import local modules when run from root
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from generate_test_suite_report import generate_markdown_report

if __name__ == "__main__":
    if len(sys.argv) > 1:
        artifact_report_path = sys.argv[1]
    else:
        artifact_report_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "reports", "appium_test_report.md")
    generate_markdown_report(artifact_report_path)
