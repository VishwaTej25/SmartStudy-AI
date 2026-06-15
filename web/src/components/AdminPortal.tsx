import React, { useState, useEffect } from "react";
import { db } from "../firebase";
import { 
  collection, 
  onSnapshot, 
  doc, 
  setDoc, 
  deleteDoc, 
  updateDoc 
} from "firebase/firestore";
import { 
  BookOpen, 
  Users, 
  Plus, 
  Edit, 
  Trash2, 
  Save, 
  X, 
  TrendingUp, 
  Award 
} from "lucide-react";
import type { Course } from "./Courses";

interface AdminPortalProps {
  onBack: () => void;
}

interface UserProfile {
  uid: string;
  fullName: string;
  email: string;
  xp: number;
  streak: number;
}

export const AdminPortal: React.FC<AdminPortalProps> = ({ onBack }) => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [users, setUsers] = useState<UserProfile[]>([]);
  const [activeTab, setActiveTab] = useState<"courses" | "users">("courses");

  // Course editing state
  const [editingCourseId, setEditingCourseId] = useState<string | null>(null);
  const [editTitle, setEditTitle] = useState("");
  const [editSubtitle, setEditSubtitle] = useState("");
  const [editEmoji, setEditEmoji] = useState("");
  const [editOrder, setEditOrder] = useState(1);

  // New course state
  const [showAddCourse, setShowAddCourse] = useState(false);
  const [newCourseId, setNewCourseId] = useState("");
  const [newTitle, setNewTitle] = useState("");
  const [newSubtitle, setNewSubtitle] = useState("");
  const [newEmoji, setNewEmoji] = useState("");
  const [newOrder, setNewOrder] = useState(1);

  // User editing state
  const [editingUserId, setEditingUserId] = useState<string | null>(null);
  const [editUserXp, setEditUserXp] = useState(0);
  const [editUserStreak, setEditUserStreak] = useState(0);

  // Listen to courses & users
  useEffect(() => {
    const unsubCourses = onSnapshot(collection(db, "courses"), (snapshot) => {
      const list = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() })) as Course[];
      setCourses(list.sort((a, b) => a.order - b.order));
    });

    const unsubUsers = onSnapshot(collection(db, "users"), (snapshot) => {
      const list = snapshot.docs.map(doc => ({ uid: doc.id, ...doc.data() })) as UserProfile[];
      setUsers(list);
    });

    return () => {
      unsubCourses();
      unsubUsers();
    };
  }, []);

  // Save course edit
  const handleSaveCourse = async (courseId: string) => {
    try {
      await updateDoc(doc(db, "courses", courseId), {
        title: editTitle,
        subtitle: editSubtitle,
        emoji: editEmoji,
        order: editOrder
      });
      setEditingCourseId(null);
    } catch (e) {
      console.error(e);
      alert("Failed to update course.");
    }
  };

  // Add course
  const handleAddCourse = async () => {
    if (!newCourseId || !newTitle) return;
    try {
      await setDoc(doc(db, "courses", newCourseId.trim().toLowerCase()), {
        id: newCourseId.trim().toLowerCase(),
        title: newTitle.trim(),
        subtitle: newSubtitle.trim(),
        emoji: newEmoji.trim() || "📚",
        order: newOrder
      });
      setShowAddCourse(false);
      setNewCourseId("");
      setNewTitle("");
      setNewSubtitle("");
      setNewEmoji("");
      setNewOrder(1);
    } catch (e) {
      console.error(e);
      alert("Failed to add course.");
    }
  };

  // Delete course
  const handleDeleteCourse = async (courseId: string) => {
    if (!window.confirm("Are you sure you want to delete this course?")) return;
    try {
      await deleteDoc(doc(db, "courses", courseId));
    } catch (e) {
      console.error(e);
    }
  };

  // Edit user XP / Streak
  const handleSaveUser = async (userId: string) => {
    try {
      await updateDoc(doc(db, "users", userId), {
        xp: Number(editUserXp),
        streak: Number(editUserStreak)
      });
      setEditingUserId(null);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <div>
            <h1 style={styles.title}>Admin Control Portal 🛡️</h1>
            <p style={styles.subtitle}>Manage platform courses, seed content, and edit student user profiles.</p>
          </div>
          <button onClick={onBack} style={styles.exitBtn}>
            Exit Portal
          </button>
        </div>
      </div>

      <div style={styles.tabsRow}>
        <button 
          onClick={() => setActiveTab("courses")}
          style={{ ...styles.tabBtn, ...(activeTab === "courses" ? styles.activeTabBtn : {}) }}
        >
          <BookOpen size={18} />
          Manage Courses
        </button>
        <button 
          onClick={() => setActiveTab("users")}
          style={{ ...styles.tabBtn, ...(activeTab === "users" ? styles.activeTabBtn : {}) }}
        >
          <Users size={18} />
          Student Users
        </button>
      </div>

      {activeTab === "courses" && (
        <div style={styles.section} className="animate-fade-in">
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
            <h3 style={styles.sectionTitle}>Course Syllabus List</h3>
            <button onClick={() => setShowAddCourse(true)} style={styles.addBtn}>
              <Plus size={16} />
              Add New Course
            </button>
          </div>

          {showAddCourse && (
            <div className="glass-panel" style={styles.formCard}>
              <h4 style={{ color: "#fff", marginBottom: "16px" }}>Add New Course Config</h4>
              <div style={styles.gridForm}>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>Course ID (lowercase slug, e.g. "os")</label>
                  <input type="text" value={newCourseId} onChange={e => setNewCourseId(e.target.value)} placeholder="os" />
                </div>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>Emoji</label>
                  <input type="text" value={newEmoji} onChange={e => setNewEmoji(e.target.value)} placeholder="⚙️" />
                </div>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>Title</label>
                  <input type="text" value={newTitle} onChange={e => setNewTitle(e.target.value)} placeholder="Operating Systems" />
                </div>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>Order Index</label>
                  <input type="number" value={newOrder} onChange={e => setNewOrder(Number(e.target.value))} />
                </div>
                <div style={{ ...styles.inputGroup, gridColumn: "span 2" }}>
                  <label style={styles.label}>Subtitle / Description</label>
                  <input type="text" value={newSubtitle} onChange={e => setNewSubtitle(e.target.value)} placeholder="OS Concepts & CPU scheduling" />
                </div>
              </div>
              <div style={styles.formActions}>
                <button onClick={handleAddCourse} style={styles.saveBtn}>Create Course</button>
                <button onClick={() => setShowAddCourse(false)} style={styles.cancelBtn}>Cancel</button>
              </div>
            </div>
          )}

          <div style={styles.list}>
            {courses.map((course) => {
              const isEditing = editingCourseId === course.id;
              return (
                <div key={course.id} className="glass-panel" style={styles.card}>
                  {isEditing ? (
                    <div style={{ display: "flex", flexDirection: "column", gap: "16px", width: "100%" }}>
                      <div style={styles.gridForm}>
                        <div style={styles.inputGroup}>
                          <label style={styles.label}>Emoji</label>
                          <input type="text" value={editEmoji} onChange={e => setEditEmoji(e.target.value)} />
                        </div>
                        <div style={styles.inputGroup}>
                          <label style={styles.label}>Title</label>
                          <input type="text" value={editTitle} onChange={e => setEditTitle(e.target.value)} />
                        </div>
                        <div style={styles.inputGroup}>
                          <label style={styles.label}>Order</label>
                          <input type="number" value={editOrder} onChange={e => setEditOrder(Number(e.target.value))} />
                        </div>
                        <div style={{ ...styles.inputGroup, gridColumn: "span 2" }}>
                          <label style={styles.label}>Subtitle</label>
                          <input type="text" value={editSubtitle} onChange={e => setEditSubtitle(e.target.value)} />
                        </div>
                      </div>
                      <div style={styles.formActions}>
                        <button onClick={() => handleSaveCourse(course.id)} style={styles.saveBtn}>
                          <Save size={16} /> Save
                        </button>
                        <button onClick={() => setEditingCourseId(null)} style={styles.cancelBtn}>
                          <X size={16} /> Cancel
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", width: "100%" }}>
                      <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
                        <span style={{ fontSize: "2.2rem" }}>{course.emoji}</span>
                        <div>
                          <h4 style={styles.courseTitle}>{course.title} <span style={styles.orderLabel}>Order: {course.order}</span></h4>
                          <p style={styles.courseSubtitle}>{course.subtitle}</p>
                        </div>
                      </div>
                      <div style={styles.actions}>
                        <button 
                          onClick={() => {
                            setEditingCourseId(course.id);
                            setEditTitle(course.title);
                            setEditSubtitle(course.subtitle);
                            setEditEmoji(course.emoji);
                            setEditOrder(course.order);
                          }} 
                          style={styles.actionBtnEdit}
                        >
                          <Edit size={16} />
                        </button>
                        <button onClick={() => handleDeleteCourse(course.id)} style={styles.actionBtnDelete}>
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {activeTab === "users" && (
        <div style={styles.section} className="animate-fade-in">
          <h3 style={styles.sectionTitle}>Registered Student Users</h3>
          <div style={styles.list}>
            {users.map((u) => {
              const isEditing = editingUserId === u.uid;
              return (
                <div key={u.uid} className="glass-panel" style={styles.card}>
                  {isEditing ? (
                    <div style={{ display: "flex", flexDirection: "column", gap: "16px", width: "100%" }}>
                      <h4 style={{ color: "#fff" }}>Edit Stats for {u.fullName}</h4>
                      <div style={styles.gridForm}>
                        <div style={styles.inputGroup}>
                          <label style={styles.label}>XP Points</label>
                          <input type="number" value={editUserXp} onChange={e => setEditUserXp(Number(e.target.value))} />
                        </div>
                        <div style={styles.inputGroup}>
                          <label style={styles.label}>Day Streak</label>
                          <input type="number" value={editUserStreak} onChange={e => setEditUserStreak(Number(e.target.value))} />
                        </div>
                      </div>
                      <div style={styles.formActions}>
                        <button onClick={() => handleSaveUser(u.uid)} style={styles.saveBtn}>
                          <Save size={16} /> Save Stats
                        </button>
                        <button onClick={() => setEditingUserId(null)} style={styles.cancelBtn}>
                          <X size={16} /> Cancel
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", width: "100%" }}>
                      <div>
                        <h4 style={styles.userTitle}>{u.fullName || "Unnamed student"}</h4>
                        <p style={styles.userSub}>{u.email}</p>
                        <div style={styles.statsMeta}>
                          <Award size={14} color="#8b3dff" />
                          <span>XP: {u.xp || 0}</span>
                          <span style={styles.metaDot}>&bull;</span>
                          <TrendingUp size={14} color="#00b894" />
                          <span>Streak: {u.streak || 0} days</span>
                        </div>
                      </div>
                      <button 
                        onClick={() => {
                          setEditingUserId(u.uid);
                          setEditUserXp(u.xp || 0);
                          setEditUserStreak(u.streak || 0);
                        }} 
                        style={styles.actionBtnEdit}
                      >
                        <Edit size={16} /> Edit Stats
                      </button>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "800px",
    margin: "0 auto",
    display: "flex",
    flexDirection: "column" as const,
    gap: "24px",
  },
  header: {
    marginBottom: "10px",
  },
  title: {
    fontSize: "2.4rem",
    color: "#fff",
    fontWeight: 800,
  },
  subtitle: {
    fontSize: "1.05rem",
    color: "#9ca3af",
  },
  exitBtn: {
    background: "rgba(255, 255, 255, 0.05)",
    border: "1px solid rgba(255, 255, 255, 0.08)",
    borderRadius: "12px",
    padding: "10px 20px",
    color: "#fff",
    fontWeight: 600,
    cursor: "pointer",
  },
  tabsRow: {
    display: "flex",
    gap: "16px",
    borderBottom: "1px solid rgba(255, 255, 255, 0.08)",
    paddingBottom: "12px",
  },
  tabBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "none",
    border: "none",
    color: "#9ca3af",
    fontSize: "1rem",
    fontWeight: 600,
    padding: "8px 16px",
    cursor: "pointer",
  },
  activeTabBtn: {
    color: "#8b3dff",
    borderBottom: "2px solid #8b3dff",
  },
  section: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  sectionTitle: {
    fontSize: "1.4rem",
    color: "#fff",
    fontWeight: 700,
  },
  addBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "10px",
    padding: "8px 16px",
    fontWeight: 600,
    fontSize: "0.9rem",
    cursor: "pointer",
  },
  formCard: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  gridForm: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "16px",
  },
  inputGroup: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "8px",
  },
  label: {
    fontSize: "0.85rem",
    fontWeight: 600,
    color: "#9ca3af",
  },
  formActions: {
    display: "flex",
    gap: "12px",
    marginTop: "10px",
  },
  saveBtn: {
    background: "#00b894",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    padding: "8px 16px",
    fontWeight: 600,
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    gap: "6px",
  },
  cancelBtn: {
    background: "rgba(255, 255, 255, 0.05)",
    border: "1px solid rgba(255, 255, 255, 0.08)",
    color: "#fff",
    borderRadius: "8px",
    padding: "8px 16px",
    fontWeight: 600,
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    gap: "6px",
  },
  list: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "12px",
  },
  card: {
    padding: "20px 24px",
    display: "flex",
  },
  courseTitle: {
    fontSize: "1.15rem",
    color: "#fff",
    fontWeight: 700,
    display: "flex",
    alignItems: "center",
    gap: "8px",
  },
  orderLabel: {
    fontSize: "0.75rem",
    background: "rgba(139, 61, 255, 0.15)",
    color: "#a78bfa",
    padding: "2px 8px",
    borderRadius: "20px",
  },
  courseSubtitle: {
    fontSize: "0.95rem",
    color: "#9ca3af",
    marginTop: "4px",
  },
  actions: {
    display: "flex",
    gap: "8px",
  },
  actionBtnEdit: {
    background: "rgba(139, 61, 255, 0.12)",
    color: "#8b3dff",
    border: "1px solid rgba(139, 61, 255, 0.25)",
    borderRadius: "8px",
    padding: "6px 12px",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    gap: "6px",
    fontSize: "0.85rem",
    fontWeight: 600,
  },
  actionBtnDelete: {
    background: "rgba(239, 68, 68, 0.12)",
    color: "#f87171",
    border: "1px solid rgba(239, 68, 68, 0.25)",
    borderRadius: "8px",
    padding: "6px 10px",
    cursor: "pointer",
  },
  userTitle: {
    fontSize: "1.15rem",
    color: "#fff",
    fontWeight: 700,
  },
  userSub: {
    fontSize: "0.9rem",
    color: "#9ca3af",
    marginTop: "2px",
  },
  statsMeta: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    fontSize: "0.85rem",
    color: "#9ca3af",
    marginTop: "8px",
  },
  metaDot: {
    color: "#4b5563",
  }
};
