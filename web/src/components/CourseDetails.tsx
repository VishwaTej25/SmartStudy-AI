import { useState } from "react";
import type { Course } from "./Courses";
import { ArrowLeft, BookOpen, FileText } from "lucide-react";
import { TopicLearn } from "./TopicLearn";
import { TopicTest } from "./TopicTest";

interface CourseDetailsProps {
  course: Course;
  onBack: () => void;
  userId: string;
}

export interface Topic {
  id: string;
  title: string;
  desc: string;
}

export const CourseDetails: React.FC<CourseDetailsProps> = ({ course, onBack, userId }) => {
  const [selectedTopic, setSelectedTopic] = useState<Topic | null>(null);
  const [mode, setMode] = useState<"learn" | "test" | null>(null);

  const topics: Topic[] = [
    { id: "oops", title: "📘 OOPs", desc: "Classes, Objects, Inheritance, Polymorphism" },
    { id: "collections", title: "📚 Collections", desc: "ArrayList, HashMap, HashSet, Vector" },
    { id: "exceptions", title: "⚠️ Exception Handling", desc: "try, catch, finally, throw, custom exceptions" },
    { id: "multithreading", title: "🧵 Multithreading", desc: "Threads, Runnable, Concurrency, Synchronization" }
  ];

  if (selectedTopic && mode === "learn") {
    return (
      <TopicLearn 
        course={course} 
        topic={selectedTopic} 
        onBack={() => {
          setSelectedTopic(null);
          setMode(null);
        }} 
      />
    );
  }

  if (selectedTopic && mode === "test") {
    return (
      <TopicTest 
        course={course} 
        topic={selectedTopic} 
        userId={userId}
        onBack={() => {
          setSelectedTopic(null);
          setMode(null);
        }} 
      />
    );
  }

  return (
    <div style={styles.container} className="animate-fade-in">
      <button onClick={onBack} style={styles.backBtn}>
        <ArrowLeft size={18} />
        Back to Courses
      </button>

      <div style={styles.header}>
        <span style={styles.emoji}>{course.emoji}</span>
        <h1 style={styles.title}>{course.title}</h1>
        <p style={styles.subtitle}>{course.subtitle}</p>
      </div>

      <div style={styles.topicsList}>
        {topics.map((topic) => (
          <div key={topic.id} className="glass-panel" style={styles.topicCard}>
            <div style={styles.topicInfo}>
              <h3 style={styles.topicTitle}>{topic.title}</h3>
              <p style={styles.topicDesc}>{topic.desc}</p>
            </div>

            <div style={styles.actions}>
              <button 
                onClick={() => {
                  setSelectedTopic(topic);
                  setMode("learn");
                }} 
                style={styles.learnBtn}
              >
                <BookOpen size={16} />
                Learn
              </button>
              <button 
                onClick={() => {
                  setSelectedTopic(topic);
                  setMode("test");
                }} 
                style={styles.testBtn}
              >
                <FileText size={16} />
                Test
              </button>
            </div>
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
  backBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "none",
    border: "none",
    color: "#a78bfa",
    fontSize: "0.95rem",
    fontWeight: 600,
    width: "fit-content",
    alignSelf: "flex-start",
  },
  header: {
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    textAlign: "center" as const,
    marginBottom: "10px",
  },
  emoji: {
    fontSize: "3.5rem",
    marginBottom: "10px",
  },
  title: {
    fontSize: "2.4rem",
    color: "#fff",
  },
  subtitle: {
    fontSize: "1.05rem",
    color: "#9ca3af",
    marginTop: "4px",
  },
  topicsList: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  topicCard: {
    padding: "24px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "20px",
  },
  topicInfo: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "6px",
  },
  topicTitle: {
    fontSize: "1.25rem",
    color: "#fff",
  },
  topicDesc: {
    fontSize: "0.95rem",
    color: "#9ca3af",
  },
  actions: {
    display: "flex",
    gap: "12px",
  },
  learnBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "10px 18px",
    fontSize: "0.95rem",
    fontWeight: 600,
  },
  testBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "rgba(0, 184, 148, 0.15)",
    color: "#00b894",
    border: "1px solid rgba(0, 184, 148, 0.3)",
    borderRadius: "12px",
    padding: "10px 18px",
    fontSize: "0.95rem",
    fontWeight: 600,
  },
};
