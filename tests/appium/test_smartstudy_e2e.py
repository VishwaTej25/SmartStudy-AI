import os
import time
from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from test_config import TEST_USER_EMAIL, TEST_USER_PASSWORD

class SmartStudyE2ETests:
    def __init__(self, driver, reporter):
        self.driver = driver
        self.reporter = reporter
        self.wait = WebDriverWait(self.driver, 15)

    def run_all_tests(self):
        print("Starting Appium frontend E2E tests...")
        self.test_app_launch()
        self.test_login_screen_elements()
        self.test_login_with_credentials()
        self.test_navigation_and_course_flow()
        self.test_logout_flow()

    def find_by_text(self, text, timeout=10):
        xpath = f"//*[contains(@text, '{text}') or contains(@content-desc, '{text}')]"
        return WebDriverWait(self.driver, timeout).until(
            EC.presence_of_element_located((AppiumBy.XPATH, xpath))
        )

    def find_input_fields(self):
        return self.driver.find_elements(AppiumBy.CLASS_NAME, "android.widget.EditText")

    def test_app_launch(self):
        start_time = time.time()
        step_name = "App Launch & Initialization"
        try:
            time.sleep(5)
            app_state = self.driver.query_app_state("com.example.smartstudy")

            if app_state == 4:
                self.reporter.add_result(step_name, "PASS", "App launched and is in foreground", time.time() - start_time)
            else:
                self.reporter.add_result(step_name, "FAIL", f"App not in foreground. State: {app_state}", time.time() - start_time)
        except Exception as e:
            self.reporter.add_result(step_name, "FAIL", str(e), time.time() - start_time)
            print(f"Test Failed: {step_name} - {e}")

    def test_login_screen_elements(self):
        start_time = time.time()
        step_name = "Login Screen UI Elements"
        try:
            self.find_by_text("Email")
            self.find_by_text("Password")
            self.find_by_text("Login")
            self.find_by_text("Forgot Password")
            self.reporter.add_result(step_name, "PASS", "Login screen elements are visible", time.time() - start_time)
        except Exception as e:
            self.reporter.add_result(step_name, "FAIL", str(e), time.time() - start_time)
            print(f"Test Failed: {step_name} - {e}")

    def test_login_with_credentials(self):
        start_time = time.time()
        step_name = "Login With Credentials"

        if not TEST_USER_EMAIL or not TEST_USER_PASSWORD:
            self.reporter.add_result(step_name, "SKIPPED", "No login credentials provided via environment variables", 0)
            return

        try:
            text_fields = self.find_input_fields()
            if len(text_fields) < 2:
                raise NoSuchElementException("Expected at least 2 input fields on login screen")

            text_fields[0].clear()
            text_fields[0].send_keys(TEST_USER_EMAIL)
            text_fields[1].clear()
            text_fields[1].send_keys(TEST_USER_PASSWORD)

            login_button = self.find_by_text("Login")
            login_button.click()

            self.wait.until(EC.presence_of_element_located((AppiumBy.XPATH, "//*[contains(@text, 'Smart Study') or contains(@text, 'Home')]")))
            self.reporter.add_result(step_name, "PASS", "Login flow completed successfully", time.time() - start_time)
        except Exception as e:
            self.reporter.add_result(step_name, "FAIL", str(e), time.time() - start_time)
            print(f"Test Failed: {step_name} - {e}")

    def test_navigation_and_course_flow(self):
        start_time = time.time()
        step_name = "Navigation and Course Flow"
        try:
            drawer_button = self.find_by_text("☰")
            drawer_button.click()

            courses_item = self.find_by_text("Courses")
            courses_item.click()
            self.wait.until(EC.presence_of_element_located((AppiumBy.XPATH, "//*[contains(@text, 'Java Programming') or contains(@text, 'Courses')]")))

            learn_button = self.find_by_text("Learn")
            learn_button.click()
            self.wait.until(EC.presence_of_element_located((AppiumBy.XPATH, "//*[contains(@text, 'OOPs Concepts') or contains(@text, 'Video Lectures')]")))

            self.reporter.add_result(step_name, "PASS", "Navigation and course selection flow completed", time.time() - start_time)
        except Exception as e:
            self.reporter.add_result(step_name, "FAIL", str(e), time.time() - start_time)
            print(f"Test Failed: {step_name} - {e}")

    def test_logout_flow(self):
        start_time = time.time()
        step_name = "Logout Flow"
        try:
            drawer_button = self.find_by_text("☰")
            drawer_button.click()

            logout_item = self.find_by_text("Logout")
            logout_item.click()

            self.wait.until(EC.presence_of_element_located((AppiumBy.XPATH, "//*[contains(@text, 'Login') or contains(@text, 'Welcome Back')]")))
            self.reporter.add_result(step_name, "PASS", "Logout returned to login screen", time.time() - start_time)
        except Exception as e:
            self.reporter.add_result(step_name, "FAIL", str(e), time.time() - start_time)
            print(f"Test Failed: {step_name} - {e}")
