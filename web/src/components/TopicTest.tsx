import { useState, useEffect } from "react";
import type { Course } from "./Courses";
import type { Topic } from "./CourseDetails";
import { ArrowLeft, Clock, Award, CheckCircle } from "lucide-react";
import { db } from "../firebase";
import { doc, writeBatch, increment } from "firebase/firestore";
import { askGroq } from "../utils/groq";

interface TopicTestProps {
  course: Course;
  topic: Topic;
  userId: string;
  onBack: () => void;
}

interface TestQuestion {
  question: string;
  options: string[];
  correctAnswer: string;
}

export const TopicTest: React.FC<TopicTestProps> = ({ course, topic, userId, onBack }) => {
  const [questions, setQuestions] = useState<TestQuestion[]>([]);
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState<string | null>(null);

  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState("");
  const [score, setScore] = useState(0);
  const [testFinished, setTestFinished] = useState(false);
  const [timeLeft, setTimeLeft] = useState(1800); // 30 mins
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [aiAnalysis, setAiAnalysis] = useState<string | null>(null);
  const [aiLoading, setAiLoading] = useState(false);

  // Generate 50 MCQs dynamically from Groq on mount
  useEffect(() => {
    let active = true;
    const loadQuestions = async () => {
      try {
        const prompt = `Generate 50 multiple-choice questions for ${course.title} - ${topic.title}. ` +
          `Each question must have exactly four options labeled A, B, C, D and indicate the correct answer letter. ` +
          `Separate entries with a blank line. Use this exact format:\n` +
          `Question: <question>\nOptions: A) <opt1>, B) <opt2>, C) <opt3>, D) <opt4>\nAnswer: <letter>`;
        
        const raw = await askGroq(prompt);
        if (!active) return;

        const blocks = raw.split("\n\n");
        const parsed: TestQuestion[] = [];
        for (const block of blocks) {
          const lines = block.split("\n").map(l => l.trim()).filter(l => l.length > 0);
          if (lines.length >= 3) {
            const q = lines[0].replace(/^Question:\s*/i, "").trim();
            const optsPart = lines[1].replace(/^Options:\s*/i, "").trim();
            const options = optsPart.split(/,\s*(?=[A-D]\))/i)
              .map(opt => opt.replace(/^[A-D]\)\s*/i, "").trim());
            const ansLetter = lines[2].replace(/^Answer:\s*/i, "").trim().toUpperCase();
            
            const correctIdx = ansLetter === "A" ? 0 : ansLetter === "B" ? 1 : ansLetter === "C" ? 2 : ansLetter === "D" ? 3 : 0;
            const correctAnswer = options[correctIdx] || "";

            if (q && options.length === 4 && correctAnswer) {
              parsed.push({
                question: q,
                options: options,
                correctAnswer: correctAnswer
              });
            }
          }
        }

        if (parsed.length > 0) {
          setQuestions(parsed);
        } else {
          // Fallback static list if parsing failed
          setQuestions(generateFallbackQuestions());
        }
      } catch (err: any) {
        if (active) {
          setFetchError(err.message || "Failed to fetch questions from AI.");
          setQuestions(generateFallbackQuestions());
        }
      } finally {
        if (active) setLoading(false);
      }
    };

    loadQuestions();
    return () => { active = false; };
  }, [course, topic]);

  // Fallback questions just in case
  const generateFallbackQuestions = () => [
    {
      question: "Which keyword is used to create an object blueprint in Java?",
      options: ["Function", "Class", "Object", "Method"],
      correctAnswer: "Class"
    },
    {
      question: "Which collection allows duplicate values?",
      options: ["Set", "HashSet", "ArrayList", "TreeSet"],
      correctAnswer: "ArrayList"
    },
    {
      question: "Which block always executes in exception handling?",
      options: ["catch", "throw", "finally", "try"],
      correctAnswer: "finally"
    },
    {
      question: "What is encapsulation in OOP?",
      options: ["Hiding data and exposing only necessary methods", "Creating child classes", "Overloading methods", "Dynamic binding"],
      correctAnswer: "Hiding data and exposing only necessary methods"
    },
    {
      question: "Which of the following is not a primitive type in Java?",
      options: ["int", "double", "char", "String"],
      correctAnswer: "String"
    }
  ];

  // Timer Effect
  useEffect(() => {
    if (timeLeft > 0 && !testFinished && !loading) {
      const timer = setTimeout(() => setTimeLeft(timeLeft - 1), 1000);
      return () => clearTimeout(timer);
    } else if (timeLeft === 0 && !loading) {
      setTestFinished(true);
    }
  }, [timeLeft, testFinished, loading]);

  const saveResultsAndAnalyze = async (finalScore: number) => {
    setSaving(true);
    setAiLoading(true);
    setError(null);
    try {
      const pct = Math.round((finalScore / questions.length) * 100);

      const batch = writeBatch(db);

      // 1. Save attempt in testAttempts subcollection
      const attemptRef = doc(db, "users", userId, "testAttempts", `attempt_${Date.now()}`);
      batch.set(attemptRef, {
        id: attemptRef.id,
        testId: "java-assessment",
        courseId: course.id,
        title: `${course.title} - ${topic.title} Assessment`,
        score: finalScore,
        totalQuestions: questions.length,
        percentage: pct,
        createdAt: Date.now()
      });

      // 2. Update Enrollment progress
      const enrollmentRef = doc(db, "users", userId, "enrollments", course.id);
      batch.set(enrollmentRef, {
        courseId: course.id,
        progress: pct / 100,
        enrolledAt: Date.now()
      }, { merge: true });

      // 3. Increment User XP and Streak
      const userRef = doc(db, "users", userId);
      batch.set(userRef, {
        xp: increment(pct * 10),
        streak: increment(1)
      }, { merge: true });

      await batch.commit();

      // Trigger AI analysis
      const analysisPrompt = `A student just completed a ${questions.length}-question ${course.title} multiple-choice test on "${topic.title}" ` +
        `and scored ${finalScore} out of ${questions.length} (${pct}%). ` +
        `Provide a detailed personalised AI analysis (around 150 words) covering: ` +
        `1) overall performance, 2) which areas they likely struggled with based on the score, ` +
        `3) specific study recommendations, and 4) an encouraging closing remark.`;
      
      const analysis = await askGroq(analysisPrompt);
      setAiAnalysis(analysis);
    } catch (err: any) {
      setError(err.message || "Failed to save test results / run AI analysis.");
    } finally {
      setSaving(false);
      setAiLoading(false);
    }
  };

  const handleNext = () => {
    const isCorrect = selectedAnswer === questions[currentIndex].correctAnswer;
    const newScore = score + (isCorrect ? 1 : 0);
    setScore(newScore);

    if (currentIndex < questions.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setSelectedAnswer("");
    } else {
      setTestFinished(true);
      saveResultsAndAnalyze(newScore);
    }
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, "0")}`;
  };

  if (loading) {
    return (
      <div style={styles.container} className="animate-fade-in">
        <div style={styles.loadingCard}>
          <div className="spinner" style={styles.spinner}></div>
          <p style={{ marginTop: "20px", fontSize: "1.1rem" }}>Generating 50 AI questions for {topic.title}...</p>
        </div>
      </div>
    );
  }

  if (!loading && questions.length === 0) {
    return (
      <div style={styles.container} className="animate-fade-in">
        <div className="glass-panel" style={styles.resultsCard}>
          <h2 style={styles.error}>⚠️ Failed to load questions</h2>
          <p style={styles.savingText}>{fetchError || "Could not parse AI response."}</p>
          <button onClick={onBack} style={styles.finishBtn}>
            Go Back
          </button>
        </div>
      </div>
    );
  }

  if (testFinished) {
    const pct = Math.round((score / questions.length) * 100);
    return (
      <div style={styles.container} className="animate-fade-in">
        <div className="glass-panel" style={styles.resultsCard}>
          <Award size={64} color="#facc15" style={{ marginBottom: "15px" }} />
          <h2 style={styles.resultsTitle}>🎉 Test Completed</h2>
          
          <div style={styles.scoreText}>
            Score: <span style={styles.scoreVal}>{score} / {questions.length} ({pct}%)</span>
          </div>

          {saving ? (
            <p style={styles.savingText}>Saving results and updating XP...</p>
          ) : error ? (
            <p style={styles.error}>{error}</p>
          ) : (
            <p style={styles.success}>✓ Results saved! XP updated.</p>
          )}

          <div className="glass-panel" style={styles.aiReviewCard}>
            <h4 style={styles.aiTitle}>🤖 Personalised AI Analysis</h4>
            {aiLoading ? (
              <p style={styles.savingText}>Generating AI report...</p>
            ) : aiAnalysis ? (
              <p style={styles.aiDesc}>{aiAnalysis}</p>
            ) : (
              <p style={styles.aiDesc}>
                {pct >= 80 ? (
                  "Outstanding performance! You have mastered this module. Focus on advanced patterns next."
                ) : pct >= 50 ? (
                  "Good effort. Focus on practicing more questions and reviewing your incorrect answers."
                ) : (
                  "Additional study is recommended. Revise core OOPs principles and try again."
                )}
              </p>
            )}
          </div>

          <button onClick={onBack} style={styles.finishBtn}>
            <CheckCircle size={18} />
            View Course Dashboard
          </button>
        </div>
      </div>
    );
  }

  const currentQuestion = questions[currentIndex];

  return (
    <div style={styles.container} className="animate-fade-in">
      <button onClick={onBack} style={styles.backBtn}>
        <ArrowLeft size={18} />
        Quit Test
      </button>

      <div style={styles.testHeader}>
        <div style={styles.headerTitleSec}>
          <h1 style={styles.title}>{course.title} Assessment 📝</h1>
          <span style={styles.subText}>Question {currentIndex + 1} of {questions.length}</span>
        </div>
        
        <div style={{
          ...styles.timerBox,
          backgroundColor: timeLeft < 300 ? "rgba(239, 68, 68, 0.15)" : "rgba(16, 185, 129, 0.15)",
          color: timeLeft < 300 ? "#ef4444" : "#10b981",
          borderColor: timeLeft < 300 ? "rgba(239, 68, 68, 0.25)" : "rgba(16, 185, 129, 0.25)"
        }}>
          <Clock size={18} />
          <span>{formatTime(timeLeft)}</span>
        </div>
      </div>

      <div className="glass-panel" style={styles.quizCard}>
        <h3 style={styles.questionText}>{currentQuestion?.question}</h3>

        <div style={styles.optionsList}>
          {currentQuestion?.options.map((option, idx) => {
            const isSelected = selectedAnswer === option;
            const labels = ["A", "B", "C", "D"];
            return (
              <label 
                key={idx} 
                style={{
                  ...styles.optionItem,
                  ...(isSelected ? styles.selectedOption : {}),
                }}
              >
                <input
                  type="radio"
                  name="quiz-option"
                  value={option}
                  checked={isSelected}
                  onChange={() => setSelectedAnswer(option)}
                  style={styles.radioInput}
                />
                <span style={{ fontWeight: 600, marginRight: "8px" }}>{labels[idx]})</span> {option}
              </label>
            );
          })}
        </div>
      </div>

      <button 
        onClick={handleNext} 
        disabled={!selectedAnswer}
        style={{
          ...styles.nextBtn,
          opacity: selectedAnswer ? 1 : 0.6,
          cursor: selectedAnswer ? "pointer" : "not-allowed",
        }}
      >
        {currentIndex === questions.length - 1 ? "Finish Test 🏁" : "Next Question →"}
      </button>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "700px",
    margin: "0 auto",
    display: "flex",
    flexDirection: "column" as const,
    gap: "24px",
  },
  loadingCard: {
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    justifyContent: "center",
    padding: "60px",
    background: "rgba(255, 255, 255, 0.02)",
    borderRadius: "24px",
    border: "1px solid rgba(255, 255, 255, 0.05)",
  },
  spinner: {
    width: "50px",
    height: "50px",
    border: "5px solid rgba(139, 61, 255, 0.2)",
    borderTop: "5px solid #8b3dff",
    borderRadius: "50%",
    animation: "spin 1s linear infinite",
  },
  backBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "none",
    border: "none",
    color: "#ef4444",
    fontSize: "0.95rem",
    fontWeight: 600,
    width: "fit-content",
    alignSelf: "flex-start",
  },
  testHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  headerTitleSec: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "4px",
  },
  title: {
    fontSize: "1.8rem",
    color: "#fff",
  },
  subText: {
    color: "#00e5ff",
    fontWeight: 600,
    fontSize: "0.95rem",
  },
  timerBox: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    padding: "8px 16px",
    borderRadius: "12px",
    fontWeight: 700,
    fontSize: "1.05rem",
    border: "1px solid"
  },
  quizCard: {
    padding: "30px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "24px",
  },
  questionText: {
    fontSize: "1.25rem",
    color: "#fff",
    lineHeight: "1.6",
  },
  optionsList: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "12px",
  },
  optionItem: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    padding: "16px",
    borderRadius: "14px",
    border: "1px solid rgba(255, 255, 255, 0.05)",
    background: "rgba(255, 255, 255, 0.02)",
    cursor: "pointer",
    fontSize: "1rem",
    color: "#fff",
    transition: "all 0.2s",
  },
  selectedOption: {
    borderColor: "#8b3dff",
    background: "rgba(139, 61, 255, 0.1)",
  },
  radioInput: {
    accentColor: "#8b3dff",
    width: "18px",
    height: "18px",
  },
  nextBtn: {
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "14px",
    fontSize: "1rem",
    fontWeight: 600,
    boxShadow: "0 4px 12px rgba(139, 61, 255, 0.3)",
  },
  resultsCard: {
    padding: "40px",
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    textAlign: "center" as const,
  },
  resultsTitle: {
    fontSize: "2rem",
    color: "#fff",
    marginBottom: "10px",
  },
  scoreText: {
    fontSize: "1.2rem",
    color: "#fff",
    marginBottom: "15px",
  },
  scoreVal: {
    color: "#8b3dff",
    fontWeight: 800,
    fontSize: "1.6rem",
  },
  savingText: {
    color: "#9ca3af",
    fontSize: "0.9rem",
  },
  error: {
    color: "#f87171",
    fontSize: "0.9rem",
  },
  success: {
    color: "#34d399",
    fontSize: "0.9rem",
    fontWeight: 600,
  },
  aiReviewCard: {
    width: "100%",
    padding: "20px",
    marginTop: "20px",
    marginBottom: "30px",
    textAlign: "left" as const,
  },
  aiTitle: {
    fontSize: "1.1rem",
    color: "#fff",
    marginBottom: "8px",
  },
  aiDesc: {
    color: "#9ca3af",
    lineHeight: "1.5",
  },
  finishBtn: {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "12px 24px",
    fontSize: "1rem",
    fontWeight: 600,
    boxShadow: "0 4px 12px rgba(139, 61, 255, 0.3)",
  },
};
