import React, { useState, useEffect } from "react";
import { db } from "../firebase";
import { collection, onSnapshot, addDoc, doc, updateDoc, query, orderBy } from "firebase/firestore";

interface PlannerProps {
  userId: string;
}

interface StudyPlan {
  id: string;
  subject: string;
  time: string;
  priority: "High" | "Medium" | "Low";
  completed: boolean;
  createdAt: number;
}

export const Planner: React.FC<PlannerProps> = ({ userId }) => {
  const [plans, setPlans] = useState<StudyPlan[]>([]);
  const [subject, setSubject] = useState("");
  const [time, setTime] = useState("");
  const [priority, setPriority] = useState<"High" | "Medium" | "Low">("High");
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const plansRef = collection(db, "users", userId, "plans");
    const q = query(plansRef, orderBy("createdAt", "desc"));

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const fetched = snapshot.docs.map((doc) => ({
        id: doc.id,
        ...doc.data()
      })) as StudyPlan[];
      setPlans(fetched);
    }, (err) => {
      setError(err.message);
    });

    return () => unsubscribe();
  }, [userId]);

  const handleAddPlan = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subject.trim() || !time.trim()) return;

    setError(null);
    try {
      const plansRef = collection(db, "users", userId, "plans");
      await addDoc(plansRef, {
        subject: subject.trim(),
        time: time.trim(),
        priority,
        completed: false,
        createdAt: Date.now()
      });
      setSubject("");
      setTime("");
    } catch (err: any) {
      setError(err.message || "Failed to add study plan.");
    }
  };

  const handleToggleComplete = async (plan: StudyPlan) => {
    try {
      const planRef = doc(db, "users", userId, "plans", plan.id);
      await updateDoc(planRef, {
        completed: !plan.completed
      });
    } catch (err: any) {
      setError(err.message || "Failed to update plan.");
    }
  };

  const completedCount = plans.filter(p => p.completed).length;
  const progressPercent = plans.length > 0 ? Math.round((completedCount / plans.length) * 100) : 0;

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <h1 style={styles.title}>Study Planner 📅</h1>
        <p style={styles.subtitle}>Organize your daily study schedule and track your accomplishments.</p>
      </div>

      {error && <div style={styles.error}>{error}</div>}

      <div style={styles.contentGrid}>
        {/* Form panel */}
        <div className="glass-panel" style={styles.formPanel}>
          <h3 style={styles.panelTitle}>Add New Study Goal</h3>
          <form onSubmit={handleAddPlan} style={styles.form}>
            <div style={styles.inputGroup}>
              <label style={styles.label}>Subject / Topic</label>
              <input
                type="text"
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                placeholder="e.g. Practice DSA Problems"
                required
              />
            </div>

            <div style={styles.inputGroup}>
              <label style={styles.label}>Study Time</label>
              <input
                type="text"
                value={time}
                onChange={(e) => setTime(e.target.value)}
                placeholder="e.g. 11:00 AM"
                required
              />
            </div>

            <div style={styles.inputGroup}>
              <label style={styles.label}>Priority Level</label>
              <select
                value={priority}
                onChange={(e) => setPriority(e.target.value as any)}
                style={styles.select}
              >
                <option value="High">🔴 High Priority</option>
                <option value="Medium">🟡 Medium Priority</option>
                <option value="Low">🟢 Low Priority</option>
              </select>
            </div>

            <button type="submit" style={styles.addBtn}>Add to Schedule</button>
          </form>
        </div>

        {/* Progress & Task List Panel */}
        <div style={styles.listPanel}>
          <div className="glass-panel" style={styles.progressCard}>
            <h3 style={styles.progressTitle}>Today's Progress</h3>
            <div style={styles.progressMeta}>
              <span style={styles.progressStats}>{completedCount} / {plans.length} Tasks Completed</span>
              <span style={styles.progressPercent}>{progressPercent}%</span>
            </div>
            <div style={styles.barOuter}>
              <div style={{ ...styles.barInner, width: `${progressPercent}%` }} />
            </div>
          </div>

          <div style={styles.plansList}>
            {plans.length === 0 ? (
              <div className="glass-panel" style={styles.emptyCard}>
                <p>No study tasks planned for today. Add tasks on the left to get started!</p>
              </div>
            ) : (
              plans.map((plan) => (
                <div key={plan.id} className="glass-panel" style={styles.planCard}>
                  <div style={styles.planLeft}>
                    <input
                      type="checkbox"
                      checked={plan.completed}
                      onChange={() => handleToggleComplete(plan)}
                      style={styles.checkbox}
                    />
                    <div style={styles.planDetails}>
                      <span style={{
                        ...styles.planSubject,
                        textDecoration: plan.completed ? "line-through" : "none",
                        color: plan.completed ? "#6b7280" : "#fff",
                      }}>
                        {plan.subject}
                      </span>
                      <span style={styles.planTime}>{plan.time}</span>
                    </div>
                  </div>

                  <span 
                    style={{
                      ...styles.priorityBadge,
                      background: plan.priority === "High" ? "rgba(239, 68, 68, 0.15)" : plan.priority === "Medium" ? "rgba(245, 158, 11, 0.15)" : "rgba(16, 185, 129, 0.15)",
                      color: plan.priority === "High" ? "#ef4444" : plan.priority === "Medium" ? "#f59e0b" : "#10b981",
                      borderColor: plan.priority === "High" ? "rgba(239, 68, 68, 0.3)" : plan.priority === "Medium" ? "rgba(245, 158, 11, 0.3)" : "rgba(16, 185, 129, 0.3)",
                    }}
                  >
                    {plan.priority}
                  </span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "1000px",
    margin: "0 auto",
    display: "flex",
    flexDirection: "column" as const,
    gap: "30px",
  },
  header: {
    marginBottom: "10px",
  },
  title: {
    fontSize: "2.4rem",
    fontWeight: 800,
    color: "#fff",
  },
  subtitle: {
    fontSize: "1.05rem",
    color: "#9ca3af",
  },
  error: {
    background: "rgba(239, 68, 68, 0.15)",
    border: "1px solid rgba(239, 68, 68, 0.3)",
    borderRadius: "10px",
    color: "#f87171",
    padding: "10px",
    fontSize: "0.85rem",
  },
  contentGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1.4fr",
    gap: "30px",
  },
  formPanel: {
    padding: "24px",
    height: "fit-content",
  },
  panelTitle: {
    fontSize: "1.3rem",
    color: "#fff",
    marginBottom: "20px",
  },
  form: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "20px",
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
  select: {
    width: "100%",
    backgroundColor: "rgba(15, 23, 42, 0.6)",
    border: "1px solid rgba(255, 255, 255, 0.1)",
    borderRadius: "12px",
    padding: "12px 16px",
    color: "#fff",
    outline: "none",
  },
  addBtn: {
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "14px",
    fontSize: "1rem",
    fontWeight: 600,
    marginTop: "10px",
    boxShadow: "0 4px 12px rgba(139, 61, 255, 0.3)",
  },
  listPanel: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "20px",
  },
  progressCard: {
    padding: "20px",
  },
  progressTitle: {
    fontSize: "1.1rem",
    color: "#fff",
    marginBottom: "12px",
  },
  progressMeta: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  progressStats: {
    fontSize: "0.9rem",
    color: "#9ca3af",
  },
  progressPercent: {
    fontSize: "1.2rem",
    fontWeight: 700,
    color: "#8b3dff",
  },
  barOuter: {
    height: "10px",
    width: "100%",
    backgroundColor: "rgba(255, 255, 255, 0.08)",
    borderRadius: "10px",
    overflow: "hidden",
  },
  barInner: {
    height: "100%",
    backgroundColor: "#8b3dff",
    borderRadius: "10px",
    transition: "width 0.4s ease-out",
  },
  plansList: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "12px",
  },
  emptyCard: {
    padding: "30px",
    textAlign: "center" as const,
    color: "#9ca3af",
  },
  planCard: {
    padding: "16px 20px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  planLeft: {
    display: "flex",
    alignItems: "center",
    gap: "16px",
  },
  checkbox: {
    width: "20px",
    height: "20px",
    accentColor: "#8b3dff",
    cursor: "pointer",
  },
  planDetails: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "4px",
  },
  planSubject: {
    fontSize: "1.05rem",
    fontWeight: 600,
  },
  planTime: {
    fontSize: "0.8rem",
    color: "#9ca3af",
  },
  priorityBadge: {
    fontSize: "0.75rem",
    fontWeight: 700,
    padding: "4px 10px",
    borderRadius: "8px",
    border: "1px solid",
    textTransform: "uppercase" as const,
  },
};
