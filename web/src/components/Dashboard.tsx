import { BookOpen, CheckSquare, Award, Zap } from "lucide-react";

interface DashboardProps {
  userProfile: any;
  plans: any[];
  enrollments: any;
  setCurrentTab: (tab: string) => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ 
  userProfile, 
  plans, 
  enrollments, 
  setCurrentTab 
}) => {
  const completedPlansCount = plans.filter(p => p.completed).length;
  const enrolledCoursesCount = Object.keys(enrollments || {}).length;

  const weeklyData = [
    { day: "Mon", value: 65 },
    { day: "Tue", value: 45 },
    { day: "Wed", value: 95 },
    { day: "Thu", value: 60 },
    { day: "Fri", value: 100 },
    { day: "Sat", value: 80 },
    { day: "Sun", value: 75 },
  ];

  return (
    <div style={styles.container} className="animate-fade-in">
      {/* Header */}
      <div style={styles.header}>
        <h1 style={styles.greeting}>Hello {userProfile?.fullName || "Learner"} 👋</h1>
        <p style={styles.subGreeting}>AI powered learning dashboard 🚀</p>
      </div>

      {/* Hero Streak Card */}
      <div className="glass-panel" style={styles.streakCard}>
        <div style={styles.streakInfo}>
          <span style={styles.streakLabel}>🔥 Study Streak</span>
          <span style={styles.streakVal}>{userProfile?.streak || 1} Days</span>
          <span style={styles.streakSub}>Keep it up! You are doing great.</span>
        </div>
        <div style={styles.streakIconContainer}>
          <span style={styles.streakIcon}>🔥</span>
        </div>
      </div>

      {/* Grid of Profile and Stats */}
      <div style={styles.statsGrid}>
        {/* Profile Details Card */}
        <div className="glass-panel" style={styles.profileCard}>
          <h3 style={styles.profileTitle}>👤 {userProfile?.fullName || "Learner"}</h3>
          <div style={styles.profileMeta}>
            <p style={styles.metaItem}>Department: <span style={styles.whiteText}>CSE</span></p>
            <p style={styles.metaItem}>Year: <span style={styles.whiteText}>4th Year</span></p>
            <p style={styles.metaItem}>College: <span style={styles.whiteText}>SIMATS</span></p>
          </div>
          <div style={styles.keepLearning}>Keep learning 🚀</div>
        </div>

        {/* Small Stats Grid */}
        <div style={styles.miniStatsGrid}>
          <div className="glass-panel" style={styles.miniCard}>
            <BookOpen size={24} color="#8b3dff" />
            <div style={styles.miniVal}>{enrolledCoursesCount || 4}</div>
            <div style={styles.miniLabel}>Courses Enrolled</div>
          </div>
          <div className="glass-panel" style={styles.miniCard}>
            <CheckSquare size={24} color="#00b894" />
            <div style={styles.miniVal}>{completedPlansCount || 2}</div>
            <div style={styles.miniLabel}>Done Today</div>
          </div>
          <div className="glass-panel" style={styles.miniCard}>
            <Award size={24} color="#facc15" />
            <div style={styles.miniVal}>15</div>
            <div style={styles.miniLabel}>Badges Earned</div>
          </div>
          <div className="glass-panel" style={styles.miniCard}>
            <Zap size={24} color="#ec4899" />
            <div style={styles.miniVal}>{userProfile?.xp || 120}</div>
            <div style={styles.miniLabel}>XP Points</div>
          </div>
        </div>
      </div>

      {/* Quick Access */}
      <div style={styles.sectionTitle}>Quick Access</div>
      <div style={styles.quickAccessGrid}>
        <div className="glass-panel clickable" style={styles.quickCard} onClick={() => setCurrentTab("courses")}>
          <span style={styles.quickEmoji}>📚</span>
          <span style={styles.quickLabel}>Courses</span>
        </div>
        <div className="glass-panel clickable" style={styles.quickCard} onClick={() => setCurrentTab("planner")}>
          <span style={styles.quickEmoji}>✅</span>
          <span style={styles.quickLabel}>Tasks</span>
        </div>
        <div className="glass-panel clickable" style={styles.quickCard} onClick={() => setCurrentTab("practice")}>
          <span style={styles.quickEmoji}>🎯</span>
          <span style={styles.quickLabel}>Practice</span>
        </div>
        <div className="glass-panel clickable" style={styles.quickCard} onClick={() => setCurrentTab("leaderboard")}>
          <span style={styles.quickEmoji}>📈</span>
          <span style={styles.quickLabel}>Rankings</span>
        </div>
      </div>

      {/* Performance Layout Grid */}
      <div style={styles.analyticsGrid}>
        {/* Left Column: AI Prediction & Weekly Graph */}
        <div style={styles.leftColumn}>
          <div className="glass-panel" style={styles.sectionCard}>
            <h3 style={styles.analyticsTitle}>AI Prediction Score 🎯</h3>
            <p style={styles.analyticsText}>91% probability of scoring above A grade</p>
            <div style={styles.progressContainer}>
              <div style={{ ...styles.progressBar, width: "91%" }} />
            </div>
          </div>

          <div className="glass-panel" style={styles.graphCard}>
            <h3 style={styles.graphTitle}>Weekly Student Performance 📈</h3>
            <div style={styles.chart}>
              {weeklyData.map((d, index) => (
                <div key={index} style={styles.barContainer}>
                  <div 
                    style={{ 
                      ...styles.bar, 
                      height: `${d.value}%`,
                      background: d.value > 80 ? "linear-gradient(180deg, #8b3dff 0%, #a78bfa 100%)" : "rgba(139, 61, 255, 0.6)"
                    }} 
                  />
                  <span style={styles.barLabel}>{d.day}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column: Today's Plan */}
        <div className="glass-panel" style={styles.planCard}>
          <h3 style={styles.planTitle}>Today's Plan 📅</h3>
          <div style={styles.plansList}>
            {plans.length === 0 ? (
              <div style={styles.noPlan}>
                <p>No plans added for today.</p>
                <button style={styles.addPlanBtn} onClick={() => setCurrentTab("planner")}>Go to Planner</button>
              </div>
            ) : (
              plans.slice(0, 3).map((plan) => (
                <div key={plan.id} style={styles.planItem}>
                  <div>
                    <div style={styles.planSubject}>{plan.subject}</div>
                    <div style={styles.planDesc}>Smart Study Daily Goal</div>
                  </div>
                  <div style={styles.planTimeContainer}>
                    <span style={styles.planTime}>{plan.time}</span>
                    <span 
                      style={{
                        ...styles.priorityBadge,
                        background: plan.priority === "High" ? "rgba(239, 68, 68, 0.2)" : plan.priority === "Medium" ? "rgba(245, 158, 11, 0.2)" : "rgba(16, 185, 129, 0.2)",
                        color: plan.priority === "High" ? "#ef4444" : plan.priority === "Medium" ? "#f59e0b" : "#10b981",
                      }}
                    >
                      {plan.priority}
                    </span>
                  </div>
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
    maxWidth: "1100px",
    margin: "0 auto",
    display: "flex",
    flexDirection: "column" as const,
    gap: "30px",
  },
  header: {
    marginBottom: "10px",
  },
  greeting: {
    fontSize: "2.4rem",
    fontWeight: 800,
    color: "#fff",
  },
  subGreeting: {
    fontSize: "1.05rem",
    color: "#9ca3af",
  },
  streakCard: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "30px 40px",
    background: "linear-gradient(135deg, rgba(139, 61, 255, 0.8) 0%, rgba(109, 40, 217, 0.8) 100%)",
    border: "1px solid rgba(255, 255, 255, 0.15)",
    boxShadow: "0 8px 32px rgba(139, 61, 255, 0.25)",
  },
  streakInfo: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "8px",
  },
  streakLabel: {
    fontSize: "0.95rem",
    fontWeight: 600,
    color: "rgba(255, 255, 255, 0.8)",
  },
  streakVal: {
    fontSize: "2.6rem",
    fontWeight: 800,
    color: "#fff",
  },
  streakSub: {
    fontSize: "0.9rem",
    color: "rgba(255, 255, 255, 0.8)",
  },
  streakIconContainer: {
    width: "90px",
    height: "90px",
    borderRadius: "50%",
    background: "rgba(255, 255, 255, 0.15)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    backdropFilter: "blur(5px)",
  },
  streakIcon: {
    fontSize: "2.5rem",
  },
  statsGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1.5fr",
    gap: "24px",
  },
  profileCard: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    justifyContent: "space-between",
  },
  profileTitle: {
    fontSize: "1.4rem",
    color: "#fff",
    marginBottom: "16px",
  },
  profileMeta: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "10px",
  },
  metaItem: {
    color: "#9ca3af",
    fontSize: "0.95rem",
  },
  whiteText: {
    color: "#fff",
    fontWeight: 600,
  },
  keepLearning: {
    marginTop: "20px",
    color: "#a78bfa",
    fontWeight: 600,
    fontSize: "0.95rem",
  },
  miniStatsGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "16px",
  },
  miniCard: {
    padding: "20px",
    display: "flex",
    flexDirection: "column" as const,
    justifyContent: "center",
    alignItems: "center",
    textAlign: "center" as const,
    gap: "10px",
  },
  miniVal: {
    fontSize: "1.6rem",
    fontWeight: 700,
    color: "#fff",
  },
  miniLabel: {
    fontSize: "0.85rem",
    color: "#9ca3af",
  },
  sectionTitle: {
    fontSize: "1.4rem",
    fontWeight: 700,
    color: "#fff",
    marginTop: "10px",
  },
  quickAccessGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(4, 1fr)",
    gap: "20px",
  },
  quickCard: {
    padding: "20px",
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    justifyContent: "center",
    gap: "12px",
    textAlign: "center" as const,
    transition: "transform 0.2s, box-shadow 0.2s",
    border: "1px solid rgba(255, 255, 255, 0.05)",
    cursor: "pointer",
  },
  quickEmoji: {
    fontSize: "2rem",
  },
  quickLabel: {
    fontSize: "0.95rem",
    color: "#fff",
    fontWeight: 600,
  },
  analyticsGrid: {
    display: "grid",
    gridTemplateColumns: "1.2fr 1fr",
    gap: "24px",
    marginBottom: "30px",
  },
  leftColumn: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "24px",
  },
  sectionCard: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "12px",
  },
  analyticsTitle: {
    fontSize: "1.2rem",
    color: "#fff",
  },
  analyticsText: {
    fontSize: "0.95rem",
    color: "#9ca3af",
  },
  progressContainer: {
    height: "10px",
    width: "100%",
    backgroundColor: "rgba(255, 255, 255, 0.08)",
    borderRadius: "10px",
    overflow: "hidden",
  },
  progressBar: {
    height: "100%",
    backgroundColor: "#8b3dff",
    borderRadius: "10px",
  },
  graphCard: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "20px",
  },
  graphTitle: {
    fontSize: "1.2rem",
    color: "#fff",
  },
  chart: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-end",
    height: "140px",
    paddingTop: "20px",
  },
  barContainer: {
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    gap: "10px",
    width: "40px",
  },
  bar: {
    width: "14px",
    borderRadius: "10px 10px 0 0",
    transition: "height 0.8s ease-out",
  },
  barLabel: {
    fontSize: "0.85rem",
    color: "#9ca3af",
  },
  planCard: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
  },
  planTitle: {
    fontSize: "1.3rem",
    color: "#fff",
    marginBottom: "20px",
  },
  plansList: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
    flexGrow: 1,
    justifyContent: "center",
  },
  planItem: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "16px",
    background: "rgba(255, 255, 255, 0.03)",
    borderRadius: "16px",
    border: "1px solid rgba(255, 255, 255, 0.04)",
  },
  planSubject: {
    fontWeight: 600,
    fontSize: "1.05rem",
    color: "#fff",
  },
  planDesc: {
    fontSize: "0.8rem",
    color: "#8b3dff",
    marginTop: "2px",
  },
  planTimeContainer: {
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "flex-end",
    gap: "6px",
  },
  planTime: {
    fontSize: "0.9rem",
    color: "#fff",
    fontWeight: 500,
  },
  priorityBadge: {
    fontSize: "0.7rem",
    fontWeight: 700,
    padding: "2px 8px",
    borderRadius: "6px",
    textTransform: "uppercase" as const,
  },
  noPlan: {
    textAlign: "center" as const,
    color: "#9ca3af",
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    gap: "12px",
  },
  addPlanBtn: {
    background: "rgba(139, 61, 255, 0.2)",
    color: "#a78bfa",
    border: "1px solid rgba(139, 61, 255, 0.3)",
    padding: "8px 16px",
    borderRadius: "10px",
    fontSize: "0.85rem",
    fontWeight: 600,
  },
};
