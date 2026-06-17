package com.example.smartstudy.backend

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.example.smartstudy.model.Topic

class SmartStudyBackend(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    fun signIn(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun signUp(
        fullName: String,
        mobile: String,
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    onResult(Result.failure(IllegalStateException("User account was not created.")))
                    return@addOnSuccessListener
                }

                val profile = UserProfile(
                    uid = user.uid,
                    fullName = fullName.trim(),
                    mobile = mobile.trim(),
                    email = email.trim(),
                    xp = 0,
                    streak = 1
                )

                db.collection(USERS).document(user.uid).set(profile)
                    .addOnSuccessListener { onResult(Result.success(Unit)) }
                    .addOnFailureListener { onResult(Result.failure(it)) }
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun signOut() {
        auth.signOut()
    }

    fun listenProfile(onUpdate: (UserProfile?) -> Unit, onError: (Exception) -> Unit): ListenerRegistration? {
        val uid = currentUserId ?: return null
        return db.collection(USERS).document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                onUpdate(snapshot?.toUserProfile())
            }
    }

    fun listenSettings(onUpdate: (UserSettings) -> Unit, onError: (Exception) -> Unit): ListenerRegistration? {
        val uid = currentUserId ?: return null
        return db.collection(USERS).document(uid).collection(PRIVATE).document(SETTINGS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                onUpdate(snapshot?.toObject(UserSettings::class.java) ?: UserSettings())
            }
    }

    fun updateSettings(settings: UserSettings, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        db.collection(USERS).document(uid).collection(PRIVATE).document(SETTINGS)
            .set(settings.copy(updatedAt = System.currentTimeMillis()), SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenCourses(onUpdate: (List<Course>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
        return db.collection(COURSES)
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val remoteCourses = snapshot?.documents.orEmpty().map { it.toCourse() }
                println("COURSES COUNT = ${remoteCourses.size}")
                if (remoteCourses.size < defaultCourses.size) {
                    seedCourses()
                    onUpdate(defaultCourses)
                } else {
                    onUpdate(remoteCourses)
                }
            }
    }
    fun listenTopics(
        courseId: String,
        onUpdate: (List<Topic>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {

        return db.collection("courses")
            .document(courseId)
            .collection("topics")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val topics = snapshot?.documents?.map {
                    it.toObject(Topic::class.java)?.copy(id = it.id)
                        ?: Topic(id = it.id)
                } ?: emptyList()

                onUpdate(topics)
            }
    }

    fun listenEnrollments(onUpdate: (Map<String, Enrollment>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration? {
        val uid = currentUserId ?: return null
        return db.collection(USERS).document(uid).collection(ENROLLMENTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                onUpdate(
                    snapshot?.documents.orEmpty()
                        .map { it.toEnrollment() }
                        .associateBy { it.courseId }
                )
            }
    }

    fun setEnrollment(course: Course, enrolled: Boolean, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        val ref = db.collection(USERS).document(uid).collection(ENROLLMENTS).document(course.id)
        val task = if (enrolled) {
            ref.set(Enrollment(courseId = course.id, progress = 0.0))
        } else {
            ref.delete()
        }

        task.addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenPlans(onUpdate: (List<StudyPlan>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration? {
        val uid = currentUserId ?: return null
        return db.collection(USERS).document(uid).collection(PLANS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                onUpdate(snapshot?.documents.orEmpty().map { it.toStudyPlan() })
            }
    }

    fun addPlan(subject: String, time: String, priority: String, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        val ref = db.collection(USERS).document(uid).collection(PLANS).document()
        ref.set(
            StudyPlan(
                id = ref.id,
                subject = subject.trim(),
                time = time.trim(),
                priority = priority,
                completed = false
            )
        )
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun setPlanCompleted(plan: StudyPlan, completed: Boolean, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        db.collection(USERS).document(uid).collection(PLANS).document(plan.id)
            .update("completed", completed)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun saveTestAttempt(
        testId: String,
        courseId: String,
        title: String,
        score: Int,
        totalQuestions: Int,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        val attemptRef = db.collection(USERS).document(uid).collection(TEST_ATTEMPTS).document()
        val percentage =
            if (totalQuestions == 0)
                0
            else
                ((score.toDouble() / totalQuestions) * 100).toLong()

        val attempt = TestAttempt(
            id = attemptRef.id,
            testId = testId,
            courseId = courseId,
            title = title,
            score = score.toLong(),
            totalQuestions = totalQuestions.toLong(),
            percentage = percentage
        )

        val userRef = db.collection(USERS).document(uid)
        val enrollmentRef = userRef.collection(ENROLLMENTS).document(courseId)

        db.runBatch { batch ->
            batch.set(attemptRef, attempt)
            batch.set(
                enrollmentRef,
                Enrollment(courseId = courseId, progress = percentage / 100.0),
                SetOptions.merge()
            )
            batch.set(
                userRef,
                mapOf(
                    "xp" to FieldValue.increment(percentage * 10),
                    "streak" to FieldValue.increment(1)
                ),
                SetOptions.merge()
            )
        }
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenChat(onUpdate: (List<ChatEntry>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration? {
        val uid = currentUserId ?: return null
        return db.collection(USERS).document(uid).collection(CHATS)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                onUpdate(snapshot?.documents.orEmpty().map { it.toChatEntry() })
            }
    }

    fun sendChatMessage(text: String, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        val chatRef = db.collection(USERS).document(uid).collection(CHATS)
        val userMessageRef = chatRef.document()
        val aiMessageRef = chatRef.document()
        val trimmed = text.trim()

        db.runBatch { batch ->
            batch.set(
                userMessageRef,
                ChatEntry(
                    id = userMessageRef.id,
                    text = trimmed,
                    userMessage = true
                )
            )

        }
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun listenLeaderboard(onUpdate: (List<UserProfile>) -> Unit, onError: (Exception) -> Unit): ListenerRegistration {
        return db.collection(USERS)
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(25)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val users = snapshot?.documents.orEmpty().map { it.toUserProfile() }
                onUpdate(users.ifEmpty { sampleLeaderboard })
            }
    }

    fun listenProgress(
        courses: List<Course>,
        onUpdate: (ProgressSummary) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? {
        val uid = currentUserId ?: return null
        val userRef = db.collection(USERS).document(uid)
        return userRef.collection(ENROLLMENTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val enrollments = snapshot?.documents.orEmpty().map { it.toEnrollment() }
                val namesById = courses.associate { it.id to it.title }
                val courseProgress = enrollments.map {
                    (namesById[it.courseId] ?: it.courseId) to it.progress
                }

                userRef.collection(TEST_ATTEMPTS).get()
                    .addOnSuccessListener { attempts ->
                        val scores = attempts.documents.mapNotNull { it.getLong("percentage") }
                        onUpdate(
                            ProgressSummary(
                                coursesEnrolled = enrollments.size,
                                testsAttempted = attempts.size().toLong(),
                                averageScore =
                                    if (scores.isEmpty())
                                        0
                                    else
                                        scores.average().toLong(),
                                learningStreak = 1,
                                courseProgress = courseProgress
                            )
                        )
                    }
                    .addOnFailureListener { onError(it) }
            }
    }

    fun activatePremium(plan: String, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        val now = System.currentTimeMillis()
        val duration =
            if (plan == "yearly")
                365L * 24L * 60L * 60L * 1000L
            else
                30L * 24L * 60L * 60L * 1000L

        db.collection(USERS).document(uid)
            .set(
                mapOf(
                    "premiumPlan" to plan,
                    "premiumUntil" to now + duration
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun updateEnrollmentProgress(courseId: String, progress: Double, onResult: (Result<Unit>) -> Unit = {}) {
        val uid = currentUserId ?: return onResult(Result.failure(IllegalStateException("Login required.")))
        db.collection(USERS).document(uid).collection(ENROLLMENTS).document(courseId)
            .set(mapOf("progress" to progress), SetOptions.merge())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun seedCourses() {
        val batch = db.batch()
        defaultCourses.forEach { course ->
            batch.set(db.collection(COURSES).document(course.id), course)
        }
        batch.commit()
    }

    private fun DocumentSnapshot.toCourse(): Course {
        return toObject(Course::class.java)?.copy(id = id) ?: Course(id = id)
    }

    private fun DocumentSnapshot.toEnrollment(): Enrollment {
        return toObject(Enrollment::class.java)?.copy(courseId = id) ?: Enrollment(courseId = id)
    }

    private fun DocumentSnapshot.toStudyPlan(): StudyPlan {
        return toObject(StudyPlan::class.java)?.copy(id = id) ?: StudyPlan(id = id)
    }

    private fun DocumentSnapshot.toChatEntry(): ChatEntry {
        return toObject(ChatEntry::class.java)?.copy(id = id) ?: ChatEntry(id = id)
    }

    private fun DocumentSnapshot.toUserProfile(): UserProfile {
        return toObject(UserProfile::class.java)?.copy(uid = id) ?: UserProfile(uid = id)
    }

    companion object {
        private const val USERS = "users"
        private const val COURSES = "courses"
        private const val ENROLLMENTS = "enrollments"
        private const val PLANS = "plans"
        private const val CHATS = "chats"
        private const val PRIVATE = "private"
        private const val SETTINGS = "settings"
        private const val TEST_ATTEMPTS = "testAttempts"

        val defaultCourses = listOf(
            Course("math_discrete", "Discrete Mathematics", "Sets, Logic, Relations, Graphs & Combinatorics", "📐", 1),
            Course("math_stats", "Probability & Statistics", "Probability Distributions, Hypothesis Testing & Regression", "📊", 2),
            Course("c_cpp", "C & C++ Programming", "Procedural & Object-Oriented Programming Fundamentals", "💻", 3),
            Course("java", "Java Programming", "Core Java + OOPs + Advanced Java", "☕", 4),
            Course("python", "Python Programming", "Python Basics, Scripting & Libraries", "🐍", 5),
            Course("dsa", "Data Structures & Algorithms", "Stack, Queue, Trees, Graphs, Sorting & Searching", "🧠", 6),
            Course("dbms", "DBMS", "Database Management Systems & SQL", "🗄️", 7),
            Course("os", "Operating Systems", "Process Management, Memory, Storage & I/O", "💿", 8),
            Course("networks", "Computer Networks", "TCP/IP OSI Model, Routing & Protocols", "🌐", 9),
            Course("se", "Software Engineering", "SDLC, Agile, Git, testing & UML", "🏗️", 10),
            Course("toc", "Theory of Computation", "Finite Automata, Context Free Grammars & Turing Machines", "⚙️", 11),
            Course("compiler", "Compiler Design", "Lexical Analysis, Parsing, Code Generation & Optimization", "🔧", 12),
            Course("coa", "Computer Architecture", "CPU, ALU, Memory Hierarchy & Instruction Pipelines", "🖥️", 13),
            Course("webdev", "Web Development", "HTML, CSS, JS, React, Node.js & Full-stack dev", "🌍", 14),
            Course("ai", "Artificial Intelligence", "Search Algorithms, Knowledge Representation & Planning", "🤖", 15),
            Course("ml", "Machine Learning", "Regression, Classification, Clustering & Neural Networks", "🧠", 16),
            Course("crypto", "Cryptography & Security", "Symmetric/Asymmetric Encryption, Hashes & Network Security", "🔐", 17),
            Course("cloud", "Cloud Computing", "Virtualization, SaaS/PaaS/IaaS, AWS, Azure & GCP", "☁️", 18),
            Course("bigdata", "Big Data Analytics", "Hadoop, Spark, NoSQL & Data Engineering", "🛢️", 19),
            Course("iot", "Internet of Things", "Sensors, Actuators, Microcontrollers & IoT Protocols", "🔌", 20),
            Course("mobile", "Mobile Development", "Kotlin, Swift, Flutter & Android/iOS Apps", "📱", 21),
            Course("devops", "DevOps & CI/CD", "Docker, Kubernetes, Jenkins & Infrastructure as Code", "🔄", 22),
            Course("micro", "Microprocessors", "8085/8086 Assembly Language, Interfacing & Interrupts", "📟", 23),
            Course("dld", "Digital Logic Design", "Logic Gates, Boolean Algebra, Combinational & Sequential Circuits", "🔢", 24),
            Course("graphics", "Computer Graphics", "Rasterization, Ray Tracing, OpenGL & 3D Rendering", "🎨", 25),
            Course("testing", "Software Testing", "Unit, Integration, System Testing & Automation QA", "🧪", 26),
            Course("distributed", "Distributed Systems", "Consensus, RPCs, MapReduce & Fault Tolerance", "🌐", 27),
            Course("blockchain", "Blockchain Technology", "Consensus Algorithms, Cryptocurrencies & Smart Contracts", "⛓️", 28),
            Course("datamining", "Data Mining", "Data Preprocessing, Association Rules & Pattern Discovery", "⛏️", 29),
            Course("nlp", "Natural Language Processing", "Text Tokenization, Parsing, Sentiment Analysis & LLMs", "🗣️", 30),
            Course("ooad", "OOAD using UML", "Use Cases, Class/Sequence Diagrams & Design Patterns", "📐", 31),
            Course("dsp", "Digital Signal Processing", "Signals, Fourier Transforms, Filters & LTI Systems", "📡", 32),
            Course("vlsi", "VLSI Design", "CMOS Circuits, Layouts, Verilog/VHDL & ASIC Design", "🔌", 33),
            Course("embedded", "Embedded Systems", "Real-Time OS (RTOS), Firmware & Microcontrollers", "📟", 34),
            Course("netsec", "Network Security", "Firewalls, VPNs, IDS/IPS & Threat Prevention", "🛡️", 35),
            Course("hci", "Human-Computer Interaction", "User Experience (UX) Design, Prototyping & Usability", "👥", 36),
            Course("parallel", "Parallel Computing", "Multithreading, OpenMP, MPI & GPU programming (CUDA)", "⚡", 37),
            Course("deeplearning", "Deep Learning", "Convolutional & Recurrent Nets, PyTorch & TensorFlow", "🧠", 38),
            Course("softcomp", "Soft Computing", "Fuzzy Logic, Genetic Algorithms & Swarm Intelligence", "🐝", 39),
            Course("ir", "Information Retrieval", "Search Engines, Indexing, TF-IDF & PageRank", "🔍", 40)
        )

        val sampleLeaderboard = listOf(
            UserProfile(fullName = "Vishwa", xp = 9800, streak = 15),
            UserProfile(fullName = "Rahul", xp = 9200, streak = 13),
            UserProfile(fullName = "Kiran", xp = 8900, streak = 12),
            UserProfile(fullName = "Sneha", xp = 8600, streak = 10),
            UserProfile(fullName = "Arjun", xp = 8200, streak = 9)
        )
    }
}
