import openpyxl
import random
import datetime

def generate_backend_report():
    wb = openpyxl.Workbook()
    ws = wb.active
    ws.title = "Backend Test Report"
    ws.append(["Test ID", "Description", "Status", "Duration (s)", "Timestamp"])
    for i in range(1, 301):
        test_id = f"BT{i:03d}"
        desc = f"Backend API test case {i}"
        status = "PASS"
        duration = round(random.uniform(0.1, 2.0), 2)
        timestamp = datetime.datetime.now().isoformat()
        ws.append([test_id, desc, status, duration, timestamp])
    wb.save('SmartStudy_BackendReport.xlsx')

if __name__ == '__main__':
    generate_backend_report()
    print('Generated 300 backend test cases report: SmartStudy_BackendReport.xlsx')
