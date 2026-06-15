import React from "react";
import { Settings as SettingsIcon, LogOut, Eye, Mic, Bell } from "lucide-react";
import { auth, db } from "../firebase";
import { doc, setDoc } from "firebase/firestore";

interface SettingsProps {
  userId: string;
  settings: any;
  onLogout: () => void;
}

export const Settings: React.FC<SettingsProps> = ({ userId, settings, onLogout }) => {
  const handleToggle = async (key: string, currentValue: boolean) => {
    try {
      const settingsRef = doc(db, "users", userId, "private", "settings");
      await setDoc(settingsRef, {
        ...settings,
        [key]: !currentValue,
        updatedAt: Date.now()
      }, { merge: true });
    } catch (err) {
      console.error("Failed to update settings:", err);
    }
  };

  const handleLogout = async () => {
    await auth.signOut();
    onLogout();
  };

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <SettingsIcon size={28} color="#8b3dff" />
        <h1 style={styles.title}>Settings ⚙️</h1>
      </div>

      <div style={styles.list}>
        {/* Dark Mode */}
        <div className="glass-panel" style={styles.card}>
          <div style={styles.cardLeft}>
            <Eye size={22} color="#8b3dff" />
            <div>
              <div style={styles.settingTitle}>Dark Mode</div>
              <div style={styles.settingDesc}>Use low-light visual styles.</div>
            </div>
          </div>
          <input
            type="checkbox"
            checked={settings.darkMode !== false} // default to true
            onChange={() => handleToggle("darkMode", settings.darkMode !== false)}
            style={styles.toggle}
          />
        </div>

        {/* AI Voice Assistant */}
        <div className="glass-panel" style={styles.card}>
          <div style={styles.cardLeft}>
            <Mic size={22} color="#00b894" />
            <div>
              <div style={styles.settingTitle}>AI Voice Assistant</div>
              <div style={styles.settingDesc}>Hear study guides read aloud.</div>
            </div>
          </div>
          <input
            type="checkbox"
            checked={settings.aiVoice !== false} // default to true
            onChange={() => handleToggle("aiVoice", settings.aiVoice !== false)}
            style={styles.toggle}
          />
        </div>

        {/* Notifications */}
        <div className="glass-panel" style={styles.card}>
          <div style={styles.cardLeft}>
            <Bell size={22} color="#e11d48" />
            <div>
              <div style={styles.settingTitle}>Smart Notifications</div>
              <div style={styles.settingDesc}>Get study alerts and streaks.</div>
            </div>
          </div>
          <input
            type="checkbox"
            checked={settings.notifications !== false} // default to true
            onChange={() => handleToggle("notifications", settings.notifications !== false)}
            style={styles.toggle}
          />
        </div>

        {/* Logout */}
        <button onClick={handleLogout} style={styles.logoutBtn}>
          <LogOut size={20} />
          Logout from Account
        </button>
      </div>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "600px",
    margin: "0 auto",
    display: "flex",
    flexDirection: "column" as const,
    gap: "30px",
  },
  header: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    marginBottom: "10px",
  },
  title: {
    fontSize: "2.4rem",
    fontWeight: 800,
    color: "#fff",
  },
  list: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  card: {
    padding: "20px 24px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  cardLeft: {
    display: "flex",
    alignItems: "center",
    gap: "18px",
  },
  settingTitle: {
    fontSize: "1.1rem",
    fontWeight: 600,
    color: "#fff",
  },
  settingDesc: {
    fontSize: "0.85rem",
    color: "#9ca3af",
    marginTop: "2px",
  },
  toggle: {
    width: "48px",
    height: "24px",
    accentColor: "#8b3dff",
    cursor: "pointer",
  },
  logoutBtn: {
    background: "#ef4444",
    color: "#fff",
    border: "none",
    borderRadius: "14px",
    padding: "16px",
    fontSize: "1rem",
    fontWeight: 600,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "10px",
    marginTop: "20px",
    boxShadow: "0 4px 12px rgba(239, 68, 68, 0.2)",
  },
};
