import os
import pandas as pd
from datetime import datetime

class ExcelReporter:
    def __init__(self, report_dir="reports"):
        self.report_dir = report_dir
        self.results = []
        
        if not os.path.exists(self.report_dir):
            os.makedirs(self.report_dir)
            
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.report_path = os.path.join(self.report_dir, f"SmartStudy_Test_Report_{timestamp}.xlsx")
        
    def add_result(self, step_name, status, message="", execution_time_sec=0):
        self.results.append({
            "Timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "Step/Test Name": step_name,
            "Status": status,
            "Message": message,
            "Execution Time (s)": round(execution_time_sec, 2)
        })
        
    def generate_report(self):
        if not self.results:
            print("No test results to report.")
            return
            
        df = pd.DataFrame(self.results)
        
        # Using pandas to write to Excel (requires openpyxl)
        df.to_excel(self.report_path, index=False, engine='openpyxl')
        print(f"Excel report generated successfully at: {self.report_path}")
        return self.report_path
