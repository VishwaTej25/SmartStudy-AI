Automated DAST helper scripts

Overview
- Use these scripts to discover API endpoints (OpenAPI) and run non-destructive DAST checks.

Prerequisites
- Python 3.8+
- Install dependencies: `pip install -r automated_test/requirements.txt`

Usage
- Create `automated_test/input.json` with the following shape:
  {
    "baseUrl": "https://api.example.com",
    "admin": "<ADMIN_TOKEN>",
    "user": "<USER_TOKEN>"
  }

- To discover endpoints (this will write `automated_test/discovered_endpoints.json`):
  `python automated_test/discover_endpoints.py`

- The discovery step will print the endpoints and exit. Inspect and confirm.

- To run tests after confirming discovery:
  `python automated_test/run_all.py --confirm`

Output
- `automated_test/report.json` — one record per test
- `automated_test/report.xlsx` — Excel summary of tests

Security
- Do not check `input.json` with secrets into source control. The runner reads tokens at runtime and does not print them.
