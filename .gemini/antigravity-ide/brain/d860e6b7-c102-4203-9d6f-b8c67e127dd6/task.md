# SmartStudy Tasks (Implementation)

## Phase 1 – Android Navigation & UI
- [ ] Add **Reject** button and **Details** button in `CoursesScreen.kt` after enrollment.
- [ ] Create `CourseDetailsScreen.kt` with four tabs (AI Explanation, Video, PDF, Important Questions) and fetch data from backend.
- [ ] Add navigation entries for **Profile**, **Practice**, **Assessment**, **Theory** in `BottomNavBar.kt` and `AppDrawer.kt`.
- [ ] Implement `ProfileScreen.kt` (user info, XP, logout).
- [ ] Implement `PracticeScreen.kt` (list of coding practice problems, progress bar).
- [ ] Implement `AssessmentScreen.kt` (load 50 theory questions, 30‑minute timer, submit to Groq for analysis, display results).
- [ ] Update backend (`BackendProvider.kt` / `BackendModels.kt`) with methods: `removeEnrollment`, `getCourseDetails`, `getAssessmentQuestions`, `getPracticeProblems`.

## Phase 2 – Web (Vite/React) UI
- [ ] Update `Courses.tsx` to show **Reject** and **Details** buttons after enrollment.
- [ ] Create `CourseDetails.tsx` component with four tabs (AI, Video, PDF, Questions) and fetch data via Firebase.
- [ ] Extend `Sidebar.tsx` navigation items to include **Profile**, **Practice**, **Assessment**, **Theory**.
- [ ] Add `Profile.tsx`, `Practice.tsx`, `Assessment.tsx` components mirroring Android screens.
- [ ] Implement timer (30 min) and question loading (50) in `Assessment.tsx`.
- [ ] Hook up AI analysis call to Groq endpoint after test submission.

## Phase 3 – Data & Backend
- [ ] Populate Firestore with unique topics per course, 15 theory questions per topic, 50 assessment questions per course.
- [ ] Store video URLs and PDF links for each topic.
- [ ] Add Groq prompt templates for AI explanations and test analysis.

## Phase 4 – Build & Run
- [ ] Run Android build (`./gradlew assembleDebug`).
- [ ] Run Web dev server (`npm run dev`).
- [ ] Verify all screens work, navigation functional, timer works, AI analysis displays.
- [ ] Capture the local web URL (e.g., `http://localhost:5173`).

## Phase 5 – Documentation
- [ ] Update `walkthrough.md` with final screenshots and notes.
- [ ] Publish the web link.
