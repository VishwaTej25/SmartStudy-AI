import { 
  Home, 
  BookOpen, 
  Code2,
  FileEdit,
  Calendar, 
  MessageSquare, 
  Trophy, 
  User,
  Settings, 
  LogOut 
} from "lucide-react";
import { auth } from "../firebase";

interface SidebarProps {
  currentTab: string;
  setCurrentTab: (tab: string) => void;
  onLogout: () => void;
  userProfile: any;
}

export const Sidebar: React.FC<SidebarProps> = ({ 
  currentTab, 
  setCurrentTab, 
  onLogout,
  userProfile 
}) => {
  const menuItems = [
    { id: "dashboard", label: "Home", icon: Home },
    { id: "courses", label: "Courses", icon: BookOpen },
    { id: "practice", label: "Practice", icon: Code2 },
    { id: "assessment", label: "Assessment", icon: FileEdit },
    { id: "planner", label: "Planner", icon: Calendar },
    { id: "chat", label: "AI Chat", icon: MessageSquare },
    { id: "leaderboard", label: "Leaderboard", icon: Trophy },
    { id: "profile", label: "Profile", icon: User },
    { id: "settings", label: "Settings", icon: Settings },
  ];

  const handleLogout = async () => {
    await auth.signOut();
    onLogout();
  };

  return (
    <div className="glass-panel" style={styles.sidebar}>
      <div style={styles.logoSection}>
        <span style={styles.logoEmoji}>🚀</span>
        <h2 style={styles.logoText}>SmartStudy AI</h2>
      </div>

      <div style={styles.profileSummary}>
        <div style={styles.avatar}>
          {userProfile?.fullName?.[0]?.toUpperCase() || "L"}
        </div>
        <div style={styles.profileDetails}>
          <div style={styles.profileName}>{userProfile?.fullName || "Learner"}</div>
          <div style={styles.profileTitle}>CSE Student</div>
        </div>
      </div>

      <nav style={styles.nav}>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setCurrentTab(item.id)}
              style={{
                ...styles.navItem,
                ...(isActive ? styles.activeNavItem : {}),
              }}
            >
              <Icon size={20} style={isActive ? styles.activeIcon : styles.icon} />
              <span style={styles.navLabel}>{item.label}</span>
            </button>
          );
        })}
      </nav>

      <button onClick={handleLogout} style={styles.logoutBtn}>
        <LogOut size={20} style={styles.icon} />
        <span style={styles.navLabel}>Logout</span>
      </button>
    </div>
  );
};

const styles = {
  sidebar: {
    width: "var(--sidebar-width)",
    height: "calc(100vh - 40px)",
    position: "fixed" as const,
    left: "20px",
    top: "20px",
    display: "flex",
    flexDirection: "column" as const,
    padding: "30px 20px",
    zIndex: 100,
    borderRadius: "24px",
  },
  logoSection: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    marginBottom: "35px",
    paddingLeft: "10px",
  },
  logoEmoji: {
    fontSize: "1.6rem",
  },
  logoText: {
    fontSize: "1.3rem",
    fontWeight: 800,
    color: "#fff",
    background: "linear-gradient(90deg, #fff 0%, #a78bfa 100%)",
    WebkitBackgroundClip: "text",
    WebkitTextFillColor: "transparent",
  },
  profileSummary: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    background: "rgba(255, 255, 255, 0.04)",
    borderRadius: "16px",
    padding: "12px",
    marginBottom: "30px",
    border: "1px solid rgba(255, 255, 255, 0.04)",
  },
  avatar: {
    width: "42px",
    height: "42px",
    borderRadius: "50%",
    background: "#8b3dff",
    color: "#fff",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: 700,
    fontSize: "1.1rem",
    boxShadow: "0 0 10px rgba(139, 61, 255, 0.3)",
  },
  profileDetails: {
    display: "flex",
    flexDirection: "column" as const,
    overflow: "hidden",
  },
  profileName: {
    fontWeight: 600,
    fontSize: "0.95rem",
    color: "#fff",
    whiteSpace: "nowrap" as const,
    textOverflow: "ellipsis",
    overflow: "hidden",
  },
  profileTitle: {
    fontSize: "0.75rem",
    color: "#9ca3af",
  },
  nav: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "8px",
    flexGrow: 1,
  },
  navItem: {
    display: "flex",
    alignItems: "center",
    gap: "14px",
    background: "none",
    border: "none",
    borderRadius: "14px",
    padding: "12px 16px",
    width: "100%",
    textAlign: "left" as const,
    color: "#9ca3af",
    fontSize: "0.95rem",
    fontWeight: 500,
  },
  activeNavItem: {
    background: "rgba(139, 61, 255, 0.15)",
    color: "#fff",
    border: "1px solid rgba(139, 61, 255, 0.25)",
  },
  icon: {
    color: "#9ca3af",
  },
  activeIcon: {
    color: "#a78bfa",
  },
  navLabel: {
    fontWeight: 600,
  },
  logoutBtn: {
    display: "flex",
    alignItems: "center",
    gap: "14px",
    background: "none",
    border: "none",
    borderRadius: "14px",
    padding: "12px 16px",
    width: "100%",
    textAlign: "left" as const,
    color: "#ef4444",
    fontSize: "0.95rem",
    fontWeight: 500,
    marginTop: "20px",
  },
};
