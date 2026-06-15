import os

# Configuration for Appium Android Tests
APPIUM_SERVER_URL = "http://127.0.0.1:4723"

# Update these capabilities based on your specific Android Emulator or Physical Device
DESIRED_CAPABILITIES = {
    "platformName": "Android",
    "appium:automationName": "UiAutomator2",
    "appium:deviceName": "Android Emulator",  # Change this to your emulator's name if needed
    "appium:appPackage": "com.example.smartstudy",
    "appium:appActivity": "com.example.smartstudy.MainActivity",
    "appium:autoGrantPermissions": True,
    "appium:newCommandTimeout": 3600,
    "appium:noReset": True  # Set to False if you want to clear app data between runs
}

# Optional frontend user credentials for login validation.
# If these are not provided, the login flow will be detected but not executed.
TEST_USER_EMAIL = os.getenv("TEST_USER_EMAIL", "")
TEST_USER_PASSWORD = os.getenv("TEST_USER_PASSWORD", "")
TEST_USER_FULLNAME = os.getenv("TEST_USER_FULLNAME", "Appium Test User")
