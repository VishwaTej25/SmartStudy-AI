import React, { useState, useEffect } from "react";
import { db } from "../firebase";
import { collection, onSnapshot, query, orderBy, limit } from "firebase/firestore";
import { Trophy } from "lucide-react";

interface LeaderboardProps {
  userId: string;
}

interface UserProfile {
  uid: string;
  fullName: string;
  email: string;
  xp: number;
  streak: number;
}

const sampleLeaderboard: UserProfile[] = [
  { uid: "1", fullName: "Vishwa", email: "vishwa@example.com", xp: 9800, streak: 15 },
  { uid: "2", fullName: "Rahul", email: "rahul@example.com", xp: 9200, streak: 13 },
  { uid: "3", fullName: "Kiran", email: "kiran@example.com", xp: 8900, streak: 12 },
  { uid: "4", fullName: "Sneha", email: "sneha@example.com", xp: 8600, streak: 10 },
  { uid: "5", fullName: "Arjun", email: "arjun@example.com", xp: 8200, streak: 9 }
];

export const Leaderboard: React.FC<LeaderboardProps> = ({ userId }) => {
  const [users, setUsers] = useState<UserProfile[]>(sampleLeaderboard);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const usersRef = collection(db, "users");
    const q = query(usersRef, orderBy("xp", "desc"), limit(25));

    const unsubscribe = onSnapshot(q, (snapshot) => {
      const fetched = snapshot.docs.map((doc) => ({
        uid: doc.id,
        ...doc.data()
      })) as UserProfile[];

      if (fetched.length > 0) {
        setUsers(fetched);
      } else {
        setUsers(sampleLeaderboard);
      }
    }, (err) => {
      setError(err.message);
    });

    return () => unsubscribe();
  }, []);

  return (
    <div style={styles.container} className="animate-fade-in">
      <div style={styles.header}>
        <h1 style={styles.title}>Leaderboard 🏆</h1>
        <p style={styles.subtitle}>Compete with other learners and climb up the rankings by earning XP.</p>
      </div>

      {error && <div style={styles.error}>{error}</div>}

      <div style={styles.list}>
        {users.map((user, index) => {
          const rank = index + 1;
          const isCurrentUser = user.uid === userId;
          
          return (
            <div 
              key={user.uid} 
              className="glass-panel" 
              style={{
                ...styles.card,
                ...(isCurrentUser ? styles.currentUserCard : {}),
              }}
            >
              <div style={styles.cardLeft}>
                <div 
                  style={{
                    ...styles.rankBadge,
                    backgroundColor: rank === 1 ? "#facc15" : rank === 2 ? "#e2e8f0" : rank === 3 ? "#cd7f32" : "#7c3aed",
                  }}
                >
                  #{rank}
                </div>
                <div style={styles.userDetails}>
                  <span style={styles.name}>
                    {user.fullName || user.email?.split("@")[0] || "Learner"}
                    {isCurrentUser && <span style={styles.youBadge}>You</span>}
                  </span>
                  <span style={styles.meta}>{user.xp} XP • {user.streak} Day Streak</span>
                </div>
              </div>

              <Trophy 
                size={24} 
                color={rank === 1 ? "#facc15" : rank === 2 ? "#94a3b8" : rank === 3 ? "#b45309" : "rgba(255, 255, 255, 0.1)"} 
              />
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
  list: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "16px",
  },
  card: {
    padding: "20px 24px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    transition: "transform 0.2s",
  },
  currentUserCard: {
    borderColor: "#8b3dff",
    background: "rgba(139, 61, 255, 0.1)",
    boxShadow: "0 4px 20px rgba(139, 61, 255, 0.15)",
  },
  cardLeft: {
    display: "flex",
    alignItems: "center",
    gap: "20px",
  },
  rankBadge: {
    width: "48px",
    height: "48px",
    borderRadius: "14px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    fontWeight: 800,
    fontSize: "1.1rem",
    boxShadow: "0 4px 10px rgba(0, 0, 0, 0.1)",
  },
  userDetails: {
    display: "flex",
    flexDirection: "column" as const,
    gap: "4px",
  },
  name: {
    fontSize: "1.15rem",
    fontWeight: 700,
    color: "#fff",
    display: "flex",
    alignItems: "center",
    gap: "10px",
  },
  youBadge: {
    fontSize: "0.7rem",
    background: "#8b3dff",
    color: "#fff",
    padding: "2px 8px",
    borderRadius: "20px",
    fontWeight: 700,
    textTransform: "uppercase" as const,
  },
  meta: {
    fontSize: "0.88rem",
    color: "#9ca3af",
  },
};
