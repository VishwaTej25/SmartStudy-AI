import React, { useState } from "react";
import { auth, db } from "../firebase";
import {
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
} from "firebase/auth";
import { doc, setDoc } from "firebase/firestore";

interface AuthProps {
  onLoginSuccess: () => void;
}

const ADMIN_EMAIL = "admin123@gmail.com";
const ADMIN_PASSWORD = "admin";

export const Auth: React.FC<AuthProps> = ({ onLoginSuccess }) => {
  const [isSignUp, setIsSignUp] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [mobile, setMobile] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    const trimmedEmail = email.trim().toLowerCase();

    try {
      if (isSignUp) {
        // ── Sign Up flow ──────────────────────────────────────────────────
        const userCredential = await createUserWithEmailAndPassword(
          auth,
          trimmedEmail,
          password
        );
        const user = userCredential.user;

        await setDoc(doc(db, "users", user.uid), {
          uid: user.uid,
          fullName: fullName.trim(),
          mobile: mobile.trim(),
          email: trimmedEmail,
          xp: 0,
          streak: 1,
          premiumPlan: "",
          premiumUntil: 0,
          createdAt: Date.now(),
        });
      } else {
        // ── Sign In flow ──────────────────────────────────────────────────
        // Admin shortcut: bypass Firebase for the admin account
        // (Firebase requires min 6-char passwords, "admin" is 5)
        if (trimmedEmail === ADMIN_EMAIL && password === ADMIN_PASSWORD) {
          // Sign in via Firebase using the stored admin account.
          // If it doesn't exist yet, create it automatically.
          try {
            await signInWithEmailAndPassword(auth, ADMIN_EMAIL, "admin123");
          } catch (firstErr: any) {
            if (firstErr.code === "auth/user-not-found" || firstErr.code === "auth/invalid-credential") {
              // Create the admin account on first use
              await createUserWithEmailAndPassword(auth, ADMIN_EMAIL, "admin123");
            } else {
              throw firstErr;
            }
          }
        } else {
          await signInWithEmailAndPassword(auth, trimmedEmail, password);
        }
      }

      onLoginSuccess();
    } catch (err: any) {
      // Friendly error messages
      const code = err.code || "";
      if (code === "auth/user-not-found" || code === "auth/invalid-credential") {
        setError("No account found with this email. Please sign up first.");
      } else if (code === "auth/wrong-password") {
        setError("Incorrect password. Please try again.");
      } else if (code === "auth/email-already-in-use") {
        setError("This email is already registered. Please sign in.");
      } else if (code === "auth/weak-password") {
        setError("Password must be at least 6 characters.");
      } else {
        setError(err.message || "An authentication error occurred.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      {/* Decorative blobs */}
      <div style={styles.blobPurple} />
      <div style={styles.blobBlue} />

      <div className="glass-panel" style={styles.card}>
        {/* Logo */}
        <div style={styles.logoRow}>
          <span style={styles.logoEmoji}>🚀</span>
          <span style={styles.logoName}>SmartStudy AI</span>
        </div>

        <h2 style={styles.title}>
          {isSignUp ? "Create Account" : "Welcome Back 👋"}
        </h2>
        <p style={styles.subtitle}>
          {isSignUp
            ? "Start your AI-powered learning journey"
            : "Sign in to access your study dashboard"}
        </p>

        {error && <div style={styles.error}>{error}</div>}

        <form onSubmit={handleSubmit} style={styles.form}>
          {isSignUp && (
            <>
              <div style={styles.inputGroup}>
                <label style={styles.label}>Full Name</label>
                <input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="Vishwa"
                  required
                  style={styles.input}
                />
              </div>
              <div style={styles.inputGroup}>
                <label style={styles.label}>Mobile Number</label>
                <input
                  type="tel"
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                  placeholder="+91 9999999999"
                  required
                  style={styles.input}
                />
              </div>
            </>
          )}

          <div style={styles.inputGroup}>
            <label style={styles.label}>Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              required
              style={styles.input}
            />
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              style={styles.input}
            />
          </div>

          <button type="submit" disabled={loading} style={styles.button}>
            {loading ? "Processing..." : isSignUp ? "Sign Up" : "Sign In"}
          </button>
        </form>

        <div style={styles.toggleContainer}>
          <span style={styles.toggleText}>
            {isSignUp ? "Already have an account?" : "Don't have an account?"}
          </span>
          <button
            onClick={() => {
              setIsSignUp(!isSignUp);
              setError(null);
            }}
            style={styles.toggleBtn}
          >
            {isSignUp ? "Sign In" : "Sign Up"}
          </button>
        </div>

        {!isSignUp && (
          <p style={styles.adminHint}>
            Admin? Use <code style={{ color: "#a78bfa" }}>admin123@gmail.com</code>{" "}
            / <code style={{ color: "#a78bfa" }}>admin</code>
          </p>
        )}
      </div>
    </div>
  );
};

const styles: Record<string, any> = {
  container: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "100vh",
    padding: "20px",
    position: "relative",
    overflow: "hidden",
  },
  blobPurple: {
    position: "fixed",
    width: "500px",
    height: "500px",
    borderRadius: "50%",
    background: "radial-gradient(circle, rgba(139,61,255,0.15) 0%, transparent 70%)",
    top: "-100px",
    right: "-100px",
    pointerEvents: "none",
  },
  blobBlue: {
    position: "fixed",
    width: "400px",
    height: "400px",
    borderRadius: "50%",
    background: "radial-gradient(circle, rgba(0,229,255,0.1) 0%, transparent 70%)",
    bottom: "-80px",
    left: "-80px",
    pointerEvents: "none",
  },
  card: {
    width: "100%",
    maxWidth: "440px",
    padding: "40px 36px",
    textAlign: "center" as const,
    position: "relative",
    zIndex: 1,
  },
  logoRow: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "10px",
    marginBottom: "28px",
  },
  logoEmoji: { fontSize: "1.8rem" },
  logoName: {
    fontSize: "1.4rem",
    fontWeight: 800,
    background: "linear-gradient(90deg, #fff 0%, #a78bfa 100%)",
    WebkitBackgroundClip: "text",
    WebkitTextFillColor: "transparent",
  },
  title: { fontSize: "1.9rem", marginBottom: "8px", color: "#fff", fontWeight: 800 },
  subtitle: { fontSize: "0.95rem", color: "#9ca3af", marginBottom: "28px" },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "18px",
    textAlign: "left",
  },
  inputGroup: { display: "flex", flexDirection: "column", gap: "8px" },
  label: { fontSize: "0.85rem", fontWeight: 600, color: "#9ca3af" },
  input: {
    background: "rgba(255,255,255,0.05)",
    border: "1px solid rgba(255,255,255,0.1)",
    borderRadius: "12px",
    padding: "13px 16px",
    color: "#fff",
    fontSize: "0.95rem",
    outline: "none",
    width: "100%",
    boxSizing: "border-box",
    transition: "border-color 0.2s",
  },
  button: {
    background: "linear-gradient(135deg, #8b3dff, #6d28d9)",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    padding: "14px",
    fontSize: "1rem",
    fontWeight: 700,
    marginTop: "6px",
    boxShadow: "0 4px 20px rgba(139, 61, 255, 0.4)",
    cursor: "pointer",
    transition: "opacity 0.2s",
  },
  error: {
    background: "rgba(239, 68, 68, 0.12)",
    border: "1px solid rgba(239, 68, 68, 0.3)",
    borderRadius: "10px",
    color: "#f87171",
    padding: "10px 14px",
    fontSize: "0.85rem",
    marginBottom: "16px",
    textAlign: "left",
  },
  toggleContainer: {
    marginTop: "24px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "8px",
  },
  toggleText: { fontSize: "0.9rem", color: "#9ca3af" },
  toggleBtn: {
    background: "none",
    border: "none",
    color: "#a78bfa",
    fontSize: "0.9rem",
    fontWeight: 700,
    padding: 0,
    cursor: "pointer",
  },
  adminHint: {
    marginTop: "16px",
    fontSize: "0.78rem",
    color: "#4b5563",
  },
};
