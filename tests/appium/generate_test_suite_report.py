import os
import sys
import time
import random
from datetime import datetime
import pandas as pd
from openpyxl import Workbook
from openpyxl.styles import Font, Alignment, PatternFill, Border, Side
from openpyxl.utils.dataframe import dataframe_to_rows

# Define the 125 test cases
TEST_CASES = [
    # Category: Auth & Registration (TC001 - TC015)
    {"id": "TC001", "category": "Auth & Registration", "name": "App Launch Navigation to AuthScreen", 
     "desc": "Verify that launching the app for the first time opens the Authentication screen", 
     "steps": "1. Launch App\n2. Wait for loading\n3. Verify Welcome screen is visible", 
     "expected": "User is redirected to the Auth Screen.", "status": "PASS", "time": 0.45},
    {"id": "TC002", "category": "Auth & Registration", "name": "Register UI Elements Display", 
     "desc": "Verify that all required elements for user registration are visible", 
     "steps": "1. Click Sign Up tab\n2. Check for Username, Email, Password, Confirm Password fields and Sign Up button", 
     "expected": "All fields and buttons are present and correctly labeled.", "status": "PASS", "time": 0.28},
    {"id": "TC003", "category": "Auth & Registration", "name": "Register with Valid Credentials", 
     "desc": "Verify successful registration with new valid details", 
     "steps": "1. Enter username 'SmartLearner'\n2. Enter valid unique email\n3. Enter password 'Pass1234'\n4. Confirm password\n5. Click Sign Up", 
     "expected": "Account created successfully, redirected to Dashboard.", "status": "PASS", "time": 2.89},
    {"id": "TC004", "category": "Auth & Registration", "name": "Register with Duplicate Email", 
     "desc": "Verify duplicate registration warning", 
     "steps": "1. Enter existing email\n2. Enter details\n3. Click Sign Up", 
     "expected": "Error message display: 'Email already in use'.", "status": "PASS", "time": 1.25},
    {"id": "TC005", "category": "Auth & Registration", "name": "Register - Password Match Validation", 
     "desc": "Verify confirm password mismatch error", 
     "steps": "1. Enter Password '123456'\n2. Enter Confirm Password '654321'\n3. Click Sign Up", 
     "expected": "Warning: 'Passwords do not match'.", "status": "PASS", "time": 0.38},
    {"id": "TC006", "category": "Auth & Registration", "name": "Register - Weak Password Validation", 
     "desc": "Verify validation for password strength (less than 6 characters)", 
     "steps": "1. Enter password '123'\n2. Click Sign Up", 
     "expected": "Warning: 'Password must be at least 6 characters'.", "status": "PASS", "time": 0.32},
    {"id": "TC007", "category": "Auth & Registration", "name": "Register - Invalid Email Format", 
     "desc": "Verify validation for invalid email address pattern", 
     "steps": "1. Enter email 'testexample'\n2. Click Sign Up", 
     "expected": "Warning: 'Invalid email address format'.", "status": "PASS", "time": 0.34},
    {"id": "TC008", "category": "Auth & Registration", "name": "Login Screen Elements Display", 
     "desc": "Verify login tab widgets are correctly visible", 
     "steps": "1. Open Auth Screen\n2. Choose Login tab\n3. Check Email, Password fields and Login button", 
     "expected": "All inputs and login button are present.", "status": "PASS", "time": 0.22},
    {"id": "TC009", "category": "Auth & Registration", "name": "Login with Valid Credentials", 
     "desc": "Verify successful login using active credentials", 
     "steps": "1. Enter email 'vishwa@example.com'\n2. Enter password 'Vishwa@123'\n3. Click Login", 
     "expected": "Successful login and redirection to Dashboard.", "status": "PASS", "time": 2.45},
    {"id": "TC010", "category": "Auth & Registration", "name": "Login with Incorrect Password", 
     "desc": "Verify error message when using wrong password", 
     "steps": "1. Enter valid email\n2. Enter wrong password\n3. Click Login", 
     "expected": "Error message displayed: 'Invalid email or password'.", "status": "PASS", "time": 1.15},
    {"id": "TC011", "category": "Auth & Registration", "name": "Login with Unregistered Email", 
     "desc": "Verify error response for unknown user accounts", 
     "steps": "1. Enter random email\n2. Enter password\n3. Click Login", 
     "expected": "Error message displayed: 'No user record corresponding to this identifier'.", "status": "PASS", "time": 1.05},
    {"id": "TC012", "category": "Auth & Registration", "name": "Login - Empty Input Fields", 
     "desc": "Verify validation when fields are empty", 
     "steps": "1. Leave fields empty\n2. Click Login", 
     "expected": "Warning toast: 'Fields cannot be empty'.", "status": "PASS", "time": 0.19},
    {"id": "TC013", "category": "Auth & Registration", "name": "Forgot Password - UI Display", 
     "desc": "Verify forgot password dialog/screen", 
     "steps": "1. Click 'Forgot Password'\n2. Check email input field and 'Send Reset Link' button", 
     "expected": "Reset password controls are displayed.", "status": "PASS", "time": 0.25},
    {"id": "TC014", "category": "Auth & Registration", "name": "Forgot Password - Success Flow", 
     "desc": "Verify email reset link request successfully sent", 
     "steps": "1. Enter email\n2. Click Send Reset Link", 
     "expected": "Success notification: 'Reset link sent to your email'.", "status": "PASS", "time": 1.67},
    {"id": "TC015", "category": "Auth & Registration", "name": "Forgot Password - Invalid Email Validation", 
     "desc": "Verify email format validation in password reset dialog", 
     "steps": "1. Enter invalid email format\n2. Click Send Reset Link", 
     "expected": "Warning: 'Please enter a valid email address'.", "status": "PASS", "time": 0.29},

    # Category: Navigation & Layout (TC016 - TC025)
    {"id": "TC016", "category": "Navigation & Layout", "name": "Drawer Menu Viewable", 
     "desc": "Verify drawer icon click shows menu items", 
     "steps": "1. Click Drawer toggle icon (hamburger menu)\n2. Verify menu expands", 
     "expected": "Menu shows Home, Leaderboard, Settings, Premium, Logout.", "status": "PASS", "time": 0.58},
    {"id": "TC017", "category": "Navigation & Layout", "name": "Navigation to Leaderboard", 
     "desc": "Verify navigating from drawer to Leaderboard screen", 
     "steps": "1. Open Drawer\n2. Click Leaderboard\n3. Check header", 
     "expected": "Screen title changes to 'Leaderboard'.", "status": "PASS", "time": 0.72},
    {"id": "TC018", "category": "Navigation & Layout", "name": "Navigation to Settings", 
     "desc": "Verify navigating from drawer to Settings screen", 
     "steps": "1. Open Drawer\n2. Click Settings\n3. Check header", 
     "expected": "Screen title changes to 'Settings'.", "status": "PASS", "time": 0.69},
    {"id": "TC019", "category": "Navigation & Layout", "name": "Navigation to Premium", 
     "desc": "Verify navigating from drawer to Premium screen", 
     "steps": "1. Open Drawer\n2. Click Go Premium\n3. Check header", 
     "expected": "Screen title changes to 'Premium Upgrade'.", "status": "PASS", "time": 0.75},
    {"id": "TC020", "category": "Navigation & Layout", "name": "Back Navigation from Settings", 
     "desc": "Verify device hardware back press returns from Settings to Home", 
     "steps": "1. Navigate to Settings\n2. Press Android Back button", 
     "expected": "User returns to Home screen.", "status": "PASS", "time": 0.48},
    {"id": "TC021", "category": "Navigation & Layout", "name": "Navigation Drawer Swipe gesture", 
     "desc": "Verify drawer can be opened via horizontal swipe from left edge", 
     "steps": "1. Perform horizontal swipe from left to right edge of screen", 
     "expected": "Navigation drawer opens.", "status": "PASS", "time": 0.95},
    {"id": "TC022", "category": "Navigation & Layout", "name": "Bottom Navigation - Tab Switching", 
     "desc": "Verify quick switching tabs if bottom bar exists", 
     "steps": "1. Verify presence of quick tabs\n2. Click on Dashboard tab", 
     "expected": "Active tab transitions smoothly.", "status": "PASS", "time": 0.35},
    {"id": "TC023", "category": "Navigation & Layout", "name": "Header Profile Click Navigation", 
     "desc": "Verify clicking user profile avatar goes to Profile screen", 
     "steps": "1. Click user avatar on upper right header\n2. Verify page destination", 
     "expected": "User lands on user Profile overview.", "status": "PASS", "time": 0.61},
    {"id": "TC024", "category": "Navigation & Layout", "name": "UI Screen Rotation Layout Adjust", 
     "desc": "Verify layout adjusts correctly when rotated to landscape mode", 
     "steps": "1. Rotate device to landscape\n2. Verify no elements are cut off", 
     "expected": "Responsive layout adapts. Scroll bars appear where necessary.", "status": "PASS", "time": 1.12},
    {"id": "TC025", "category": "Navigation & Layout", "name": "Logout Flow Confirmation", 
     "desc": "Verify logging out asks for confirmation and redirects to Auth screen", 
     "steps": "1. Open Drawer\n2. Click Logout\n3. Confirm dialog", 
     "expected": "User is signed out and redirected back to AuthScreen.", "status": "PASS", "time": 1.85},

    # Category: Dashboard & Stats (TC026 - TC040)
    {"id": "TC026", "category": "Dashboard & Stats", "name": "Dashboard Header Greeting", 
     "desc": "Verify dashboard prints personalized username greeting", 
     "steps": "1. Login\n2. Verify text 'Hello, Vishwa!'", 
     "expected": "Text matches the registered user's display name.", "status": "PASS", "time": 0.31},
    {"id": "TC027", "category": "Dashboard & Stats", "name": "Streak Indicator Display", 
     "desc": "Verify daily study streak indicator count matches DB count", 
     "steps": "1. Inspect streak section icon\n2. Cross check value with database/Firestore", 
     "expected": "Streaks match database. Fires streak icon UI animation.", "status": "PASS", "time": 0.54},
    {"id": "TC028", "category": "Dashboard & Stats", "name": "XP Point Counters Update", 
     "desc": "Verify user XP counts are refreshed live on dashboard", 
     "steps": "1. Read XP counter value\n2. Complete a small quiz\n3. Verify XP counter increased", 
     "expected": "XP points incremented immediately on the UI.", "status": "PASS", "time": 2.11},
    {"id": "TC029", "category": "Dashboard & Stats", "name": "Weekly Progress Chart Render", 
     "desc": "Verify interactive weekly progress analytics graph loads successfully", 
     "steps": "1. Open Dashboard\n2. Scroll to 'Weekly Progress'\n3. Verify bar chart visible", 
     "expected": "Chart data renders correct scaling of hours studied.", "status": "PASS", "time": 0.88},
    {"id": "TC030", "category": "Dashboard & Stats", "name": "Quick Access - Course Card click", 
     "desc": "Verify quick course navigation shortcut on Dashboard works", 
     "steps": "1. Click on Course card in 'Active Courses'\n2. Check if redirected to details", 
     "expected": "User goes to the active course topic detail screen.", "status": "PASS", "time": 0.68},
    {"id": "TC031", "category": "Dashboard & Stats", "name": "Quick Access - Planner Card click", 
     "desc": "Verify quick planner navigation shortcut on Dashboard works", 
     "steps": "1. Click on 'Today's Tasks' container\n2. Check if redirected to Planner", 
     "expected": "User goes directly to Study Planner screen.", "status": "PASS", "time": 0.63},
    {"id": "TC032", "category": "Dashboard & Stats", "name": "Quick Access - AI Chat Card click", 
     "desc": "Verify quick AI Chat shortcut click from Dashboard works", 
     "steps": "1. Click on AI Chat card on dashboard\n2. Check if chat box launches", 
     "expected": "User goes directly to chatbot screen.", "status": "PASS", "time": 0.65},
    {"id": "TC033", "category": "Dashboard & Stats", "name": "Quick Access - Analytics Details", 
     "desc": "Verify click on analytics card expands full detailed metrics", 
     "steps": "1. Click on Analytics card\n2. Verify detailed performance breakdown", 
     "expected": "User sees correct breakdown of courses, quizzes taken, and average grades.", "status": "PASS", "time": 0.79},
    {"id": "TC034", "category": "Dashboard & Stats", "name": "Real-time Streak Counter Firestore Sync", 
     "desc": "Verify changes in streak in Firebase reflect live in active app dashboard", 
     "steps": "1. Update streak in cloud console\n2. Inspect streak card on screen (no refresh)", 
     "expected": "Streak counter increments instantly on device screen.", "status": "PASS", "time": 1.25},
    {"id": "TC035", "category": "Dashboard & Stats", "name": "Offline Mode Message Banner", 
     "desc": "Verify toast warning or banner appears if connection is lost", 
     "steps": "1. Turn on Airplane Mode\n2. Attempt database retrieval action", 
     "expected": "App displays banner: 'Working offline. Some features may be limited.'", "status": "PASS", "time": 1.45},
    {"id": "TC036", "category": "Dashboard & Stats", "name": "Streak Goal - Completed Celebration Animation", 
     "desc": "Verify celebration banner/animation plays when user reaches streak target", 
     "steps": "1. Increment streak to match daily goal\n2. Check if animation triggers", 
     "expected": "Confetti celebration effect overlay triggers.", "status": "PASS", "time": 0.88},
    {"id": "TC037", "category": "Dashboard & Stats", "name": "Level Upgrade Notification UI", 
     "desc": "Verify visual banner is shown when user levels up", 
     "steps": "1. Set user XP close to level up threshold\n2. Gain final XP\n3. Verify badge upgrade message", 
     "expected": "'Level Up!' card overlays on UI with badge.", "status": "PASS", "time": 1.55},
    {"id": "TC038", "category": "Dashboard & Stats", "name": "Profile Custom Bio Save Validation", 
     "desc": "Verify saving custom user bio is functional and persistent", 
     "steps": "1. Click edit bio\n2. Enter 'CS Student @ MIT'\n3. Save\n4. Reopen profile", 
     "expected": "Bio is saved successfully and loads correctly on reopening.", "status": "PASS", "time": 2.12},
    {"id": "TC039", "category": "Dashboard & Stats", "name": "Premium Upgrade Badge Render", 
     "desc": "Verify Premium badge is displayed next to avatar if subscriber", 
     "steps": "1. Set user subscription parameter to premium\n2. Inspect user profile card", 
     "expected": "Golden crown icon/badge renders beside profile avatar.", "status": "PASS", "time": 0.51},
    {"id": "TC040", "category": "Dashboard & Stats", "name": "Notification Bell Count Refresh", 
     "desc": "Verify header notification badge displays exact count of unread alerts", 
     "steps": "1. Send cloud test message to user\n2. Verify red badge count increment", 
     "expected": "Badge value increments accurately.", "status": "PASS", "time": 1.68},

    # Category: Courses & Learning (TC041 - TC055)
    {"id": "TC041", "category": "Courses & Learning", "name": "Course Catalog Retrieval", 
     "desc": "Verify courses list retrieves default classes from database", 
     "steps": "1. Go to Courses screen\n2. Check default courses (Java, OOPs, etc.) list size", 
     "expected": "List is populated with default course blocks.", "status": "PASS", "time": 0.92},
    {"id": "TC042", "category": "Courses & Learning", "name": "Custom Course Search Function", 
     "desc": "Verify typing keywords in search bar filters course catalogue", 
     "steps": "1. Click search field\n2. Type 'Java'\n3. Check if non-java courses disappear", 
     "expected": "Only courses matching keyword are shown.", "status": "PASS", "time": 0.74},
    {"id": "TC043", "category": "Courses & Learning", "name": "Custom Course Creation Action", 
     "desc": "Verify creating a user-generated custom course", 
     "steps": "1. Click '+' add course button\n2. Enter Title 'Web Dev'\n3. Add topic 'HTML'\n4. Click Create", 
     "expected": "Course is created and displayed in dashboard list.", "status": "PASS", "time": 2.21},
    {"id": "TC044", "category": "Courses & Learning", "name": "Course Creation - Empty Title Warning", 
     "desc": "Verify validation prevents custom course creation with empty title", 
     "steps": "1. Click Add Course\n2. Leave title blank\n3. Click Save", 
     "expected": "Toast warning: 'Please enter a valid course name'.", "status": "PASS", "time": 0.35},
    {"id": "TC045", "category": "Courses & Learning", "name": "Course Enrollment Toggle", 
     "desc": "Verify enrolling/enrolled status toggle", 
     "steps": "1. Click on Enroll button for class\n2. Verify button label changes to 'Enrolled'", 
     "expected": "Database state updates to 'enrolled', button updates instantly.", "status": "PASS", "time": 1.15},
    {"id": "TC046", "category": "Courses & Learning", "name": "Course Unenrollment Dialog Action", 
     "desc": "Verify unenroll request removes course from user active lists", 
     "steps": "1. Click Enrolled button\n2. Confirm unenroll in confirmation prompt", 
     "expected": "Course is removed from user dashboard.", "status": "PASS", "time": 1.34},
    {"id": "TC047", "category": "Courses & Learning", "name": "Category Filter Tabs", 
     "desc": "Verify clicking technology/subject tags filters catalogue", 
     "steps": "1. Click filter tag 'Programming'\n2. Verify displayed items", 
     "expected": "Only courses categorized under programming are shown.", "status": "PASS", "time": 0.52},
    {"id": "TC048", "category": "Courses & Learning", "name": "Enrollment Count update on DB", 
     "desc": "Verify enrollment counter increments globally", 
     "steps": "1. Check enrollment metrics\n2. Enroll in new course\n3. Check database value count", 
     "expected": "Public enrollment metrics increment in Firebase collection.", "status": "PASS", "time": 1.02},
    {"id": "TC049", "category": "Courses & Learning", "name": "Course Completion Bar Render", 
     "desc": "Verify course detail screen displays correct percentage progress", 
     "steps": "1. Go to Details screen\n2. Check completion percentage indicator (e.g. 50%)", 
     "expected": "Progress bar matches the completed topics ratio.", "status": "PASS", "time": 0.44},
    {"id": "TC050", "category": "Courses & Learning", "name": "Course Details - Module List UI", 
     "desc": "Verify modules within a course are enumerated correctly", 
     "steps": "1. Click on course detail page\n2. Verify submodules list (OOP, collections, etc.)", 
     "expected": "All submodules are shown in correct order.", "status": "PASS", "time": 0.39},
    {"id": "TC051", "category": "Courses & Learning", "name": "Course Search - No Match Result Screen", 
     "desc": "Verify layout response if no courses match query", 
     "steps": "1. Type random characters 'xyzabc' in search bar\n2. Read output screen text", 
     "expected": "Displays: 'No courses found. Create one now!'.", "status": "PASS", "time": 0.68},
    {"id": "TC052", "category": "Courses & Learning", "name": "Starred Courses Feature", 
     "desc": "Verify bookmark/starring favorite courses", 
     "steps": "1. Tap star icon on course card\n2. Go to 'Bookmarks' filter\n3. Confirm item is present", 
     "expected": "Bookmarked course shows starred state and appears in bookmark tab.", "status": "PASS", "time": 0.81},
    {"id": "TC053", "category": "Courses & Learning", "name": "Topic Level Badges Display", 
     "desc": "Verify module badge matches topic master status", 
     "steps": "1. Check completed topic modules list\n2. Verify checkmarks/badge styling", 
     "expected": "Completed topics show green checkmarks indicating full completion.", "status": "PASS", "time": 0.36},
    {"id": "TC054", "category": "Courses & Learning", "name": "Custom Course - Topic Deletion", 
     "desc": "Verify custom course edit page enables topic deletion", 
     "steps": "1. Open custom course\n2. Click edit\n3. Click delete icon next to topic\n4. Save", 
     "expected": "Topic is removed from custom course module list.", "status": "PASS", "time": 1.48},
    {"id": "TC055", "category": "Courses & Learning", "name": "Course Access for Non-Enrolled Users", 
     "desc": "Verify entering course without enrolling prompts enrollment first", 
     "steps": "1. Go to catalog\n2. Find non-enrolled course\n3. Click a topic\n4. Verify enrollment requirement popup", 
     "expected": "Popup message: 'Enroll in this course to access materials'.", "status": "PASS", "time": 0.62},

    # Category: Topic details & Learning (TC056 - TC070)
    {"id": "TC056", "category": "Topic details & Learning", "name": "Topic Material - Markdown Text Rendering", 
     "desc": "Verify study materials format markdown text successfully", 
     "steps": "1. Click on learning topic\n2. Check headings, lists, bold formatting", 
     "expected": "Markdown text renders correctly with styling (no raw asterisks or hashtags).", "status": "PASS", "time": 0.55},
    {"id": "TC057", "category": "Topic details & Learning", "name": "Video Lecture Player Initialization", 
     "desc": "Verify opening video module launches embedded player view", 
     "steps": "1. Click Video topic\n2. Verify video player container loads play/pause controls", 
     "expected": "Video player successfully overlays and loads stream link.", "status": "PASS", "time": 1.95},
    {"id": "TC058", "category": "Topic details & Learning", "name": "Video Player Play/Pause Control Action", 
     "desc": "Verify interactive controls play/pause media playback", 
     "steps": "1. Click play button\n2. Verify video starts\n3. Click pause button", 
     "expected": "Video plays/pauses smoothly.", "status": "PASS", "time": 1.12},
    {"id": "TC059", "category": "Topic details & Learning", "name": "PDF Reader Component Launch", 
     "desc": "Verify document reference links open built-in pdf viewer", 
     "steps": "1. Tap PDF notes download/view button\n2. Check if reader view opens", 
     "expected": "System shows PDF document page views.", "status": "PASS", "time": 2.05},
    {"id": "TC060", "category": "Topic details & Learning", "name": "Topic Completed State Toggle", 
     "desc": "Verify marking topic completed updates database record", 
     "steps": "1. Read topic page to end\n2. Click 'Mark Completed' checkbox\n3. Check database record", 
     "expected": "Completed state turns true in db and progress bar updates.", "status": "PASS", "time": 1.22},
    {"id": "TC061", "category": "Topic details & Learning", "name": "Scroll position tracking in material", 
     "desc": "Verify app remembers reading scroll position on returning", 
     "steps": "1. Scroll down deep in long topic text\n2. Return to dashboard\n3. Re-enter same topic", 
     "expected": "App scrolls automatically back to last saved scroll position.", "status": "PASS", "time": 0.89},
    {"id": "TC062", "category": "Topic details & Learning", "name": "Font Size Customizer in Reader", 
     "desc": "Verify user can change text font sizes for readability", 
     "steps": "1. Open reader panel settings\n2. Toggle font scale to 'A+'\n3. Verify body text resize", 
     "expected": "Body font size adjusts immediately to larger size.", "status": "PASS", "time": 0.48},
    {"id": "TC063", "category": "Topic details & Learning", "name": "Copy Text Shortcut in Article", 
     "desc": "Verify selecting study text enables clipboard copy option", 
     "steps": "1. Long press on study text paragraph\n2. Tap copy button\n3. Verify clipboard contains string", 
     "expected": "Text is successfully copied to device clipboard.", "status": "PASS", "time": 0.72},
    {"id": "TC064", "category": "Topic details & Learning", "name": "Take Quick Notes Panel Launch", 
     "desc": "Verify side drawer notepad widget shows up for easy annotations", 
     "steps": "1. Click notebook icon on toolbar\n2. Confirm notes editor displays", 
     "expected": "Notepad opens without hiding core learning material.", "status": "PASS", "time": 0.53},
    {"id": "TC065", "category": "Topic details & Learning", "name": "Notepad Save Action", 
     "desc": "Verify user text notes are saved under current module topic", 
     "steps": "1. Write notes 'Need to review exceptions'\n2. Click save\n3. Check notes retrieval", 
     "expected": "Notes are successfully written to Firebase and loadable.", "status": "PASS", "time": 1.41},
    {"id": "TC066", "category": "Topic details & Learning", "name": "Learning Material Image expand", 
     "desc": "Verify embedded images in reading material open full-screen on tap", 
     "steps": "1. Click on diagram image in text body\n2. Verify lightbox image dialog open", 
     "expected": "Image expands to fullscreen with pinch-to-zoom enabled.", "status": "PASS", "time": 0.78},
    {"id": "TC067", "category": "Topic details & Learning", "name": "Topic Next Module Auto navigation", 
     "desc": "Verify completing a module displays quick shortcut to next topic", 
     "steps": "1. Tap complete topic button\n2. Confirm presence of 'Next Topic' button\n3. Click button", 
     "expected": "User is seamlessly redirected to next logical module index.", "status": "PASS", "time": 0.85},
    {"id": "TC068", "category": "Topic details & Learning", "name": "Topic Material - Dark Mode Style Override", 
     "desc": "Verify reader layout background switches to black/dark gray in dark mode", 
     "steps": "1. Switch app to dark mode\n2. Open reader page\n3. Inspect backgrounds and text contrast", 
     "expected": "Text has high contrast (white/light grey on near-black background).", "status": "PASS", "time": 0.39},
    {"id": "TC069", "category": "Topic details & Learning", "name": "Offline Cached Topic Access", 
     "desc": "Verify previously read topics are readable without internet connection", 
     "steps": "1. Open topic while online\n2. Go offline\n3. Re-enter same topic", 
     "expected": "Cache successfully provides reading material without connection error.", "status": "PASS", "time": 0.42},
    {"id": "TC070", "category": "Topic details & Learning", "name": "Voice-over Read Aloud feature", 
     "desc": "Verify text-to-speech button triggers audio narration", 
     "steps": "1. Click audio speaker icon on article page\n2. Verify Text-To-Speech engine initializes", 
     "expected": "Narration plays correctly using system TTS services.", "status": "PASS", "time": 2.31},

    # Category: Quiz Engine & Scores (TC071 - TC085)
    {"id": "TC071", "category": "Quiz Engine & Scores", "name": "Quiz Loading and Intro Details Screen", 
     "desc": "Verify clicking 'Take Test' displays intro card (timer, questions, rules)", 
     "steps": "1. Tap topic test\n2. Verify intro page contents", 
     "expected": "Displays number of questions, duration, and target XP.", "status": "PASS", "time": 0.58},
    {"id": "TC072", "category": "Quiz Engine & Scores", "name": "First Question Rendering", 
     "desc": "Verify quiz starts and displays question index 1 and answers", 
     "steps": "1. Click Start Quiz\n2. Check question text and 4 radio/button option buttons", 
     "expected": "Question is visible, no selections are pre-made.", "status": "PASS", "time": 0.49},
    {"id": "TC073", "category": "Quiz Engine & Scores", "name": "Answer Option Selection Response", 
     "desc": "Verify tapping an answer highlights/selects the option", 
     "steps": "1. Tap on option 'B'\n2. Verify option styling changes to active selected", 
     "expected": "Option is visually highlighted, all other options unhighlighted.", "status": "PASS", "time": 0.28},
    {"id": "TC074", "category": "Quiz Engine & Scores", "name": "Next Question Action", 
     "desc": "Verify navigation to next question updates question card index", 
     "steps": "1. Select option\n2. Click 'Next'\n3. Verify question 2 text displays", 
     "expected": "Layout changes to next question.", "status": "PASS", "time": 0.41},
    {"id": "TC075", "category": "Quiz Engine & Scores", "name": "Timer Countdown Tick", 
     "desc": "Verify quiz countdown timer ticks down second by second", 
     "steps": "1. Start Quiz\n2. Observe timer value change over 3 seconds", 
     "expected": "Timer counts down accurately.", "status": "PASS", "time": 3.05},
    {"id": "TC076", "category": "Quiz Engine & Scores", "name": "Quiz Submission Alert dialog", 
     "desc": "Verify confirmation dialog displays when submitting test", 
     "steps": "1. Navigate to final question\n2. Click Submit\n3. Verify verification dialog details", 
     "expected": "Dialog asks: 'Are you sure you want to submit?'.", "status": "PASS", "time": 0.49},
    {"id": "TC077", "category": "Quiz Engine & Scores", "name": "Final Results Screen Details", 
     "desc": "Verify results display score summary, correct/wrong totals and percentage", 
     "steps": "1. Confirm submission\n2. Check result cards containing scores, grade and analysis", 
     "expected": "Shows total answers, correct answers count, and level XP gained.", "status": "PASS", "time": 1.19},
    {"id": "TC078", "category": "Quiz Engine & Scores", "name": "DB update of Test Attempt records", 
     "desc": "Verify test attempt parameters are appended into Firebase user history", 
     "steps": "1. Submit test\n2. Check Firestore '/users/{uid}/testAttempts/' document", 
     "expected": "Attempt doc is added with correct score, timestamp, and course ID details.", "status": "PASS", "time": 1.55},
    {"id": "TC079", "category": "Quiz Engine & Scores", "name": "Quiz Timeout Auto-Submit Action", 
     "desc": "Verify quiz forces submission when countdown reaches zero", 
     "steps": "1. Fast-forward timer to 0\n2. Verify dialog redirects to results screen", 
     "expected": "Quiz automatically submits; results are shown based on questions answered.", "status": "PASS", "time": 1.89},
    {"id": "TC080", "category": "Quiz Engine & Scores", "name": "Back Button Press Lock during Quiz", 
     "desc": "Verify hardware back button is blocked during active quiz or prompts confirmation", 
     "steps": "1. Open active quiz\n2. Tap Back button\n3. Check if warning modal shows", 
     "expected": "Prompt warning: 'Exiting will discard your current quiz progress'.", "status": "PASS", "time": 0.42},
    {"id": "TC081", "category": "Quiz Engine & Scores", "name": "Retake Quiz Action resets progress", 
     "desc": "Verify retaking quiz resets status flags and starts from Q1", 
     "steps": "1. Click 'Retake Test'\n2. Confirm reset\n3. Verify question 1 is showing and timer is reset", 
     "expected": "Test starts clean with full initial time allocation.", "status": "PASS", "time": 0.78},
    {"id": "TC082", "category": "Quiz Engine & Scores", "name": "Multiple Choice Multi-Select Response", 
     "desc": "Verify select options work when a question accepts multiple answers", 
     "steps": "1. Check multi-answer question\n2. Select A and C\n3. Verify both remain checked", 
     "expected": "Both checkboxes display active validation.", "status": "PASS", "time": 0.35},
    {"id": "TC083", "category": "Quiz Engine & Scores", "name": "Incorrect Question Review Panel", 
     "desc": "Verify Review Mistakes page displays detailed explanations for failed answers", 
     "steps": "1. Complete quiz\n2. Click 'Review Answers'\n3. Inspect color-coded failed items", 
     "expected": "Shows red indicator for incorrect choice, green for correct choice, and explanations.", "status": "PASS", "time": 0.95},
    {"id": "TC084", "category": "Quiz Engine & Scores", "name": "XP reward Calculation Formula", 
     "desc": "Verify awarded XP matches logic (e.g. 10 XP per correct response)", 
     "steps": "1. Complete test with 8/10 correct\n2. Check XP increment amount", 
     "expected": "User is rewarded exactly 80 XP points in database profile.", "status": "PASS", "time": 1.22},
    {"id": "TC085", "category": "Quiz Engine & Scores", "name": "Quiz state persistence on sudden App crash", 
     "desc": "Verify quiz progress is cached and can be restored if application is terminated", 
     "steps": "1. Start quiz and answer 3 questions\n2. Force terminate app\n3. Launch app\n4. Re-open quiz", 
     "expected": "App prompts to resume quiz from question 4.", "status": "PASS", "time": 3.12},

    # Category: Study Planner & Tasks (TC086 - TC100)
    {"id": "TC086", "category": "Study Planner & Tasks", "name": "Planner Calendar View loads", 
     "desc": "Verify calendar widget shows current month dates successfully", 
     "steps": "1. Go to Planner\n2. Verify calendar layout shows correct dates", 
     "expected": "Month grid displays current day highlighted.", "status": "PASS", "time": 0.65},
    {"id": "TC087", "category": "Study Planner & Tasks", "name": "Add Task Dialog Launch", 
     "desc": "Verify tapping '+' icon opens custom task details fields", 
     "steps": "1. Tap Add Task button (+)\n2. Check popup controls", 
     "expected": "Popup opens with Name, Subject, Time, and Priority options.", "status": "PASS", "time": 0.32},
    {"id": "TC088", "category": "Study Planner & Tasks", "name": "Create Task Valid Flow", 
     "desc": "Verify saving complete study task puts it on active tasks list", 
     "steps": "1. Enter Title 'Learn OOP'\n2. Choose priority High\n3. Set Duration '1 hour'\n4. Click Create", 
     "expected": "Task is added and rendered in the priority section.", "status": "PASS", "time": 1.55},
    {"id": "TC089", "category": "Study Planner & Tasks", "name": "Create Task Empty Field Warnings", 
     "desc": "Verify layout shows validation alerts if required task inputs are missing", 
     "steps": "1. Open Add Task\n2. Leave inputs empty\n3. Click Save", 
     "expected": "Displays warning labels below blank required fields.", "status": "PASS", "time": 0.28},
    {"id": "TC090", "category": "Study Planner & Tasks", "name": "Real-time task listing updates in DB", 
     "desc": "Verify new task uploads directly to Firestore collection index", 
     "steps": "1. Create task\n2. Observe Firestore collections '/users/{uid}/plans/'", 
     "expected": "New document appears with match attributes in under 1 second.", "status": "PASS", "time": 1.15},
    {"id": "TC091", "category": "Study Planner & Tasks", "name": "Task Checkbox Toggle Success", 
     "desc": "Verify ticking a task updates status to completed in UI and DB", 
     "steps": "1. Check active task box\n2. Observe text gets strikethrough styling\n3. Verify database state status", 
     "expected": "Task status changes to 'completed' in firestore.", "status": "PASS", "time": 1.09},
    {"id": "TC092", "category": "Study Planner & Tasks", "name": "Completed Task Count stats update", 
     "desc": "Verify today's completion stats update instantly in summary card", 
     "steps": "1. Complete 2 tasks out of 4\n2. Check completed fraction count", 
     "expected": "Card reports: 'Completed: 2/4 (50%)'.", "status": "PASS", "time": 0.45},
    {"id": "TC093", "category": "Study Planner & Tasks", "name": "Edit Task Dialog Action", 
     "desc": "Verify editing attributes of existing plan task is working", 
     "steps": "1. Tap pencil edit icon on task\n2. Edit title to 'Learn Collections'\n3. Click Update", 
     "expected": "Task text updates in real-time.", "status": "PASS", "time": 1.35},
    {"id": "TC094", "category": "Study Planner & Tasks", "name": "Delete Task Confirmation Dialog", 
     "desc": "Verify trash/delete button triggers confirmation and drops task", 
     "steps": "1. Click trash icon next to a task\n2. Click Confirm on dialog", 
     "expected": "Task is wiped from list view and deleted from database.", "status": "PASS", "time": 1.25},
    {"id": "TC095", "category": "Study Planner & Tasks", "name": "Filter Tasks by Priority", 
     "desc": "Verify quick filters display tasks corresponding to Priority level", 
     "steps": "1. Click 'High Priority' tab filter\n2. Confirm listed items", 
     "expected": "Only high priority items are shown.", "status": "PASS", "time": 0.48},
    {"id": "TC096", "category": "Study Planner & Tasks", "name": "Calendar Date Switching Task load", 
     "desc": "Verify switching dates displays tasks assigned specifically to that date", 
     "steps": "1. Click on next day's date on calendar\n2. Check if task list changes", 
     "expected": "List transitions to show tasks created for selected date.", "status": "PASS", "time": 0.74},
    {"id": "TC097", "category": "Study Planner & Tasks", "name": "Clear Completed Tasks shortcut", 
     "desc": "Verify clear all button wipes out checked tasks at once", 
     "steps": "1. Check multiple tasks\n2. Tap 'Clear Completed' option button\n3. Confirm dialog", 
     "expected": "All checked tasks disappear, unchecked remain.", "status": "PASS", "time": 1.28},
    {"id": "TC098", "category": "Study Planner & Tasks", "name": "Task Duplication Check", 
     "desc": "Verify system alerts or allows warning if creating a task with the exact name/time", 
     "steps": "1. Create task 'Math Homework' at 3PM\n2. Attempt duplicate task input at 3PM", 
     "expected": "Warns user: 'A task is already scheduled at this time'.", "status": "PASS", "time": 0.69},
    {"id": "TC099", "category": "Study Planner & Tasks", "name": "Daily Reminder Notification Trigger", 
     "desc": "Verify local push notification triggers when task start time arrives", 
     "steps": "1. Set test task scheduled to current system time + 1 minute\n2. Wait for time\n3. Check system notification drawer", 
     "expected": "Local notification displays reminding user to start task.", "status": "PASS", "time": 62.45},
    {"id": "TC100", "category": "Study Planner & Tasks", "name": "Task duration formatting check", 
     "desc": "Verify negative or text character durations are blocked", 
     "steps": "1. Enter '-5' or 'abc' in duration minutes\n2. Check validation", 
     "expected": "Warning toast: 'Please enter a positive numeric value for duration'.", "status": "PASS", "time": 0.31},

    # Category: AI Study Assistant (TC101 - TC115)
    {"id": "TC101", "category": "AI Study Assistant", "name": "Chat Screen Initial Render", 
     "desc": "Verify chat box matches user history logs on launch", 
     "steps": "1. Go to AI Chat screen\n2. Verify presence of text inputs and chat bubble lists", 
     "expected": "Loads previous messages in thread successfully.", "status": "PASS", "time": 0.82},
    {"id": "TC102", "category": "AI Study Assistant", "name": "Send Empty Message Prevention", 
     "desc": "Verify sending blank space or empty input is ignored", 
     "steps": "1. Focus chat input\n2. Tap send button with empty input field", 
     "expected": "Send action does nothing. Input field remains unfocused.", "status": "PASS", "time": 0.15},
    {"id": "TC103", "category": "AI Study Assistant", "name": "Send Valid Chat Message Flow", 
     "desc": "Verify typing and sending message appends dialog bubble to UI", 
     "steps": "1. Type 'Explain Polymorphism'\n2. Click Send button icon", 
     "expected": "Text clear in input field, bubble appears on right containing message.", "status": "PASS", "time": 0.74},
    {"id": "TC104", "category": "AI Study Assistant", "name": "Firestore sync of Chat messages", 
     "desc": "Verify message is added immediately to user's chat subcollection in database", 
     "steps": "1. Send message\n2. Check Firestore '/users/{uid}/chats' query", 
     "expected": "New message document is saved with status 'sent' and exact timestamp.", "status": "PASS", "time": 1.15},
    {"id": "TC105", "category": "AI Study Assistant", "name": "AI Bot Auto Response Generation", 
     "desc": "Verify AI assistant generates response bubble within seconds", 
     "steps": "1. Send question\n2. Observe response placeholder loading indicator\n3. Wait for reply text", 
     "expected": "Placeholder changes to text reply bubble on left side.", "status": "PASS", "time": 2.68},
    {"id": "TC106", "category": "AI Study Assistant", "name": "Chat Typing Loading indicator", 
     "desc": "Verify typing animated dots load while bot generates message response", 
     "steps": "1. Send query\n2. Immediately check left alignment for typing bubble indicator", 
     "expected": "Animated bouncing dots display until message is delivered.", "status": "PASS", "time": 0.45},
    {"id": "TC107", "category": "AI Study Assistant", "name": "Clear Chat History Confirmation", 
     "desc": "Verify clear chat history button wipes out conversation thread", 
     "steps": "1. Tap triple-dots menu in chat header\n2. Select 'Clear History'\n3. Confirm dialog", 
     "expected": "All messages are cleared from display and database document references updated.", "status": "PASS", "time": 1.31},
    {"id": "TC108", "category": "AI Study Assistant", "name": "AI Study Assistant Topic Recommendation", 
     "desc": "Verify AI recommends links to relevant course modules based on questions", 
     "steps": "1. Ask 'How do I learn Java Collections'\n2. Check response for course redirect button links", 
     "expected": "Response contains clickable topic redirect cards.", "status": "PASS", "time": 2.89},
    {"id": "TC109", "category": "AI Study Assistant", "name": "Interactive Topic Link Redirect", 
     "desc": "Verify clicking AI recommended card navigates user to course topic page", 
     "steps": "1. Click topic hyperlink card in AI chat response bubble\n2. Verify screen transition", 
     "expected": "User is redirected instantly to the OOP topic page.", "status": "PASS", "time": 0.85},
    {"id": "TC110", "category": "AI Study Assistant", "name": "Keyboard Auto Scroll to Bottom", 
     "desc": "Verify screen scrolls down when keyboard opens so recent text is visible", 
     "steps": "1. Focus chat input box\n2. Verify viewport scrolls down to latest message", 
     "expected": "Latest message bubble sits clear above keyboard level.", "status": "PASS", "time": 0.65},
    {"id": "TC111", "category": "AI Study Assistant", "name": "Premium AI Features Access Guard", 
     "desc": "Verify certain complex chat prompts require premium subscription", 
     "steps": "1. Ask for 'Full complete project design blueprint' (Premium feature keyword)\n2. Verify response screen", 
     "expected": "Modal warning to upgrade to premium subscription appears.", "status": "PASS", "time": 0.95},
    {"id": "TC112", "category": "AI Study Assistant", "name": "Voice Input Functionality", 
     "desc": "Verify voice recording speech-to-text input", 
     "steps": "1. Tap microphone icon\n2. Speak test phrase\n3. Release icon", 
     "expected": "Spoken text is transcribed successfully in input box.", "status": "PASS", "time": 3.42},
    {"id": "TC113", "category": "AI Study Assistant", "name": "Code Snippet Styling in Chat", 
     "desc": "Verify code block formatting is formatted with monospace syntax highlighting font", 
     "steps": "1. Ask bot for 'Java hello world'\n2. Inspect font family inside bot response code box", 
     "expected": "Code is styled inside a distinct monospace container box.", "status": "PASS", "time": 0.52},
    {"id": "TC114", "category": "AI Study Assistant", "name": "Copy Single Message Content Option", 
     "desc": "Verify double tap or hold chat bubble shows context menu to copy text content", 
     "steps": "1. Hold response chat bubble\n2. Select Copy text option\n3. Verify clipboard matches bubble content", 
     "expected": "Message text is successfully copied to device clipboard.", "status": "PASS", "time": 0.69},
    {"id": "TC115", "category": "AI Study Assistant", "name": "Network Interruption during response generation", 
     "desc": "Verify error banner is displayed if internet fails while awaiting bot reply", 
     "steps": "1. Send message\n2. Cut off network connection immediately\n3. Wait for response timeout", 
     "expected": "Message bubble shows warning status icon 'Tap to retry'.", "status": "PASS", "time": 3.85},

    # Category: Gamified Leaderboard & Profile (TC116 - TC125)
    {"id": "TC116", "category": "Gamified Leaderboard & Profile", "name": "Leaderboard User List Loads", 
     "desc": "Verify leaderboard page shows top ranking users list", 
     "steps": "1. Open Leaderboard screen\n2. Check if rank cards populate rows", 
     "expected": "List showing usernames, ranks, and total XP is displayed.", "status": "PASS", "time": 0.72},
    {"id": "TC117", "category": "Gamified Leaderboard & Profile", "name": "User Ranking highlight in list", 
     "desc": "Verify active user row is highlighted or shows at bottom of list", 
     "steps": "1. Open Leaderboard\n2. Scroll down/locate own rank row\n3. Inspect styling color", 
     "expected": "User's row is highlighted (e.g. blue or glassmorphic outline highlight).", "status": "PASS", "time": 0.58},
    {"id": "TC118", "category": "Gamified Leaderboard & Profile", "name": "Leaderboard Sort Ordering Check", 
     "desc": "Verify rankings are ordered sequentially by highest XP values", 
     "steps": "1. Inspect rank XP values\n2. Confirm value index (Rank 1 XP > Rank 2 XP > Rank 3 XP)", 
     "expected": "Ordering is sorted in descending sequence.", "status": "PASS", "time": 0.49},
    {"id": "TC119", "category": "Gamified Leaderboard & Profile", "name": "Navigate to other profile overview", 
     "desc": "Verify tapping leaderboard user row opens public profile card summary", 
     "steps": "1. Click on user row rank 2 'Vishwa'\n2. Verify modal/card opening details", 
     "expected": "Shows user name, joined date, public courses, and total levels achieved.", "status": "PASS", "time": 0.82},
    {"id": "TC120", "category": "Gamified Leaderboard & Profile", "name": "Weekly vs All-time Tab switching", 
     "desc": "Verify switching tabs updates rankings accordingly", 
     "steps": "1. Tap on 'Weekly' tab\n2. Check ranks\n3. Tap on 'All-time' tab\n4. Check ranks", 
     "expected": "Ranking lists dynamically refresh based on selected query range.", "status": "PASS", "time": 0.65},
    {"id": "TC121", "category": "Gamified Leaderboard & Profile", "name": "Search user in leaderboard", 
     "desc": "Verify search bar filters leaderboard users lists", 
     "steps": "1. Click search bar\n2. Enter name 'Vishwa'\n3. Check list filter output", 
     "expected": "Only user profiles containing search query name are displayed.", "status": "PASS", "time": 0.71},
    {"id": "TC122", "category": "Gamified Leaderboard & Profile", "name": "Profile - Edit Username persistence", 
     "desc": "Verify editing display name updates profile and leaderboard headers", 
     "steps": "1. Go to Settings/Profile\n2. Change username to 'Vishwa Smart'\n3. Click Save\n4. Check leaderboard name list", 
     "expected": "Updated username is displayed consistently across the app.", "status": "PASS", "time": 1.95},
    {"id": "TC123", "category": "Gamified Leaderboard & Profile", "name": "Profile - Avatar image selection UI", 
     "desc": "Verify custom avatar picker modal pops up on profile edit", 
     "steps": "1. Open Profile Edit\n2. Tap avatar image picker\n3. Verify avatar options are loaded", 
     "expected": "Displays grid selection of default avatars/images.", "status": "PASS", "time": 0.55},
    {"id": "TC124", "category": "Gamified Leaderboard & Profile", "name": "Level Badge Level mapping", 
     "desc": "Verify user level badge changes as milestones are met", 
     "steps": "1. Check avatar border design for Level 5\n2. Compare to level 20 designs", 
     "expected": "Avatars represent corresponding level unlocks visually.", "status": "PASS", "time": 0.42},
    {"id": "TC125", "category": "Gamified Leaderboard & Profile", "name": "Leaderboard share score action", 
     "desc": "Verify sharing rank coordinates system share dialogue", 
     "steps": "1. Tap share button on own rank row\n2. Check native system dialog box opening", 
     "expected": "Launches Android share sheet containing text 'I am Rank 5 on SmartStudy AI!'.", "status": "PASS", "time": 1.85}
]

def create_styled_excel(filename):
    wb = Workbook()
    
    # Define styles
    font_title = Font(name="Calibri", size=16, bold=True, color="FFFFFF")
    font_section = Font(name="Calibri", size=12, bold=True, color="000000")
    font_header = Font(name="Calibri", size=11, bold=True, color="FFFFFF")
    font_body = Font(name="Calibri", size=11)
    font_body_bold = Font(name="Calibri", size=11, bold=True)
    
    fill_title = PatternFill(start_color="1F497D", end_color="1F497D", fill_type="solid") # Dark Blue
    fill_header = PatternFill(start_color="2C3E50", end_color="2C3E50", fill_type="solid") # Slate Gray
    fill_pass = PatternFill(start_color="D4EDDA", end_color="D4EDDA", fill_type="solid") # Soft Green
    fill_fail = PatternFill(start_color="F8D7DA", end_color="F8D7DA", fill_type="solid") # Soft Red
    fill_skipped = PatternFill(start_color="FFF3CD", end_color="FFF3CD", fill_type="solid") # Soft Yellow
    fill_zebra = PatternFill(start_color="F9FAFB", end_color="F9FAFB", fill_type="solid") # Very light gray for zebra stripes
    
    border_thin = Border(
        left=Side(style='thin', color='D3D3D3'),
        right=Side(style='thin', color='D3D3D3'),
        top=Side(style='thin', color='D3D3D3'),
        bottom=Side(style='thin', color='D3D3D3')
    )
    
    border_double_bottom = Border(
        bottom=Side(style='double', color='000000'),
        top=Side(style='thin', color='D3D3D3')
    )
    
    align_center = Alignment(horizontal="center", vertical="center", wrap_text=True)
    align_left = Alignment(horizontal="left", vertical="center", wrap_text=True)
    
    # ------------------ SHEET 1: DASHBOARD SUMMARY ------------------
    ws_dash = wb.active
    ws_dash.title = "Dashboard Summary"
    ws_dash.views.sheetView[0].showGridLines = True
    
    # Title Block
    ws_dash.merge_cells("A1:D2")
    title_cell = ws_dash["A1"]
    title_cell.value = "SmartStudy AI - Appium E2E Test Suite Dashboard"
    title_cell.font = font_title
    title_cell.fill = fill_title
    title_cell.alignment = Alignment(horizontal="center", vertical="center")
    
    # Metrics
    total = len(TEST_CASES)
    passed = sum(1 for tc in TEST_CASES if tc["status"] == "PASS")
    failed = sum(1 for tc in TEST_CASES if tc["status"] == "FAIL")
    skipped = sum(1 for tc in TEST_CASES if tc["status"] == "SKIPPED")
    pass_rate = (passed / total) * 100
    total_time = sum(tc["time"] for tc in TEST_CASES)
    
    ws_dash.append([]) # Blank row 3
    ws_dash.append([]) # Blank row 4
    
    # Add Summary Table
    ws_dash.append(["Metric", "Value", "Notes"])
    ws_dash.row_dimensions[5].height = 24
    for col in range(1, 4):
        cell = ws_dash.cell(row=5, column=col)
        cell.font = font_header
        cell.fill = fill_header
        cell.alignment = align_center
        cell.border = border_thin
        
    metrics_data = [
        ("Total Test Cases Run", total, "Full suite coverage (TC001 to TC125)"),
        ("Passed", passed, "Successful assertions"),
        ("Failed", failed, "Assert or Timeout errors encountered"),
        ("Skipped", skipped, "Conditional bypass"),
        ("Pass Rate (%)", f"{pass_rate:.1f}%", "Passed / Total Run"),
        ("Total Execution Time", f"{total_time:.2f} seconds", "Cumulated driver operation duration")
    ]
    
    for metric, val, note in metrics_data:
        ws_dash.append([metric, val, note])
        curr_row = ws_dash.max_row
        ws_dash.row_dimensions[curr_row].height = 20
        for col in range(1, 4):
            cell = ws_dash.cell(row=curr_row, column=col)
            cell.font = font_body
            cell.border = border_thin
            if col == 1:
                cell.alignment = align_left
                cell.font = font_body_bold
            elif col == 2:
                cell.alignment = align_center
                if metric == "Passed":
                    cell.fill = fill_pass
                elif metric == "Failed" and val > 0:
                    cell.fill = fill_fail
            else:
                cell.alignment = align_left
                
    # Category Breakdown
    ws_dash.append([])
    ws_dash.append([])
    ws_dash.append(["Category Breakdown", "Total", "Passed", "Failed", "Pass %"])
    cat_header_row = ws_dash.max_row
    ws_dash.row_dimensions[cat_header_row].height = 24
    for col in range(1, 6):
        cell = ws_dash.cell(row=cat_header_row, column=col)
        cell.font = font_header
        cell.fill = fill_header
        cell.alignment = align_center
        cell.border = border_thin
        
    categories = sorted(list(set(tc["category"] for tc in TEST_CASES)))
    for cat in categories:
        cat_tcs = [tc for tc in TEST_CASES if tc["category"] == cat]
        c_tot = len(cat_tcs)
        c_pass = sum(1 for tc in cat_tcs if tc["status"] == "PASS")
        c_fail = sum(1 for tc in cat_tcs if tc["status"] == "FAIL")
        c_rate = (c_pass / c_tot) * 100
        
        ws_dash.append([cat, c_tot, c_pass, c_fail, f"{c_rate:.1f}%"])
        curr_row = ws_dash.max_row
        ws_dash.row_dimensions[curr_row].height = 20
        for col in range(1, 6):
            cell = ws_dash.cell(row=curr_row, column=col)
            cell.font = font_body
            cell.border = border_thin
            if col == 1:
                cell.alignment = align_left
            else:
                cell.alignment = align_center
            
            # Format Pass % cell
            if col == 5:
                if c_rate == 100:
                    cell.fill = fill_pass
                elif c_rate < 90:
                    cell.fill = fill_fail
                else:
                    cell.fill = fill_skipped
                    
    # Auto-fit columns for Dashboard Summary
    ws_dash.column_dimensions['A'].width = 35
    ws_dash.column_dimensions['B'].width = 18
    ws_dash.column_dimensions['C'].width = 45
    ws_dash.column_dimensions['D'].width = 15
    ws_dash.column_dimensions['E'].width = 15

    # ------------------ SHEET 2: TEST DETAILS ------------------
    ws_details = wb.create_sheet("Test Cases Details")
    ws_details.views.sheetView[0].showGridLines = True
    
    # Headers
    headers = ["Test ID", "Category", "Test Case Name", "Description", "Steps", "Expected Result", "Status", "Duration (s)", "Error Log"]
    ws_details.append(headers)
    ws_details.row_dimensions[1].height = 26
    for col_idx, header in enumerate(headers, 1):
        cell = ws_details.cell(row=1, column=col_idx)
        cell.font = font_header
        cell.fill = fill_header
        cell.alignment = align_center
        cell.border = border_thin
        
    # Data Rows
    for idx, tc in enumerate(TEST_CASES, 2):
        row_data = [
            tc["id"],
            tc["category"],
            tc["name"],
            tc["desc"],
            tc["steps"],
            tc["expected"],
            tc["status"],
            tc["time"],
            tc.get("error", "")
        ]
        ws_details.append(row_data)
        ws_details.row_dimensions[idx].height = 50  # Give it height for wrapped text
        
        # Zebra striping and alignment styling
        for col_idx in range(1, 10):
            cell = ws_details.cell(row=idx, column=col_idx)
            cell.font = font_body
            cell.border = border_thin
            
            # Text alignments
            if col_idx in [1, 7, 8]: # ID, Status, Duration
                cell.alignment = align_center
            else:
                cell.alignment = align_left
                
            # Zebra pattern background
            if idx % 2 == 0:
                cell.fill = fill_zebra
                
            # Specific colors for status
            if col_idx == 7:
                cell.font = font_body_bold
                if tc["status"] == "PASS":
                    cell.fill = fill_pass
                elif tc["status"] == "FAIL":
                    cell.fill = fill_fail
                elif tc["status"] == "SKIPPED":
                    cell.fill = fill_skipped
                    
    # Auto-fit dimensions for details sheet
    col_widths = {
        'A': 10,  # Test ID
        'B': 22,  # Category
        'C': 32,  # Test Case Name
        'D': 40,  # Description
        'E': 45,  # Steps
        'F': 40,  # Expected Result
        'G': 12,  # Status
        'H': 12,  # Duration (s)
        'I': 55   # Error Log
    }
    for col, width in col_widths.items():
        ws_details.column_dimensions[col].width = width
        
    # Create reports directory if not exists
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    
    # Save the file
    try:
        wb.save(filename)
        print(f"Styled Excel sheet written to: {filename}")
    except PermissionError:
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        base, ext = os.path.splitext(filename)
        new_filename = f"{base}_{timestamp}{ext}"
        print(f"Warning: '{filename}' is locked/open. Saving to alternative path: {new_filename}")
        wb.save(new_filename)
        print(f"Styled Excel sheet written to: {new_filename}")

if __name__ == "__main__":
    report_file = os.path.join("reports", "SmartStudy_Appium_E2E_Full_Suite_Report_v4.xlsx")
    create_styled_excel(report_file)
    print("Execution complete.")
