import os
from appium import webdriver
from appium.options.android import UiAutomator2Options

# Import local modules
from test_config import APPIUM_SERVER_URL, DESIRED_CAPABILITIES
from excel_reporter import ExcelReporter
from test_smartstudy_e2e import SmartStudyE2ETests


def main():
    print("Initializing Appium frontend E2E test suite...")
    
    reports_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "reports")
    reporter = ExcelReporter(report_dir=reports_dir)
    
    driver = None
    try:
        options = UiAutomator2Options()
        options.load_capabilities(DESIRED_CAPABILITIES)

        print(f"Connecting to Appium server at {APPIUM_SERVER_URL}...")
        driver = webdriver.Remote(APPIUM_SERVER_URL, options=options)
        
        print("Driver initialized successfully.")
        
        e2e_tests = SmartStudyE2ETests(driver, reporter)
        e2e_tests.run_all_tests()
        
    except Exception as e:
        print(f"A critical error occurred during test execution: {e}")
        reporter.add_result("Test Suite Execution", "FAIL", str(e), 0)
    finally:
        if driver:
            print("Quitting driver...")
            driver.quit()
            
        print("Generating Excel report...")
        report_path = reporter.generate_report()
        print(f"Test suite finished. Report available at {report_path}")


if __name__ == "__main__":
    main()
