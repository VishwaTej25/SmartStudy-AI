import React, { useState, useEffect, useRef } from "react";
import { Send, Bot } from "lucide-react";
import { db } from "../firebase";
import { collection, onSnapshot, query, orderBy, limit, doc, writeBatch } from "firebase/firestore";
import { askGroq } from "../utils/groq";

interface ChatProps {
  userId: string;
}

interface ChatEntry {
  id: string;
  text: string;
  userMessage: boolean;
  createdAt: number;
}

export const Chat: React.FC<ChatProps> = ({ userId }) => {
  const [messages, setMessages] = useState<ChatEntry[]>([]);
  const [inputText, setInputText] = useState("");
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const chatsRef = collection(db, "users", userId, "chats");
    const q = query(chatsRef, orderBy("createdAt", "asc"), limit(100));

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const fetched = snapshot.docs.map((doc) => ({
        id: doc.id,
        ...doc.data()
      })) as ChatEntry[];

      if (fetched.length === 0) {
        setMessages([
          {
            id: "welcome",
            text: "Hello! I am your AI Study Assistant. Ask me any study doubt!",
            userMessage: false,
            createdAt: Date.now()
          }
        ]);
      } else {
        setMessages(fetched);
      }
    }, (err) => {
      setError(err.message);
    });

    return () => unsubscribe();
  }, [userId]);

  // Scroll to bottom on new messages
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const textToSend = inputText.trim();
    setInputText("");
    setError(null);

    try {
      const chatRef = collection(db, "users", userId, "chats");
      const userMsgDoc = doc(chatRef);
      const aiMsgDoc = doc(chatRef);

      const batch = writeBatch(db);

      batch.set(userMsgDoc, {
        id: userMsgDoc.id,
        text: textToSend,
        userMessage: true,
        createdAt: Date.now()
      });

      batch.set(aiMsgDoc, {
        id: aiMsgDoc.id,
        text: "🤖 Thinking...",
        userMessage: false,
        createdAt: Date.now() + 50
      });

      await batch.commit();

      // Query Groq API
      const responseText = await askGroq(textToSend);

      const finalBatch = writeBatch(db);
      finalBatch.set(aiMsgDoc, {
        id: aiMsgDoc.id,
        text: responseText,
        userMessage: false,
        createdAt: Date.now() + 100
      });
      await finalBatch.commit();
    } catch (err: any) {
      setError(err.message || "Failed to send message.");
    }
  };

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <Bot size={28} color="#8b3dff" />
        <div>
          <h1 style={styles.title}>AI Study Assistant 🤖</h1>
          <p style={styles.subtitle}>Ask questions about Java, DSA, DBMS, or any core computer science topic.</p>
        </div>
      </div>

      {error && <div style={styles.error}>{error}</div>}

      <div className="glass-panel" style={styles.chatBox}>
        <div style={styles.messagesContainer}>
          {messages.map((msg) => (
            <div 
              key={msg.id} 
              style={{
                ...styles.messageRow,
                justifyContent: msg.userMessage ? "flex-end" : "flex-start",
              }}
            >
              {!msg.userMessage && (
                <div style={styles.botAvatar}>🤖</div>
              )}
              <div 
                style={{
                  ...styles.bubble,
                  backgroundColor: msg.userMessage ? "#8b5cf6" : "rgba(27, 34, 53, 0.8)",
                  borderRadius: msg.userMessage ? "18px 18px 2px 18px" : "18px 18px 18px 2px",
                  border: msg.userMessage ? "none" : "1px solid rgba(255, 255, 255, 0.08)",
                }}
              >
                {msg.text}
              </div>
            </div>
          ))}
          <div ref={messagesEndRef} />
        </div>

        <form onSubmit={handleSendMessage} style={styles.inputForm}>
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            placeholder="Ask your doubt..."
            style={styles.input}
          />
          <button type="submit" style={styles.sendBtn}>
            <Send size={18} />
          </button>
        </form>
      </div>
    </div>
  );
};

const styles = {
  container: {
    maxWidth: "800px",
    margin: "0 auto",
    height: "calc(100vh - 80px)",
    display: "flex",
    flexDirection: "column" as const,
    gap: "20px",
  },
  header: {
    display: "flex",
    alignItems: "center",
    gap: "14px",
  },
  title: {
    fontSize: "1.8rem",
    fontWeight: 800,
    color: "#fff",
  },
  subtitle: {
    fontSize: "0.95rem",
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
  chatBox: {
    flexGrow: 1,
    display: "flex",
    flexDirection: "column" as const,
    overflow: "hidden",
    height: "100%",
  },
  messagesContainer: {
    flexGrow: 1,
    overflowY: "auto" as const,
    padding: "24px",
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  messageRow: {
    display: "flex",
    alignItems: "flex-end",
    gap: "10px",
    maxWidth: "80%",
  },
  botAvatar: {
    width: "36px",
    height: "36px",
    borderRadius: "50%",
    background: "rgba(139, 61, 255, 0.15)",
    border: "1px solid rgba(139, 61, 255, 0.3)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "1.1rem",
  },
  bubble: {
    padding: "14px 18px",
    color: "#fff",
    fontSize: "0.98rem",
    lineHeight: "1.5",
    wordBreak: "break-word" as const,
    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)",
  },
  inputForm: {
    display: "flex",
    padding: "16px 24px",
    background: "rgba(10, 27, 85, 0.3)",
    borderTop: "1px solid rgba(255, 255, 255, 0.05)",
    gap: "12px",
    alignItems: "center",
  },
  input: {
    flexGrow: 1,
    backgroundColor: "rgba(15, 23, 42, 0.6)",
    border: "1px solid rgba(255, 255, 255, 0.1)",
    borderRadius: "24px",
    padding: "14px 20px",
    fontSize: "0.95rem",
  },
  sendBtn: {
    background: "#8b3dff",
    color: "#fff",
    border: "none",
    borderRadius: "50%",
    width: "48px",
    height: "48px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    boxShadow: "0 4px 12px rgba(139, 61, 255, 0.3)",
  },
};
