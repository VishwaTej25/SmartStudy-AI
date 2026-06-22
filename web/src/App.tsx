import { useState, useEffect } from "react";
import { auth, db } from "./firebase";
import { onAuthStateChanged } from "firebase/auth";
import type { User } from "firebase/auth";
import { doc, onSnapshot, collection, query, orderBy } from "firebase/firestore";
import { Sidebar } from "./components/Sidebar";
import { Dashboard } from "./components/Dashboard";
import { Auth } from "./components/Auth";
import { Courses } from "./components/Courses";
import type { Course } from "./components/Courses";
import { CourseDetails } from "./components/CourseDetails";
import { Planner } from "./components/Planner";
import { Chat } from "./components/Chat";
import { Leaderboard } from "./components/Leaderboard";
import { Settings } from "./components/Settings";
import { Practice } from "./components/Practice";
import { Assessment } from "./components/Assessment";
import { Profile } from "./components/Profile";
import { AdminPortal } from "./components/AdminPortal";

// SmartStudy AI Main Application Entry Point - Production Deploy
function App() {
  const [user, setUser] = useState<User | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(true);
  const [currentTab, setCurrentTab] = useState("dashboard");
  const [userProfile, setUserProfile] = useState<any>(null);
  const [settings, setSettings] = useState<any>({ darkMode: true, aiVoice: true, notifications: true });
  const [enrolledCourseIds, setEnrolledCourseIds] = useState<Set<string>>(new Set());
  const [enrollments, setEnrollments] = useState<any>({});
  const [plans, setPlans] = useState<any[]>([]);
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null);

  // ─── Auth Listener ────────────────────────────────────────────────────────
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
      setUser(currentUser);
      if (currentUser) {
        try {
          const idTokenResult = await currentUser.getIdTokenResult();
          setIsAdmin(!!idTokenResult.claims.admin);
        } catch (err) {
          console.error("Error fetching custom claims:", err);
          setIsAdmin(false);
        }
      } else {
        setIsAdmin(false);
        setUserProfile(null);
        setEnrolledCourseIds(new Set());
        setEnrollments({});
        setPlans([]);
        setSelectedCourse(null);
        setCurrentTab("dashboard");
      }
      setLoading(false);
    });
    return () => unsubscribe();
  }, []);

  // ─── Firestore Listeners (non-admin users) ────────────────────────────────
  useEffect(() => {
    if (!user || isAdmin) return;

    const profileRef = doc(db, "users", user.uid);
    const unsubProfile = onSnapshot(profileRef, (docSnap) => {
      if (docSnap.exists()) setUserProfile(docSnap.data());
    });

    const settingsRef = doc(db, "users", user.uid, "private", "settings");
    const unsubSettings = onSnapshot(settingsRef, (docSnap) => {
      if (docSnap.exists()) setSettings(docSnap.data());
    });

    const enrollmentsRef = collection(db, "users", user.uid, "enrollments");
    const unsubEnrollments = onSnapshot(enrollmentsRef, (snapshot) => {
      const ids = new Set<string>();
      const items: any = {};
      snapshot.docs.forEach((doc) => {
        ids.add(doc.id);
        items[doc.id] = doc.data();
      });
      setEnrolledCourseIds(ids);
      setEnrollments(items);
    });

    const plansRef = collection(db, "users", user.uid, "plans");
    const q = query(plansRef, orderBy("createdAt", "desc"));
    const unsubPlans = onSnapshot(q, (snapshot) => {
      const list = snapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
      setPlans(list);
    });

    return () => {
      unsubProfile();
      unsubSettings();
      unsubEnrollments();
      unsubPlans();
    };
  }, [user]);

  // ─── Theme ────────────────────────────────────────────────────────────────
  useEffect(() => {
    if (settings.darkMode === false) {
      document.body.style.background = "linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%)";
      document.body.style.color = "#1f2937";
      document.documentElement.style.setProperty("--card-bg", "rgba(255, 255, 255, 0.7)");
      document.documentElement.style.setProperty("--card-border", "rgba(0, 0, 0, 0.08)");
      document.documentElement.style.setProperty("--text-main", "#1f2937");
      document.documentElement.style.setProperty("--text-muted", "#4b5563");
    } else {
      document.body.style.background = "linear-gradient(135deg, #050b1a 0%, #0a1b55 100%)";
      document.body.style.color = "#ffffff";
      document.documentElement.style.setProperty("--card-bg", "rgba(27, 34, 53, 0.7)");
      document.documentElement.style.setProperty("--card-border", "rgba(255, 255, 255, 0.08)");
      document.documentElement.style.setProperty("--text-main", "#ffffff");
      document.documentElement.style.setProperty("--text-muted", "#9ca3af");
    }
  }, [settings.darkMode]);

  const handleLogout = () => {
    setUser(null);
  };

  // ─── Loading ──────────────────────────────────────────────────────────────
  if (loading) {
    return (
      <div style={styles.loadingContainer}>
        <div style={styles.spinner}></div>
        <p style={{ marginTop: "20px", fontSize: "1.1rem" }}>Loading SmartStudy AI...</p>
      </div>
    );
  }

  // ─── Not Logged In ────────────────────────────────────────────────────────
  if (!user) {
    return <Auth onLoginSuccess={() => {}} />;
  }

  // ─── Admin Portal ─────────────────────────────────────────────────────────
  if (isAdmin) {
    return (
      <div style={{ minHeight: "100vh", padding: "40px" }}>
        <AdminPortal onBack={() => { auth.signOut(); }} />
      </div>
    );
  }

  // ─── Main App ─────────────────────────────────────────────────────────────
  const renderContent = () => {
    if (selectedCourse) {
      return (
        <CourseDetails
          course={selectedCourse}
          userId={user.uid}
          onBack={() => setSelectedCourse(null)}
        />
      );
    }

    switch (currentTab) {
      case "dashboard":
        return (
          <Dashboard
            userProfile={userProfile}
            plans={plans}
            enrollments={enrollments}
            setCurrentTab={setCurrentTab}
          />
        );
      case "courses":
        return (
          <Courses
            userId={user.uid}
            onSelectCourse={setSelectedCourse}
            enrolledCourseIds={enrolledCourseIds as Set<string>}
          />
        );
      case "planner":
        return <Planner userId={user.uid} />;
      case "practice":
        return <Practice userId={user.uid} enrolledCourseIds={enrolledCourseIds} />;
      case "assessment":
        return <Assessment userId={user.uid} enrolledCourseIds={enrolledCourseIds} />;
      case "chat":
        return <Chat userId={user.uid} />;
      case "leaderboard":
        return <Leaderboard userId={user.uid} />;
      case "profile":
        return <Profile userId={user.uid} userProfile={userProfile} />;
      case "settings":
        return (
          <Settings
            userId={user.uid}
            settings={settings}
            onLogout={handleLogout}
          />
        );
      default:
        return <div>Tab not found</div>;
    }
  };

  return (
    <div style={styles.appContainer}>
      <Sidebar
        currentTab={selectedCourse ? "courses" : currentTab}
        setCurrentTab={(tab) => {
          setSelectedCourse(null);
          setCurrentTab(tab);
        }}
        onLogout={handleLogout}
        userProfile={userProfile}
      />
      <main style={styles.mainContent}>{renderContent()}</main>
    </div>
  );
}

const styles = {
  loadingContainer: {
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    justifyContent: "center",
    minHeight: "100vh",
    color: "#fff",
    background: "#050b1a",
  },
  spinner: {
    width: "50px",
    height: "50px",
    border: "5px solid rgba(139, 61, 255, 0.2)",
    borderTop: "5px solid #8b3dff",
    borderRadius: "50%",
    animation: "spin 1s linear infinite",
  },
  appContainer: {
    display: "flex",
    minHeight: "100vh",
  },
  mainContent: {
    marginLeft: "var(--sidebar-width)",
    flexGrow: 1,
    padding: "40px",
    minHeight: "100vh",
    transition: "margin var(--transition-speed)",
  },
};

// Spinner keyframes
const styleSheet = document.styleSheets[0] || document.createElement("style");
if (styleSheet) {
  try {
    styleSheet.insertRule(
      `@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }`,
      styleSheet.cssRules.length
    );
  } catch (e) {}
}

export default App;
