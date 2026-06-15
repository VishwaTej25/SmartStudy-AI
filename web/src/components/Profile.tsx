import { useState, useEffect } from "react";
import { BookOpen, Award, BarChart3, Mail, GraduationCap, Edit2, Save, X } from "lucide-react";
import { db } from "../firebase";
import {
  collection,
  onSnapshot,
  query,
  orderBy,
  limit,
  doc,
  updateDoc,
} from "firebase/firestore";

interface ProfileProps {
  userId: string;
  userProfile: any;
}

export const Profile: React.FC<ProfileProps> = ({ userId, userProfile }) => {
  const [testHistory, setTestHistory] = useState<any[]>([]);
  const [enrolledCount, setEnrolledCount] = useState(0);
  const [loading, setLoading] = useState(true);

  // Edit modal state
  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState("");
  const [editCourse, setEditCourse] = useState("");
  const [editMobile, setEditMobile] = useState("");
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  // Listen to test attempts — ordered by createdAt (field set in TopicTest.tsx)
  useEffect(() => {
    const attemptsRef = collection(db, "users", userId, "testAttempts");
    const q = query(attemptsRef, orderBy("createdAt", "desc"), limit(20));

    const unsub = onSnapshot(
      q,
      (snapshot) => {
        const list = snapshot.docs.map((d) => ({ id: d.id, ...d.data() }));
        setTestHistory(list);
        setLoading(false);
      },
      (err) => {
        console.error("testAttempts error:", err);
        setLoading(false);
      }
    );
    return () => unsub();
  }, [userId]);

  // Listen to enrollments for real count
  useEffect(() => {
    const enrollRef = collection(db, "users", userId, "enrollments");
    const unsub = onSnapshot(enrollRef, (snap) => {
      setEnrolledCount(snap.size);
    });
    return () => unsub();
  }, [userId]);

  const openEdit = () => {
    setEditName(userProfile?.fullName || "");
    setEditCourse(userProfile?.course || "CSE");
    setEditMobile(userProfile?.mobile || "");
    setSaveError(null);
    setIsEditing(true);
  };

  const handleSaveProfile = async () => {
    if (!editName.trim()) {
      setSaveError("Name cannot be empty.");
      return;
    }
    setSaving(true);
    setSaveError(null);
    try {
      await updateDoc(doc(db, "users", userId), {
        fullName: editName.trim(),
        course: editCourse.trim(),
        mobile: editMobile.trim(),
      });
      setIsEditing(false);
    } catch (e: any) {
      setSaveError(e.message || "Failed to save profile.");
    } finally {
      setSaving(false);
    }
  };

  const avgScore =
    testHistory.length > 0
      ? Math.round(
          testHistory.reduce((sum, a) => sum + (a.percentage ?? 0), 0) /
            testHistory.length
        )
      : 0;

  return (
    <div style={styles.container} className="animate-fade-in">
      {/* Page Header */}
      <div style={styles.pageHeader}>
        <div>
          <h1 style={styles.title}>Student Profile 👤</h1>
          <p style={styles.subtitle}>
            Track your achievements and assessment history
          </p>
        </div>
        <button onClick={openEdit} style={styles.editTopBtn}>
          <Edit2 size={16} />
          Edit Profile
        </button>
      </div>

      {/* Profile Card */}
      <div style={styles.profileCard} className="glass-panel">
        <div style={styles.avatarRow}>
          <div style={styles.avatarBig}>
            {userProfile?.fullName?.[0]?.toUpperCase() || "L"}
          </div>
          <div style={styles.userInfo}>
            <h2 style={styles.userName}>{userProfile?.fullName || "Learner"}</h2>
            <div style={styles.userMeta}>
              <Mail size={15} color="#9ca3af" />
              <span>{userProfile?.email || "learner@smartstudy.edu"}</span>
            </div>
            <div style={styles.userMeta}>
              <GraduationCap size={15} color="#9ca3af" />
              <span>
                {userProfile?.course || "CSE"} · SmartStudy Academy
              </span>
            </div>
            {userProfile?.mobile && (
              <div style={styles.userMeta}>
                <span>📱</span>
                <span>{userProfile.mobile}</span>
              </div>
            )}
          </div>
        </div>

        <div style={styles.statsRow}>
          <div style={styles.statBox}>
            <BookOpen size={22} color="#8b3dff" />
            <div style={styles.statVal}>{enrolledCount}</div>
            <div style={styles.statLabel}>Enrolled</div>
          </div>
          <div style={styles.statBox}>
            <Award size={22} color="#facc15" />
            <div style={styles.statVal}>{testHistory.length}</div>
            <div style={styles.statLabel}>Exams Taken</div>
          </div>
          <div style={styles.statBox}>
            <BarChart3 size={22} color="#00b894" />
            <div style={styles.statVal}>{avgScore}%</div>
            <div style={styles.statLabel}>Avg Score</div>
          </div>
          <div style={styles.statBox}>
            <span style={{ fontSize: "1.4rem" }}>⚡</span>
            <div style={styles.statVal}>{userProfile?.xp || 0}</div>
            <div style={styles.statLabel}>XP Points</div>
          </div>
        </div>
      </div>

      {/* Exam History */}
      <div style={styles.historySection}>
        <h3 style={styles.sectionTitle}>📋 Exam Attempt History</h3>

        {loading ? (
          <p style={{ color: "#9ca3af" }}>Loading assessment records...</p>
        ) : testHistory.length === 0 ? (
          <div className="glass-panel" style={styles.emptyCard}>
            <BarChart3 size={36} color="#4b5563" />
            <p style={{ color: "#9ca3af", marginTop: "12px", fontSize: "1rem" }}>
              No assessments recorded yet. Head to{" "}
              <strong style={{ color: "#a78bfa" }}>Assessments</strong> to take
              your first test!
            </p>
          </div>
        ) : (
          <div style={styles.historyList}>
            {testHistory.map((attempt, idx) => {
              const pct =
                attempt.percentage ??
                Math.round(
                  (attempt.score * 100) / (attempt.totalQuestions || 1)
                );
              const dateMs = attempt.createdAt;
              const date = dateMs
                ? new Date(dateMs).toLocaleString()
                : "Recent";
              const color =
                pct >= 75 ? "#10b981" : pct >= 50 ? "#f59e0b" : "#ef4444";
              const bg =
                pct >= 75
                  ? "rgba(16,185,129,0.12)"
                  : pct >= 50
                  ? "rgba(245,158,11,0.12)"
                  : "rgba(239,68,68,0.12)";
              const label =
                pct >= 75 ? "✅ Passed" : pct >= 50 ? "⚠️ Average" : "❌ Needs Review";

              return (
                <div
                  key={attempt.id || idx}
                  className="glass-panel"
                  style={styles.historyCard}
                >
                  <div style={styles.historyMeta}>
                    <span style={styles.attemptNum}>
                      #{testHistory.length - idx}
                    </span>
                    <div style={styles.historyInfo}>
                      <h4 style={styles.historyTitle}>
                        {attempt.title || "Assessment"}
                      </h4>
                      <p style={styles.historyDate}>🕐 {date}</p>
                    </div>
                  </div>
                  <div style={styles.scoreRight}>
                    <span style={{ ...styles.scoreBadge, color, background: bg }}>
                      {attempt.score}/{attempt.totalQuestions} ({pct}%)
                    </span>
                    <span style={styles.passLabel}>{label}</span>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Edit Profile Modal */}
      {isEditing && (
        <div
          style={styles.overlay}
          onClick={(e) => {
            if (e.target === e.currentTarget) setIsEditing(false);
          }}
        >
          <div className="glass-panel" style={styles.modal}>
            <div style={styles.modalHeader}>
              <h3 style={styles.modalTitle}>✏️ Edit Profile</h3>
              <button
                onClick={() => setIsEditing(false)}
                style={styles.closeBtn}
              >
                <X size={20} />
              </button>
            </div>

            <div style={styles.formGroup}>
              <label style={styles.formLabel}>Full Name</label>
              <input
                style={styles.formInput}
                value={editName}
                onChange={(e) => setEditName(e.target.value)}
                placeholder="Your full name"
              />
            </div>
            <div style={styles.formGroup}>
              <label style={styles.formLabel}>Course / Major</label>
              <input
                style={styles.formInput}
                value={editCourse}
                onChange={(e) => setEditCourse(e.target.value)}
                placeholder="e.g. CSE, ECE, MCA"
              />
            </div>
            <div style={styles.formGroup}>
              <label style={styles.formLabel}>Mobile Number</label>
              <input
                style={styles.formInput}
                value={editMobile}
                onChange={(e) => setEditMobile(e.target.value)}
                placeholder="+91 9999999999"
              />
            </div>

            {saveError && (
              <p style={{ color: "#f87171", fontSize: "0.85rem" }}>
                {saveError}
              </p>
            )}

            <div style={styles.modalActions}>
              <button
                onClick={handleSaveProfile}
                disabled={saving}
                style={styles.saveBtn}
              >
                <Save size={16} />
                {saving ? "Saving..." : "Save Changes"}
              </button>
              <button
                onClick={() => setIsEditing(false)}
                style={styles.cancelBtn}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const styles: Record<string, any> = {
  container: {
    maxWidth: "820px",
    margin: "0 auto",
    display: "flex",
    flexDirection: "column",
    gap: "28px",
  },
  pageHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    flexWrap: "wrap",
    gap: "12px",
  },
  title: { fontSize: "2.4rem", color: "#fff", fontWeight: 800 },
  subtitle: { fontSize: "1.05rem", color: "#9ca3af" },
  editTopBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "rgba(139,61,255,0.12)",
    border: "1px solid rgba(139,61,255,0.3)",
    color: "#a78bfa",
    borderRadius: "12px",
    padding: "10px 18px",
    fontWeight: 600,
    fontSize: "0.9rem",
    cursor: "pointer",
  },
  profileCard: {
    padding: "30px",
    display: "flex",
    flexDirection: "column",
    gap: "28px",
  },
  avatarRow: {
    display: "flex",
    alignItems: "center",
    gap: "20px",
    flexWrap: "wrap",
  },
  avatarBig: {
    width: "80px",
    height: "80px",
    borderRadius: "50%",
    background: "linear-gradient(135deg, #8b3dff, #ec4899)",
    color: "#fff",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: 800,
    fontSize: "2.2rem",
    boxShadow: "0 0 24px rgba(139,61,255,0.4)",
    flexShrink: 0,
  },
  userInfo: {
    display: "flex",
    flexDirection: "column",
    gap: "6px",
    flexGrow: 1,
  },
  userName: { fontSize: "1.8rem", color: "#fff", fontWeight: 800 },
  userMeta: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    color: "#9ca3af",
    fontSize: "0.9rem",
  },
  statsRow: {
    display: "grid",
    gridTemplateColumns: "repeat(4, 1fr)",
    gap: "16px",
    borderTop: "1px solid rgba(255,255,255,0.08)",
    paddingTop: "24px",
  },
  statBox: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    gap: "8px",
    padding: "18px 10px",
    background: "rgba(255,255,255,0.02)",
    borderRadius: "16px",
    border: "1px solid rgba(255,255,255,0.05)",
  },
  statVal: { fontSize: "1.5rem", color: "#fff", fontWeight: 800 },
  statLabel: { fontSize: "0.8rem", color: "#9ca3af" },
  historySection: { display: "flex", flexDirection: "column", gap: "16px" },
  sectionTitle: { fontSize: "1.4rem", color: "#fff", fontWeight: 700 },
  emptyCard: {
    padding: "50px 40px",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
  },
  historyList: { display: "flex", flexDirection: "column", gap: "12px" },
  historyCard: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "18px 24px",
    gap: "16px",
  },
  historyMeta: { display: "flex", alignItems: "center", gap: "14px" },
  attemptNum: {
    background: "rgba(139,61,255,0.15)",
    color: "#a78bfa",
    borderRadius: "8px",
    padding: "4px 10px",
    fontWeight: 700,
    fontSize: "0.85rem",
    flexShrink: 0,
  },
  historyInfo: { display: "flex", flexDirection: "column", gap: "4px" },
  historyTitle: { fontSize: "1.05rem", color: "#fff", fontWeight: 700 },
  historyDate: { fontSize: "0.82rem", color: "#9ca3af" },
  scoreRight: {
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-end",
    gap: "6px",
    flexShrink: 0,
  },
  scoreBadge: {
    padding: "6px 14px",
    borderRadius: "10px",
    fontSize: "0.9rem",
    fontWeight: 700,
  },
  passLabel: { fontSize: "0.78rem", color: "#6b7280" },
  // Modal
  overlay: {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.7)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 999,
    padding: "20px",
  },
  modal: {
    width: "100%",
    maxWidth: "460px",
    padding: "32px",
    display: "flex",
    flexDirection: "column",
    gap: "20px",
  },
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  modalTitle: { fontSize: "1.4rem", color: "#fff", fontWeight: 700 },
  closeBtn: {
    background: "none",
    border: "none",
    color: "#9ca3af",
    cursor: "pointer",
    padding: "4px",
  },
  formGroup: { display: "flex", flexDirection: "column", gap: "8px" },
  formLabel: { fontSize: "0.85rem", fontWeight: 600, color: "#9ca3af" },
  formInput: {
    background: "rgba(255,255,255,0.05)",
    border: "1px solid rgba(255,255,255,0.1)",
    borderRadius: "10px",
    padding: "12px 16px",
    color: "#fff",
    fontSize: "0.95rem",
    outline: "none",
    width: "100%",
    boxSizing: "border-box",
  },
  modalActions: { display: "flex", gap: "12px", marginTop: "8px" },
  saveBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "10px",
    padding: "12px 24px",
    fontWeight: 600,
    cursor: "pointer",
    fontSize: "0.95rem",
  },
  cancelBtn: {
    background: "rgba(255,255,255,0.05)",
    border: "1px solid rgba(255,255,255,0.1)",
    color: "#9ca3af",
    borderRadius: "10px",
    padding: "12px 20px",
    fontWeight: 600,
    cursor: "pointer",
  },
};
