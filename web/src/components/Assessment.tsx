import { useState } from "react";
import { ClipboardList } from "lucide-react";
import { TopicTest } from "./TopicTest";

interface AssessmentCourse {
  id: string;
  title: string;
  desc: string;
  emoji: string;
  timeLimit: string;
}

export const Assessment: React.FC<{ userId: string }> = ({ userId }) => {
  const [selectedCourse, setSelectedCourse] = useState<AssessmentCourse | null>(null);

  const assessmentCourses: AssessmentCourse[] = [
    { id: "java", title: "Java OOPs Assessment", desc: "50 questions testing complete OOP design & structures", emoji: "☕", timeLimit: "30 Mins" },
    { id: "dbms", title: "DBMS Assessment", desc: "50 questions on schema models, transactions & SQL", emoji: "🗄", timeLimit: "30 Mins" },
    { id: "dsa", title: "DSA Logic Assessment", desc: "50 questions on algorithmic runtimes and structures", emoji: "💻", timeLimit: "30 Mins" }
  ];

  if (selectedCourse) {
    return (
      <TopicTest
        course={{ id: selectedCourse.id, title: selectedCourse.title, subtitle: selectedCourse.desc, emoji: selectedCourse.emoji, order: 1 }}
        topic={{ id: "course-assessment", title: "Course Assessment", desc: `Final Course MCQ Assessment for ${selectedCourse.title}` }}
        userId={userId}
        onBack={() => setSelectedCourse(null)}
      />
    );
  }

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <h1 style={styles.title}>Assessments 📝</h1>
        <p style={styles.subtitle}>Formal, AI-evaluated MCQ exams to earn certification readiness</p>
      </div>

      <div style={styles.courseList}>
        {assessmentCourses.map((course) => (
          <div key={course.id} className="glass-panel" style={styles.courseCard}>
            <div style={styles.courseInfo}>
              <span style={styles.courseEmoji}>{course.emoji}</span>
              <div>
                <h3 style={styles.courseTitle}>{course.title}</h3>
                <p style={styles.courseDesc}>{course.desc}</p>
                <div style={styles.metaInfo}>
                  <ClipboardList size={14} style={{ color: "#a78bfa" }} />
                  <span>50 Questions</span>
                  <span style={styles.metaDot}>&bull;</span>
                  <span>{course.timeLimit}</span>
                </div>
              </div>
            </div>
            <button 
              onClick={() => setSelectedCourse(course)} 
              style={styles.startBtn}
            >
              Take Exam
            </button>
          </div>
        ))}
      </div>
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
    display: "flex",
    flexDirection: "column" as const,
    gap: "8px",
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
  courseList: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  courseCard: {
    padding: "24px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "20px",
  },
  courseInfo: {
    display: "flex",
    alignItems: "center",
    gap: "18px",
  },
  courseEmoji: {
    fontSize: "2.5rem",
  },
  courseTitle: {
    fontSize: "1.25rem",
    color: "#fff",
    fontWeight: 700,
  },
  courseDesc: {
    fontSize: "0.95rem",
    color: "#9ca3af",
    marginBottom: "8px",
  },
  metaInfo: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    fontSize: "0.85rem",
    color: "#9ca3af",
  },
  metaDot: {
    color: "#4b5563",
  },
  startBtn: {
    background: "#00b894",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "10px 20px",
    fontWeight: 600,
    fontSize: "0.95rem",
    cursor: "pointer",
  }
};
