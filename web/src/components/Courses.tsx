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
  { id: "math_discrete", title: "Discrete Mathematics", subtitle: "Sets, Logic, Relations, Graphs & Combinatorics", emoji: "📐", order: 1 },
  { id: "math_stats", title: "Probability & Statistics", subtitle: "Probability Distributions, Hypothesis Testing & Regression", emoji: "📊", order: 2 },
  { id: "c_cpp", title: "C & C++ Programming", subtitle: "Procedural & Object-Oriented Programming Fundamentals", emoji: "💻", order: 3 },
  { id: "java", title: "Java Programming", subtitle: "Core Java + OOPs + Advanced Java", emoji: "☕", order: 4 },
  { id: "python", title: "Python Programming", subtitle: "Python Basics, Scripting & Libraries", emoji: "🐍", order: 5 },
  { id: "dsa", title: "Data Structures & Algorithms", subtitle: "Stack, Queue, Trees, Graphs, Sorting & Searching", emoji: "🧠", order: 6 },
  { id: "dbms", title: "DBMS", subtitle: "Database Management Systems & SQL", emoji: "🗄️", order: 7 },
  { id: "os", title: "Operating Systems", subtitle: "Process Management, Memory, Storage & I/O", emoji: "💿", order: 8 },
  { id: "networks", title: "Computer Networks", subtitle: "TCP/IP OSI Model, Routing & Protocols", emoji: "🌐", order: 9 },
  { id: "se", title: "Software Engineering", subtitle: "SDLC, Agile, Git, testing & UML", emoji: "🏗️", order: 10 },
  { id: "toc", title: "Theory of Computation", subtitle: "Finite Automata, Context Free Grammars & Turing Machines", emoji: "⚙️", order: 11 },
  { id: "compiler", title: "Compiler Design", subtitle: "Lexical Analysis, Parsing, Code Generation & Optimization", emoji: "🔧", order: 12 },
  { id: "coa", title: "Computer Architecture", subtitle: "CPU, ALU, Memory Hierarchy & Instruction Pipelines", emoji: "🖥️", order: 13 },
  { id: "webdev", title: "Web Development", subtitle: "HTML, CSS, JS, React, Node.js & Full-stack dev", emoji: "🌍", order: 14 },
  { id: "ai", title: "Artificial Intelligence", subtitle: "Search Algorithms, Knowledge Representation & Planning", emoji: "🤖", order: 15 },
  { id: "ml", title: "Machine Learning", subtitle: "Regression, Classification, Clustering & Neural Networks", emoji: "🧠", order: 16 },
  { id: "crypto", title: "Cryptography & Security", subtitle: "Symmetric/Asymmetric Encryption, Hashes & Network Security", emoji: "🔐", order: 17 },
  { id: "cloud", title: "Cloud Computing", subtitle: "Virtualization, SaaS/PaaS/IaaS, AWS, Azure & GCP", emoji: "☁️", order: 18 },
  { id: "bigdata", title: "Big Data Analytics", subtitle: "Hadoop, Spark, NoSQL & Data Engineering", emoji: "🛢️", order: 19 },
  { id: "iot", title: "Internet of Things", subtitle: "Sensors, Actuators, Microcontrollers & IoT Protocols", emoji: "🔌", order: 20 },
  { id: "mobile", title: "Mobile Development", subtitle: "Kotlin, Swift, Flutter & Android/iOS Apps", emoji: "📱", order: 21 },
  { id: "devops", title: "DevOps & CI/CD", subtitle: "Docker, Kubernetes, Jenkins & Infrastructure as Code", emoji: "🔄", order: 22 },
  { id: "micro", title: "Microprocessors", subtitle: "8085/8086 Assembly Language, Interfacing & Interrupts", emoji: "📟", order: 23 },
  { id: "dld", title: "Digital Logic Design", subtitle: "Logic Gates, Boolean Algebra, Combinational & Sequential Circuits", emoji: "🔢", order: 24 },
  { id: "graphics", title: "Computer Graphics", subtitle: "Rasterization, Ray Tracing, OpenGL & 3D Rendering", emoji: "🎨", order: 25 },
  { id: "testing", title: "Software Testing", subtitle: "Unit, Integration, System Testing & Automation QA", emoji: "🧪", order: 26 },
  { id: "distributed", title: "Distributed Systems", subtitle: "Consensus, RPCs, MapReduce & Fault Tolerance", emoji: "🌐", order: 27 },
  { id: "blockchain", title: "Blockchain Technology", subtitle: "Consensus Algorithms, Cryptocurrencies & Smart Contracts", emoji: "⛓️", order: 28 },
  { id: "datamining", title: "Data Mining", subtitle: "Data Preprocessing, Association Rules & Pattern Discovery", emoji: "⛏️", order: 29 },
  { id: "nlp", title: "Natural Language Processing", subtitle: "Text Tokenization, Parsing, Sentiment Analysis & LLMs", emoji: "🗣️", order: 30 },
  { id: "ooad", title: "OOAD using UML", subtitle: "Use Cases, Class/Sequence Diagrams & Design Patterns", emoji: "📐", order: 31 },
  { id: "dsp", title: "Digital Signal Processing", subtitle: "Signals, Fourier Transforms, Filters & LTI Systems", emoji: "📡", order: 32 },
  { id: "vlsi", title: "VLSI Design", subtitle: "CMOS Circuits, Layouts, Verilog/VHDL & ASIC Design", emoji: "🔌", order: 33 },
  { id: "embedded", title: "Embedded Systems", subtitle: "Real-Time OS (RTOS), Firmware & Microcontrollers", emoji: "📟", order: 34 },
  { id: "netsec", title: "Network Security", subtitle: "Firewalls, VPNs, IDS/IPS & Threat Prevention", emoji: "🛡️", order: 35 },
  { id: "hci", title: "Human-Computer Interaction", subtitle: "User Experience (UX) Design, Prototyping & Usability", emoji: "👥", order: 36 },
  { id: "parallel", title: "Parallel Computing", subtitle: "Multithreading, OpenMP, MPI & GPU programming (CUDA)", emoji: "⚡", order: 37 },
  { id: "deeplearning", title: "Deep Learning", subtitle: "Convolutional & Recurrent Nets, PyTorch & TensorFlow", emoji: "🧠", order: 38 },
  { id: "softcomp", title: "Soft Computing", subtitle: "Fuzzy Logic, Genetic Algorithms & Swarm Intelligence", emoji: "🐝", order: 39 },
  { id: "ir", title: "Information Retrieval", subtitle: "Search Engines, Indexing, TF-IDF & PageRank", emoji: "🔍", order: 40 }
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

        if (fetchedCourses.length >= defaultCourses.length) {
          setCourses(fetchedCourses.sort((a, b) => a.order - b.order));
        } else {
          // If Firestore "courses" count is outdated/empty, seed it
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

  const [searchTerm, setSearchTerm] = useState<string>('');
  const filteredCourses = courses.filter((c) =>
    c.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    c.subtitle.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <h1 style={styles.title}>All Courses 📚</h1>
        <p style={styles.subtitle}>Enroll in subjects to start practicing, studying, and earning XP.</p>
      </div>

      {error && <div style={styles.error}>{error}</div>}

      <input
        type="text"
        placeholder="Search courses..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        style={styles.searchInput}
      />

      <div style={styles.grid}>
        {filteredCourses.map((course) => {
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
        {filteredCourses.length === 0 && (
          <div style={styles.placeholder}>
            No courses found matching your query
          </div>
        )}
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
  placeholder: {
    textAlign: "center" as const,
    padding: "20px",
    color: "#9ca3af",
    fontSize: "1rem",
  },
  searchInput: {
    width: "100%",
    padding: "14px 20px",
    borderRadius: "12px",
    background: "#1f2937",
    border: "1px solid #374151",
    color: "#fff",
    fontSize: "1rem",
    outline: "none",
    marginBottom: "20px",
  },
  enrollBtn: {
    width: "100%",
    padding: "10px",
    borderRadius: "12px",
    fontSize: "0.95rem",
    fontWeight: 600,
    marginTop: "20px",
    cursor: "pointer",
  },
};
