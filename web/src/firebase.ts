import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyC23ekaw9xH5gcJqHA1gPwmKCU3vYItJ24",
  authDomain: "smartstudyai-615b5.firebaseapp.com",
  projectId: "smartstudyai-615b5",
  storageBucket: "smartstudyai-615b5.firebasestorage.app",
  messagingSenderId: "347027395605",
  appId: "1:347027395605:web:c0ff00ff00ff00ff00ff00"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);
