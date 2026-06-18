import { useState, useEffect } from "react";
import type { Course } from "./Courses";
import type { Topic } from "./CourseDetails";
import { 
  ArrowLeft, 
  Play, 
  FileText, 
  Cpu, 
  HelpCircle, 
  Copy, 
  Check, 
  Printer, 
  ChevronDown, 
  ChevronUp
} from "lucide-react";
import { askGroq } from "../utils/groq";

interface TopicLearnProps {
  course: Course;
  topic: Topic;
  onBack: () => void;
}

interface QAItem {
  q: string;
  a: string;
  isOpen: boolean;
}

export const TopicLearn: React.FC<TopicLearnProps> = ({ course, topic, onBack }) => {
  const [selectedView, setSelectedView] = useState<"" | "video" | "pdf" | "ai" | "questions">("");
  const [loading, setLoading] = useState(false);
  const [notesText, setNotesText] = useState("");
  const [explanationText, setExplanationText] = useState("");
  const [qaList, setQaList] = useState<QAItem[]>([]);
  const [copied, setCopied] = useState(false);

  // Curated video list for OOPs and general courses
  const videos = [
    { title: `Introduction to ${topic.title.substring(2)}`, desc: `Understand the foundational concepts, definitions, and syntax of ${topic.title}.` },
    { title: `${topic.title.substring(2)} — Core Mechanics`, desc: `Deep dive into the operational model, memory layout, and runtime handling of ${topic.title}.` },
    { title: `Practical Implementation`, desc: `Applying ${topic.title} in real-world projects and industry scenarios.` },
    { title: `Common Problems & Debugging`, desc: `How to avoid common pitfalls and fix errors related to ${topic.title}.` },
    { title: `Advanced Techniques`, desc: `Mastering advanced patterns and optimisations for ${topic.title} in ${course.title}.` }
  ];

  // Fetch AI content based on view
  useEffect(() => {
    if (!selectedView || selectedView === "video") return;

    const fetchContent = async () => {
      setLoading(true);
      try {
        if (selectedView === "pdf") {
          if (notesText) { setLoading(false); return; }
          const prompt = `Generate comprehensive, structured PDF-style study notes for the course "${course.title}" covering the topic "${topic.title}". Include clear sections, bullet points, concise definitions, syntax examples, and exam tips.`;
          const res = await askGroq(prompt);
          setNotesText(res);
        } else if (selectedView === "ai") {
          if (explanationText) { setLoading(false); return; }
          const prompt = `Provide an interactive AI explanation/breakdown of "${topic.title}" in the context of "${course.title}". Explain the theory, provide clear code snippets in the primary language, and draw a step-by-step logic flow.`;
          const res = await askGroq(prompt);
          setExplanationText(res);
        } else if (selectedView === "questions") {
          if (qaList.length > 0) { setLoading(false); return; }
          const prompt = `Generate 8 important technical interview and university exam questions with detailed answers for "${topic.title}" under "${course.title}". Format the response strictly using Question: and Answer: tags for each, like:\nQuestion: <question>\nAnswer: <answer>`;
          const res = await askGroq(prompt);
          
          const blocks = res.split(/Question:/i);
          const parsed: QAItem[] = [];
          for (const block of blocks) {
            if (!block.trim()) continue;
            const parts = block.split(/Answer:/i);
            if (parts.length >= 2) {
              parsed.push({
                q: parts[0].trim(),
                a: parts[1].trim(),
                isOpen: false
              });
            }
          }
          if (parsed.length > 0) {
            setQaList(parsed);
          } else {
            setQaList([
              { q: `What is the significance of ${topic.title}?`, a: `It forms the core learning block of ${course.title}, driving robust architecture.`, isOpen: false },
              { q: `Can you list the common errors in ${topic.title}?`, a: `Compilation mismatches, incorrect references, and poor design setups.`, isOpen: false }
            ]);
          }
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchContent();
  }, [selectedView, course, topic]);

  const handleCopy = () => {
    const text = selectedView === "pdf" ? notesText : explanationText;
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const toggleQA = (idx: number) => {
    setQaList(prev => prev.map((item, i) => i === idx ? { ...item, isOpen: !item.isOpen } : item));
  };

  const cards = [
    {
      id: "video",
      title: "📹 Video Lectures",
      desc: `Curated learning playlists and code walkthroughs for ${topic.title.substring(2)}.`,
      icon: Play,
      color: "#8b3dff",
    },
    {
      id: "pdf",
      title: "📄 PDF Materials",
      desc: "AI study notes, detailed cheat sheets, and downloadable guides.",
      icon: FileText,
      color: "#00b894",
    },
    {
      id: "ai",
      title: "🧠 AI Explanation",
      desc: "Instant breakdown, architectural flows, and clean code examples.",
      icon: Cpu,
      color: "#3b82f6",
    },
    {
      id: "questions",
      title: "💡 Important Questions",
      desc: "Top university exam and technical job interview questions.",
      icon: HelpCircle,
      color: "#ec4899",
    },
  ];

  if (selectedView !== "") {
    return (
      <div style={styles.container} className="animate-fade-in">
        <button onClick={() => setSelectedView("")} style={styles.backBtn}>
          <ArrowLeft size={18} />
          Back to learning topics
        </button>

        <div style={styles.header}>
          <h1 style={styles.title}>{topic.title}</h1>
          <p style={styles.subtitle}>{course.title} &bull; Learn Zone</p>
        </div>

        {loading ? (
          <div style={styles.loadingContainer}>
            <div style={styles.spinner}></div>
            <p style={{ color: "#9ca3af" }}>Generating smart material with Llama-3.3...</p>
          </div>
        ) : (
          <div style={styles.viewContent}>
            {/* 🎥 Video Screen */}
            {selectedView === "video" && (
              <div style={styles.listContainer}>
                <h2 style={styles.sectionTitle}>Curated Video Lectures</h2>
                <p style={{ color: "#9ca3af", fontSize: "0.9rem", marginBottom: "8px" }}>Click any lecture to search on YouTube →</p>
                {videos.map((vid, i) => (
                  <div
                    key={i}
                    className="glass-panel"
                    style={{ ...styles.videoCard, cursor: "pointer" }}
                    onClick={() => {
                      const searchQuery = `${course.title} ${topic.title} ${vid.title} tutorial`;
                      window.open(`https://www.youtube.com/results?search_query=${encodeURIComponent(searchQuery)}`, "_blank");
                    }}
                  >
                    <div style={styles.videoIconBg}>
                      <Play size={20} fill="#8b3dff" color="#8b3dff" />
                    </div>
                    <div style={{ flexGrow: 1 }}>
                      <h4 style={styles.videoTitle}>{vid.title}</h4>
                      <p style={styles.videoDesc}>{vid.desc}</p>
                    </div>
                    <span style={styles.durationBadge}>▶ YouTube</span>
                  </div>
                ))}
              </div>
            )}

            {/* 📄 PDF Notes Screen */}
            {selectedView === "pdf" && (
              <div className="glass-panel" style={styles.textPanel}>
                <div style={styles.panelHeader}>
                  <h3 style={styles.panelTitle}>AI Study Notes</h3>
                  <div style={styles.panelActions}>
                    <button onClick={handleCopy} style={styles.actionBtn}>
                      {copied ? <Check size={16} color="#00b894" /> : <Copy size={16} />}
                      {copied ? "Copied" : "Copy"}
                    </button>
                    <button onClick={() => window.print()} style={styles.actionBtn}>
                      <Printer size={16} />
                      Print
                    </button>
                  </div>
                </div>
                <div style={styles.formattedText}>{notesText}</div>
              </div>
            )}

            {/* 🧠 AI Explanation Screen */}
            {selectedView === "ai" && (
              <div className="glass-panel" style={styles.textPanel}>
                <div style={styles.panelHeader}>
                  <h3 style={styles.panelTitle}>Llama-3.3 Smart Breakdown</h3>
                  <button onClick={handleCopy} style={styles.actionBtn}>
                    {copied ? <Check size={16} color="#00b894" /> : <Copy size={16} />}
                    {copied ? "Copied" : "Copy"}
                  </button>
                </div>
                <div style={styles.formattedText}>{explanationText}</div>
              </div>
            )}

            {/* 💡 Important Questions Screen */}
            {selectedView === "questions" && (
              <div style={styles.listContainer}>
                <h2 style={styles.sectionTitle}>Exam & Interview Prep FAQs</h2>
                {qaList.map((qa, i) => (
                  <div key={i} className="glass-panel" style={styles.qaCard}>
                    <div 
                      onClick={() => toggleQA(i)} 
                      style={styles.qaHeader}
                    >
                      <h4 style={styles.qaQuestion}>Q{i+1}: {qa.q}</h4>
                      {qa.isOpen ? <ChevronUp size={20} color="#a78bfa" /> : <ChevronDown size={20} />}
                    </div>
                    {qa.isOpen && (
                      <div style={styles.qaAnswer}>
                        <p style={{ lineHeight: "1.6rem", color: "#d1d5db" }}>{qa.a}</p>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    );
  }

  return (
    <div style={styles.container} className="animate-fade-in">
      <button onClick={onBack} style={styles.backBtn}>
        <ArrowLeft size={18} />
        Back to Course Modules
      </button>

      <div style={styles.header}>
        <h1 style={styles.title}>{topic.title}</h1>
        <p style={styles.subtitle}>{topic.desc}</p>
        <span style={styles.badge}>{course.title}</span>
      </div>

      <div style={styles.grid}>
        {cards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div 
              key={idx} 
              className="glass-panel clickable" 
              style={styles.card}
              onClick={() => setSelectedView(card.id as any)}
            >
              <div style={{ ...styles.iconBg, backgroundColor: `${card.color}15`, color: card.color }}>
                <Icon size={24} />
              </div>
              <h3 style={styles.cardTitle}>{card.title}</h3>
              <p style={styles.cardDesc}>{card.desc}</p>
            </div>
          );
        })}
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
    cursor: "pointer",
  },
  header: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "8px",
    marginBottom: "10px",
  },
  title: {
    fontSize: "2.2rem",
    color: "#fff",
    fontWeight: 800,
  },
  subtitle: {
    fontSize: "1.05rem",
    color: "#9ca3af",
  },
  badge: {
    fontSize: "0.8rem",
    fontWeight: 700,
    background: "rgba(139, 61, 255, 0.15)",
    color: "#a78bfa",
    border: "1px solid rgba(139, 61, 255, 0.3)",
    padding: "4px 12px",
    borderRadius: "20px",
    width: "fit-content",
    marginTop: "8px",
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "20px",
  },
  card: {
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
    minHeight: "180px",
    cursor: "pointer",
    transition: "transform 0.2s, box-shadow 0.2s",
  },
  iconBg: {
    width: "50px",
    height: "50px",
    borderRadius: "14px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  cardTitle: {
    fontSize: "1.2rem",
    color: "#fff",
    fontWeight: 700,
  },
  cardDesc: {
    fontSize: "0.95rem",
    color: "#9ca3af",
  },
  loadingContainer: {
    display: "flex",
    flexDirection: "column" as const,
    alignItems: "center",
    justifyContent: "center",
    padding: "60px 20px",
    gap: "20px",
  },
  spinner: {
    width: "40px",
    height: "40px",
    border: "4px solid rgba(139, 61, 255, 0.15)",
    borderTop: "4px solid #8b3dff",
    borderRadius: "50%",
    animation: "spin 1s linear infinite",
  },
  viewContent: {
    marginTop: "10px",
  },
  sectionTitle: {
    fontSize: "1.4rem",
    color: "#fff",
    fontWeight: 700,
    marginBottom: "16px",
  },
  listContainer: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "14px",
  },
  videoCard: {
    display: "flex",
    alignItems: "center",
    padding: "16px 20px",
    gap: "18px",
  },
  videoIconBg: {
    width: "44px",
    height: "44px",
    borderRadius: "50%",
    background: "rgba(139, 61, 255, 0.12)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  videoTitle: {
    fontSize: "1.05rem",
    color: "#fff",
    fontWeight: 600,
    marginBottom: "4px",
  },
  videoDesc: {
    fontSize: "0.85rem",
    color: "#9ca3af",
  },
  durationBadge: {
    fontSize: "0.8rem",
    background: "rgba(255, 255, 255, 0.05)",
    color: "#8b3dff",
    fontWeight: 700,
    padding: "4px 8px",
    borderRadius: "8px",
    flexShrink: 0,
  },
  textPanel: {
    padding: "30px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "20px",
  },
  panelHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    borderBottom: "1px solid rgba(255, 255, 255, 0.08)",
    paddingBottom: "16px",
  },
  panelTitle: {
    fontSize: "1.3rem",
    color: "#fff",
    fontWeight: 700,
  },
  panelActions: {
    display: "flex",
    gap: "12px",
  },
  actionBtn: {
    display: "flex",
    alignItems: "center",
    gap: "6px",
    background: "rgba(255, 255, 255, 0.04)",
    color: "#fff",
    border: "1px solid rgba(255, 255, 255, 0.08)",
    borderRadius: "8px",
    padding: "8px 14px",
    fontSize: "0.85rem",
    fontWeight: 600,
    cursor: "pointer",
  },
  formattedText: {
    whiteSpace: "pre-wrap" as const,
    lineHeight: "1.7rem",
    color: "#e5e7eb",
    fontSize: "0.98rem",
  },
  qaCard: {
    display: "flex",
    flexDirection: "column" as const,
    padding: "0px",
    overflow: "hidden",
  },
  qaHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "20px",
    cursor: "pointer",
  },
  qaQuestion: {
    fontSize: "1.05rem",
    color: "#fff",
    fontWeight: 600,
  },
  qaAnswer: {
    padding: "0 20px 20px 20px",
    borderTop: "1px solid rgba(255, 255, 255, 0.04)",
    paddingTop: "16px",
    backgroundColor: "rgba(0, 0, 0, 0.1)",
  }
};
