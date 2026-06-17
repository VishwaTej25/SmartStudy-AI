import { useState } from "react";
import { ArrowLeft, Code2, BookOpen } from "lucide-react";
import { askGroq } from "../utils/groq";
import { TopicTest } from "./TopicTest";

interface PracticeCourse {
  id: string;
  title: string;
  desc: string;
  emoji: string;
}

interface CodingProblem {
  title: string;
  description: string;
  starterCode: string;
}

const codingProblems: CodingProblem[] = [
  {
    title: "Reverse a String",
    description: "Write a function reverseString(str: string): string that reverses the input string.\n\nExample:\nInput: \"hello\"\nOutput: \"olleh\"",
    starterCode: "function reverseString(str: string): string {\n  // Write your code here\n  return \"\";\n}"
  },
  {
    title: "Two Sum",
    description: "Write a function twoSum(nums: number[], target: number): number[] that returns indices of the two numbers such that they add up to the target.\n\nExample:\nInput: nums = [2, 7, 11, 15], target = 9\nOutput: [0, 1]",
    starterCode: "function twoSum(nums: number[], target: number): number[] {\n  // Write your code here\n  return [];\n}"
  },
  {
    title: "Palindrome Number",
    description: "Write a function isPalindrome(n: number): boolean that checks if an integer n is a palindrome (reads the same backward as forward).\n\nExample:\nInput: 121 -> Output: true\nInput: -121 -> Output: false",
    starterCode: "function isPalindrome(n: number): boolean {\n  // Write your code here\n  return false;\n}"
  }
];

export const Practice: React.FC<{ userId: string }> = ({ userId }) => {
  const [selectedCourse, setSelectedCourse] = useState<PracticeCourse | null>(null);
  const [mode, setMode] = useState<"mcq" | "coding" | null>(null);

  // Coding console state
  const [selectedProblemIndex, setSelectedProblemIndex] = useState(0);
  const [userCode, setUserCode] = useState(codingProblems[0].starterCode);
  const [consoleOutput, setConsoleOutput] = useState("Press 'Run & Evaluate' to test your solution.");
  const [evaluating, setEvaluating] = useState(false);

  const practiceCourses: PracticeCourse[] = [
    { id: "java", title: "Java Programming", desc: "Practice OOPs, Exception Handling, & Collections", emoji: "☕" },
    { id: "dbms", title: "DBMS & SQL", desc: "Practice SQL Joins, Queries, & normalizations", emoji: "🗄" },
    { id: "dsa", title: "Data Structures & Algorithms", desc: "Array, Trees, Graphs & Sorting algorithms", emoji: "💻" }
  ];

  const handleSelectProblem = (idx: number) => {
    setSelectedProblemIndex(idx);
    setUserCode(codingProblems[idx].starterCode);
    setConsoleOutput("Press 'Run & Evaluate' to test your solution.");
  };

  const handleRunCode = async () => {
    setEvaluating(true);
    setConsoleOutput("Compiling code...\nRunning unit test cases...");
    try {
      const problem = codingProblems[selectedProblemIndex];
      const prompt = `Evaluate this user code for the coding problem "${problem.title}".\n\n` +
        `Problem Description:\n${problem.description}\n\n` +
        `User's Submitted Code:\n\`\`\`typescript\n${userCode}\n\`\`\`\n\n` +
        `Check if the logic is correct, dry-run with the examples, and check edge cases. ` +
        `Return the output in a clean terminal/console output format indicating if test cases passed or failed, any compilation or runtime errors, complexity analysis, and suggestions. Keep it brief and formatted as if output from a test runner.`;
      
      const response = await askGroq(prompt);
      setConsoleOutput(response);
    } catch (err: any) {
      setConsoleOutput(`ERROR: ${err.message || "Failed to evaluate code"}`);
    } finally {
      setEvaluating(false);
    }
  };

  // If MCQ practice is launched, we can load a dummy topic like OOPs or General Practice
  if (selectedCourse && mode === "mcq") {
    return (
      <TopicTest
        course={{ id: selectedCourse.id, title: selectedCourse.title, subtitle: selectedCourse.desc, emoji: selectedCourse.emoji, order: 1 }}
        topic={{ id: "practice-quiz", title: "Practice MCQ Quiz", desc: `Practice MCQ Assessment for ${selectedCourse.title}` }}
        userId={userId}
        onBack={() => setMode(null)}
      />
    );
  }

  // Coding console view
  if (selectedCourse && mode === "coding") {
    const currentProblem = codingProblems[selectedProblemIndex];
    return (
      <div style={styles.container} className="animate-fade-in">
        <button onClick={() => setMode(null)} style={styles.backBtn}>
          <ArrowLeft size={18} />
          Back to Practice Options
        </button>

        <div style={styles.header}>
          <h1 style={styles.title}>Coding Console 💻</h1>
          <p style={styles.subtitle}>{selectedCourse.title} &bull; Practice</p>
        </div>

        <div style={styles.codingLayout}>
          <div style={styles.problemPane}>
            <h3 style={styles.sectionTitle}>Select Problem:</h3>
            <div style={styles.problemSelector}>
              {codingProblems.map((prob, idx) => (
                <button
                  key={idx}
                  onClick={() => handleSelectProblem(idx)}
                  style={{
                    ...styles.problemChip,
                    ...(selectedProblemIndex === idx ? styles.activeProblemChip : {})
                  }}
                >
                  {prob.title}
                </button>
              ))}
            </div>

            <div className="glass-panel" style={styles.problemDescCard}>
              <h4 style={{ color: "#fff", marginBottom: "8px" }}>{currentProblem.title}</h4>
              <p style={{ whiteSpace: "pre-wrap", color: "#d1d5db", lineHeight: "1.5rem" }}>
                {currentProblem.description}
              </p>
            </div>
          </div>

          <div style={styles.editorPane}>
            <div style={styles.editorHeader}>
              <span>TypeScript Editor</span>
              <button 
                onClick={handleRunCode} 
                disabled={evaluating}
                style={styles.runBtn}
              >
                {evaluating ? "Evaluating..." : "Run & Evaluate 🚀"}
              </button>
            </div>
            
            <textarea
              value={userCode}
              onChange={(e) => setUserCode(e.target.value)}
              style={styles.textareaEditor}
            />

            <div style={styles.consoleContainer}>
              <div style={styles.consoleHeader}>Terminal Output</div>
              <pre style={styles.consoleBody}>{consoleOutput}</pre>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Course select options
  if (selectedCourse) {
    return (
      <div style={styles.container} className="animate-fade-in">
        <button onClick={() => setSelectedCourse(null)} style={styles.backBtn}>
          <ArrowLeft size={18} />
          Back to Practice Zone
        </button>

        <div style={styles.header}>
          <span style={styles.largeEmoji}>{selectedCourse.emoji}</span>
          <h1 style={styles.title}>{selectedCourse.title}</h1>
          <p style={styles.subtitle}>{selectedCourse.desc}</p>
        </div>

        <div style={styles.choiceGrid}>
          <div 
            onClick={() => setMode("mcq")}
            className="glass-panel clickable" 
            style={styles.choiceCard}
          >
            <div style={{ ...styles.choiceIconBg, backgroundColor: "rgba(139, 61, 255, 0.12)", color: "#8b3dff" }}>
              <BookOpen size={28} />
            </div>
            <h3 style={styles.choiceTitle}>Practice MCQs</h3>
            <p style={styles.choiceDesc}>Generate 50 dynamic questions covering this course to test your knowledge.</p>
          </div>

          <div 
            onClick={() => setMode("coding")}
            className="glass-panel clickable" 
            style={styles.choiceCard}
          >
            <div style={{ ...styles.choiceIconBg, backgroundColor: "rgba(59, 130, 246, 0.12)", color: "#3b82f6" }}>
              <Code2 size={28} />
            </div>
            <h3 style={styles.choiceTitle}>Coding Sandbox</h3>
            <p style={styles.choiceDesc}>Solve hands-on algorithmic and design problems in our live browser console.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <h1 style={styles.title}>Practice Zone 🎯</h1>
        <p style={styles.subtitle}>Consolidate your learning with coding sandboxes & dynamic MCQs</p>
      </div>

      <div style={styles.courseList}>
        {practiceCourses.map((course) => (
          <div key={course.id} className="glass-panel" style={styles.courseCard}>
            <div style={styles.courseInfo}>
              <span style={styles.courseEmoji}>{course.emoji}</span>
              <div>
                <h3 style={styles.courseTitle}>{course.title}</h3>
                <p style={styles.courseDesc}>{course.desc}</p>
              </div>
            </div>
            <button 
              onClick={() => setSelectedCourse(course)} 
              style={styles.startBtn}
            >
              Start Practice
            </button>
          </div>
        ))}
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
    cursor: "pointer",
  },
  header: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "8px",
  },
  largeEmoji: {
    fontSize: "3.5rem",
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
  },
  startBtn: {
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "10px 20px",
    fontWeight: 600,
    fontSize: "0.95rem",
    cursor: "pointer",
  },
  choiceGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "24px",
    marginTop: "20px",
  },
  choiceCard: {
    padding: "30px",
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    textAlign: "center" as const,
    gap: "16px",
    cursor: "pointer",
    minHeight: "240px",
    justifyContent: "center",
  },
  choiceIconBg: {
    width: "60px",
    height: "60px",
    borderRadius: "18px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  choiceTitle: {
    fontSize: "1.3rem",
    color: "#fff",
    fontWeight: 700,
  },
  choiceDesc: {
    fontSize: "0.95rem",
    color: "#9ca3af",
    lineHeight: "1.4rem",
  },
  codingLayout: {
    display: "grid",
    gridTemplateColumns: "350px 1fr",
    gap: "20px",
    marginTop: "10px",
  },
  problemPane: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  sectionTitle: {
    fontSize: "1.1rem",
    color: "#fff",
    fontWeight: 600,
  },
  problemSelector: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "10px",
  },
  problemChip: {
    background: "rgba(255, 255, 255, 0.03)",
    border: "1px solid rgba(255, 255, 255, 0.08)",
    borderRadius: "12px",
    padding: "12px 16px",
    color: "#9ca3af",
    textAlign: "left" as const,
    fontWeight: 600,
    cursor: "pointer",
    fontSize: "0.95rem",
  },
  activeProblemChip: {
    background: "rgba(139, 61, 255, 0.15)",
    border: "1px solid rgba(139, 61, 255, 0.3)",
    color: "#fff",
  },
  problemDescCard: {
    padding: "20px",
  },
  editorPane: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "14px",
  },
  editorHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    color: "#9ca3af",
    fontSize: "0.9rem",
    fontWeight: 500,
  },
  runBtn: {
    background: "#10b981",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    padding: "8px 16px",
    fontWeight: 600,
    fontSize: "0.9rem",
    cursor: "pointer",
  },
  textareaEditor: {
    width: "100%",
    height: "280px",
    background: "#080d1a",
    color: "#a78bfa",
    fontFamily: "monospace",
    fontSize: "1rem",
    padding: "16px",
    borderRadius: "14px",
    border: "1px solid rgba(255, 255, 255, 0.08)",
    outline: "none",
    resize: "none" as const,
    lineHeight: "1.5rem",
  },
  consoleContainer: {
    background: "#050811",
    border: "1px solid rgba(255, 255, 255, 0.08)",
    borderRadius: "14px",
    overflow: "hidden",
  },
  consoleHeader: {
    background: "rgba(255, 255, 255, 0.02)",
    padding: "10px 16px",
    fontSize: "0.85rem",
    color: "#9ca3af",
    borderBottom: "1px solid rgba(255, 255, 255, 0.08)",
  },
  consoleBody: {
    padding: "16px",
    margin: 0,
    fontSize: "0.9rem",
    color: "#ef4444",
    fontFamily: "monospace",
    whiteSpace: "pre-wrap" as const,
    lineHeight: "1.4rem",
    minHeight: "120px",
    maxHeight: "220px",
    overflowY: "auto" as const,
  }
};
