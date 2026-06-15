import React, { useEffect, useState } from "react";
import { db } from "../firebase";
import { collection, onSnapshot, doc, setDoc, deleteDoc } from "firebase/firestore";

export interface Course {
  id: string;
  title: string;
  subtitle: string;
  emoji: string;
  order: number;
}

interface CoursesProps {
  userId: string;
  onSelectCourse: (course: Course) => void;
  enrolledCourseIds: Set<string>;
}

const defaultCourses: Course[] = [
  { id: "java", title: "Java Programming", subtitle: "Core Java + Advanced Java", emoji: "☕", order: 1 },
  { id: "python", title: "Python", subtitle: "Python Basics to Advanced", emoji: "🐍", order: 2 },
  { id: "dsa", title: "DSA", subtitle: "Data Structures & Algorithms", emoji: "💻", order: 3 },
  { id: "dbms", title: "DBMS", subtitle: "Database Management System", emoji: "🗄️", order: 4 },
  { id: "os", title: "Operating Systems", subtitle: "OS Concepts & Scheduling", emoji: "⚙️", order: 5 },
  { id: "networks", title: "Computer Networks", subtitle: "Networking Fundamentals", emoji: "🌐", order: 6 }
];

export const Courses: React.FC<CoursesProps> = ({ 
  userId, 
  onSelectCourse,
  enrolledCourseIds
}) => {
  const [courses, setCourses] = useState<Course[]>(defaultCourses);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Listen to courses collection
    const unsubscribe = onSnapshot(
      collection(db, "courses"),
      (snapshot) => {
        const fetchedCourses = snapshot.docs.map((doc) => ({
          id: doc.id,
          ...doc.data(),
        })) as Course[];

        if (fetchedCourses.length > 0) {
          setCourses(fetchedCourses.sort((a, b) => a.order - b.order));
        } else {
          // If Firestore "courses" is empty, seed it
          seedCourses();
        }
      },
      (err) => {
        setError(err.message);
      }
    );

    return () => unsubscribe();
  }, []);

  const seedCourses = async () => {
    try {
      for (const course of defaultCourses) {
        await setDoc(doc(db, "courses", course.id), course);
      }
    } catch (err: any) {
      console.error("Failed to seed courses:", err);
    }
  };

  const handleEnrollmentToggle = async (e: React.MouseEvent, course: Course, isEnrolled: boolean) => {
    e.stopPropagation(); // Avoid triggering card click
    setError(null);
    try {
      const enrollmentRef = doc(db, "users", userId, "enrollments", course.id);
      if (isEnrolled) {
        await deleteDoc(enrollmentRef);
      } else {
        await setDoc(enrollmentRef, {
          courseId: course.id,
          progress: 0.0,
          enrolledAt: Date.now(),
        });
      }
    } catch (err: any) {
      setError(err.message || "Failed to update enrollment.");
    }
  };

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <h1 style={styles.title}>All Courses 📚</h1>
        <p style={styles.subtitle}>Enroll in subjects to start practicing, studying, and earning XP.</p>
      </div>

      {error && <div style={styles.error}>{error}</div>}

      <div style={styles.grid}>
        {courses.map((course) => {
          const isEnrolled = enrolledCourseIds.has(course.id);
          return (
            <div
              key={course.id}
              className="glass-panel clickable"
              style={styles.card}
              onClick={() => onSelectCourse(course)}
            >
              <div style={styles.emoji}>{course.emoji}</div>
              <div style={styles.cardInfo}>
                <h3 style={styles.courseTitle}>{course.title}</h3>
                <p style={styles.courseSubtitle}>{course.subtitle}</p>
              </div>

              <button
                onClick={(e) => handleEnrollmentToggle(e, course, isEnrolled)}
                style={{
                  ...styles.enrollBtn,
                  background: isEnrolled ? "rgba(0, 184, 148, 0.15)" : "#8b3dff",
                  color: isEnrolled ? "#00b894" : "#fff",
                  border: isEnrolled ? "1px solid rgba(0, 184, 148, 0.3)" : "none",
                }}
              >
                {isEnrolled ? "Enrolled ✓" : "Enroll"}
              </button>
            </div>
          );
        })}
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
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
    gap: "24px",
  },
  card: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    justifyContent: "space-between",
    minHeight: "230px",
    cursor: "pointer",
  },
  emoji: {
    fontSize: "2.5rem",
    marginBottom: "15px",
  },
  cardInfo: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "6px",
    flexGrow: 1,
  },
  courseTitle: {
    fontSize: "1.25rem",
    color: "#fff",
  },
  courseSubtitle: {
    fontSize: "0.9rem",
    color: "#9ca3af",
  },
  enrollBtn: {
    width: "100%",
    padding: "10px",
    borderRadius: "12px",
    fontSize: "0.95rem",
    fontWeight: 600,
    marginTop: "20px",
  },
};
