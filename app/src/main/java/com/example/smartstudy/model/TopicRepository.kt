package com.example.smartstudy.model

object TopicRepository {
    fun getTopicsForCourse(courseId: String, courseTitle: String): List<Topic> {
        return when (courseId) {
            "math_discrete" -> listOf(
                Topic(title = "📐 Sets & Relations", content = "Set operations, Cartesian products, equivalence relations and partitions."),
                Topic(title = "🧠 Mathematical Logic", content = "Propositional logic, truth tables, tautologies, and predicate calculus."),
                Topic(title = "🕸️ Graph Theory", content = "Eulerian and Hamiltonian paths, trees, graph coloring, and isomorphism."),
                Topic(title = "🔢 Combinatorics", content = "Permutations, combinations, pigeonhole principle, and generating functions."),
                Topic(title = "🔄 Recurrence Relations", content = "Solving linear recurrence relations and generating functions.")
            )
            "math_stats" -> listOf(
                Topic(title = "📊 Probability Spaces", content = "Axioms of probability, conditional probability, and Bayes' Theorem."),
                Topic(title = "📈 Probability Distributions", content = "Binomial, Poisson, Normal, and Exponential distributions."),
                Topic(title = "🔬 Hypothesis Testing", content = "Type I and II errors, t-tests, z-tests, and chi-square tests."),
                Topic(title = "📉 Correlation & Regression", content = "Least squares method, Pearson coefficient, and linear regression models."),
                Topic(title = "📋 ANOVA", content = "One-way and two-way Analysis of Variance.")
            )
            "c_cpp" -> listOf(
                Topic(title = "💾 Pointers & Memory", content = "Pointer arithmetic, dynamic memory allocation (malloc/free, new/delete)."),
                Topic(title = "🧱 OOP in C++", content = "Classes, encapsulation, inheritance, polymorphism, and virtual functions."),
                Topic(title = "🛠️ Templates & STL", content = "Function/class templates, vectors, lists, maps, and iterators."),
                Topic(title = "📂 File Handling", content = "Fstream library, reading and writing sequential/random files."),
                Topic(title = "⚙️ Preprocessor Macros", content = "Conditional compilation, macros, and header file guards.")
            )
            "java" -> listOf(
                Topic(title = "☕ OOP Principles", content = "Classes, objects, inheritance, polymorphism, abstraction, and interfaces."),
                Topic(title = "📚 Collections Framework", content = "Lists, Sets, Maps, queues, and custom comparators."),
                Topic(title = "⚠️ Exception Handling", content = "Try-catch-finally block, throws clause, and custom exceptions."),
                Topic(title = "🧵 Multithreading", content = "Thread class, Runnable interface, synchronization, and inter-thread communication."),
                Topic(title = "🌊 Streams & Lambda", content = "Functional interfaces, lambda expressions, and pipeline stream operations.")
            )
            "python" -> listOf(
                Topic(title = "🐍 Data Types & Control", content = "Lists, tuples, dictionaries, sets, and comprehension constructs."),
                Topic(title = "📂 File I/O & Exceptions", content = "Context managers, file modes, try-except blocks, and custom exceptions."),
                Topic(title = "🧱 Object Oriented Python", content = "Classes, dunder methods, inheritance, and encapsulation."),
                Topic(title = "📊 NumPy & Pandas", content = "Array operations, DataFrames, data cleaning, and manipulation."),
                Topic(title = "⚙️ Decorators & Generators", content = "Function decorators, generator expressions, and yield keyword.")
            )
            "dsa" -> listOf(
                Topic(title = "🔗 Arrays & Linked Lists", content = "Singly, doubly, and circular linked lists, array rotations."),
                Topic(title = "📥 Stacks & Queues", content = "LIFO/FIFO concepts, priority queues, and deque implementation."),
                Topic(title = "🌳 Trees & Graphs", content = "BST, AVL trees, DFS, BFS, Dijkstra's algorithm, and MST."),
                Topic(title = "⚡ Sorting & Searching", content = "QuickSort, MergeSort, HeapSort, Binary Search, and Hash Tables."),
                Topic(title = "🧠 Dynamic Programming", content = "Memoization, tabulation, Knapsack problem, and LCS.")
            )
            "dbms" -> listOf(
                Topic(title = "🗺️ ER Model & Schema", content = "Entity-Relationship diagrams, constraints, and relational mappings."),
                Topic(title = "📐 Relational Algebra", content = "Selection, projection, joins, and set operations."),
                Topic(title = "🗄️ SQL Queries", content = "DDL, DML, Joins, subqueries, group by, and aggregate functions."),
                Topic(title = "📋 Normalization", content = "Functional dependencies, 1NF, 2NF, 3NF, and BCNF."),
                Topic(title = "🔄 Transactions & ACID", content = "Concurrency control, locks, serializability, and recovery protocols.")
            )
            "os" -> listOf(
                Topic(title = "🕒 Process Scheduling", content = "FCFS, SJF, Round Robin, priority scheduling, and context switching."),
                Topic(title = "🔒 Thread Synchronization", content = "Critical section problem, semaphores, mutexes, and monitors."),
                Topic(title = "💿 Memory & Paging", content = "Virtual memory, paging, segmentation, and page replacement algorithms."),
                Topic(title = "📁 File Systems", content = "Allocation methods, directory structures, and disk scheduling."),
                Topic(title = "🚫 Deadlock Management", content = "Deadlock prevention, avoidance (Banker's algorithm), and detection.")
            )
            "networks" -> listOf(
                Topic(title = "🌐 OSI & TCP/IP Layers", content = "Functions of each layer, physical media, and framing."),
                Topic(title = "🏷️ IP Addressing", content = "IPv4 classful/classless addressing, CIDR, and IPv6 headers."),
                Topic(title = "🛣️ Routing Algorithms", content = "Link-state, distance-vector, RIP, OSPF, and BGP protocols."),
                Topic(title = "🔄 TCP & UDP Protocols", content = "Flow control, congestion control, 3-way handshake, and port multiplexing."),
                Topic(title = "🌍 Application Protocols", content = "HTTP, DNS, SMTP, FTP, and DHCP specifications.")
            )
            "se" -> listOf(
                Topic(title = "🏗️ SDLC Models", content = "Waterfall, Spiral, V-Model, and Agile frameworks."),
                Topic(title = "📋 Software Requirements", content = "SRS documentation, functional/non-functional requirements."),
                Topic(title = "🎨 Design & UML", content = "Class diagrams, sequence diagrams, and Gang of Four design patterns."),
                Topic(title = "🧪 Software Testing", content = "White-box, black-box testing, unit/integration testing."),
                Topic(title = "🔄 Git & Agile Tools", content = "Branching strategies, Scrum/Kanban practices, and team collaboration.")
            )
            else -> listOf(
                Topic(title = "📘 Introduction to $courseTitle", content = "Foundations and core concepts of $courseTitle."),
                Topic(title = "📚 Core Theory", content = "In-depth discussion of main components in $courseTitle."),
                Topic(title = "🖥️ Practical Applications", content = "Real-world projects and industry use-cases of $courseTitle."),
                Topic(title = "⚙️ Best Practices & Optimization", content = "Coding standards, patterns and optimisations for $courseTitle."),
                Topic(title = "🎓 Review & Mock Test", content = "Revision notes and mock assessment for $courseTitle.")
            )
        }
    }
}
