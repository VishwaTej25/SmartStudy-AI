import type { Topic } from "./CourseDetails";

export const getTopicsForCourse = (courseId: string, courseTitle: string): Topic[] => {
  switch (courseId) {
    case "math_discrete":
      return [
        { id: "sets", title: "📐 Sets & Relations", desc: "Set operations, Cartesian products, equivalence relations and partitions." },
        { id: "logic", title: "🧠 Mathematical Logic", desc: "Propositional logic, truth tables, tautologies, and predicate calculus." },
        { id: "graphs", title: "🕸️ Graph Theory", desc: "Eulerian and Hamiltonian paths, trees, graph coloring, and isomorphism." },
        { id: "combinatorics", title: "🔢 Combinatorics", desc: "Permutations, combinations, pigeonhole principle, and generating functions." },
        { id: "recurrence", title: "🔄 Recurrence Relations", desc: "Solving linear recurrence relations and generating functions." }
      ];
    case "math_stats":
      return [
        { id: "prob_spaces", title: "📊 Probability Spaces", desc: "Axioms of probability, conditional probability, and Bayes' Theorem." },
        { id: "distributions", title: "📈 Probability Distributions", desc: "Binomial, Poisson, Normal, and Exponential distributions." },
        { id: "hypothesis", title: "🔬 Hypothesis Testing", desc: "Type I and II errors, t-tests, z-tests, and chi-square tests." },
        { id: "regression", title: "📉 Correlation & Regression", desc: "Least squares method, Pearson coefficient, and linear regression models." },
        { id: "anova", title: "📋 ANOVA", desc: "One-way and two-way Analysis of Variance." }
      ];
    case "c_cpp":
      return [
        { id: "pointers", title: "💾 Pointers & Memory", desc: "Pointer arithmetic, dynamic memory allocation (malloc/free, new/delete)." },
        { id: "oop_cpp", title: "🧱 OOP in C++", desc: "Classes, encapsulation, inheritance, polymorphism, and virtual functions." },
        { id: "templates", title: "🛠️ Templates & STL", desc: "Function/class templates, vectors, lists, maps, and iterators." },
        { id: "files", title: "📂 File Handling", desc: "Fstream library, reading and writing sequential/random files." },
        { id: "preprocessor", title: "⚙️ Preprocessor Macros", desc: "Conditional compilation, macros, and header file guards." }
      ];
    case "java":
      return [
        { id: "oops_java", title: "☕ OOP Principles", desc: "Classes, objects, inheritance, polymorphism, abstraction, and interfaces." },
        { id: "collections", title: "📚 Collections Framework", desc: "Lists, Sets, Maps, queues, and custom comparators." },
        { id: "exceptions", title: "⚠️ Exception Handling", desc: "Try-catch-finally block, throws clause, and custom exceptions." },
        { id: "multithreading", title: "🧵 Multithreading", desc: "Thread class, Runnable interface, synchronization, and inter-thread communication." },
        { id: "streams", title: "🌊 Streams & Lambda", desc: "Functional interfaces, lambda expressions, and pipeline stream operations." }
      ];
    case "python":
      return [
        { id: "py_basics", title: "🐍 Data Types & Control", desc: "Lists, tuples, dictionaries, sets, and comprehension constructs." },
        { id: "py_files", title: "📂 File I/O & Exceptions", desc: "Context managers, file modes, try-except blocks, and custom exceptions." },
        { id: "py_oop", title: "🧱 Object Oriented Python", desc: "Classes, dunder methods, inheritance, and encapsulation." },
        { id: "py_libs", title: "📊 NumPy & Pandas", desc: "Array operations, DataFrames, data cleaning, and manipulation." },
        { id: "py_adv", title: "⚙️ Decorators & Generators", desc: "Function decorators, generator expressions, and yield keyword." }
      ];
    case "dsa":
      return [
        { id: "linear", title: "🔗 Arrays & Linked Lists", desc: "Singly, doubly, and circular linked lists, array rotations." },
        { id: "stack_queue", title: "📥 Stacks & Queues", desc: "LIFO/FIFO concepts, priority queues, and deque implementation." },
        { id: "trees_graphs", title: "🌳 Trees & Graphs", desc: "BST, AVL trees, DFS, BFS, Dijkstra's algorithm, and MST." },
        { id: "sorting", title: "⚡ Sorting & Searching", desc: "QuickSort, MergeSort, HeapSort, Binary Search, and Hash Tables." },
        { id: "dp", title: "🧠 Dynamic Programming", desc: "Memoization, tabulation, Knapsack problem, and LCS." }
      ];
    case "dbms":
      return [
        { id: "er_model", title: "🗺️ ER Model & Schema", desc: "Entity-Relationship diagrams, constraints, and relational mappings." },
        { id: "algebra", title: "📐 Relational Algebra", desc: "Selection, projection, joins, and set operations." },
        { id: "sql", title: "🗄️ SQL Queries", desc: "DDL, DML, Joins, subqueries, group by, and aggregate functions." },
        { id: "normalization", title: "📋 Normalization", desc: "Functional dependencies, 1NF, 2NF, 3NF, and BCNF." },
        { id: "transactions", title: "🔄 Transactions & ACID", desc: "Concurrency control, locks, serializability, and recovery protocols." }
      ];
    case "os":
      return [
        { id: "scheduling", title: "🕒 Process Scheduling", desc: "FCFS, SJF, Round Robin, priority scheduling, and context switching." },
        { id: "sync", title: "🔒 Thread Synchronization", desc: "Critical section problem, semaphores, mutexes, and monitors." },
        { id: "mem_mgmt", title: "💿 Memory & Paging", desc: "Virtual memory, paging, segmentation, and page replacement algorithms." },
        { id: "file_sys", title: "📁 File Systems", desc: "Allocation methods, directory structures, and disk scheduling." },
        { id: "deadlocks", title: "🚫 Deadlock Management", desc: "Deadlock prevention, avoidance (Banker's algorithm), and detection." }
      ];
    case "networks":
      return [
        { id: "osi_model", title: "🌐 OSI & TCP/IP Layers", desc: "Functions of each layer, physical media, and framing." },
        { id: "ip_addr", title: "🏷️ IP Addressing", desc: "IPv4 classful/classless addressing, CIDR, and IPv6 headers." },
        { id: "routing", title: "🛣️ Routing Algorithms", desc: "Link-state, distance-vector, RIP, OSPF, and BGP protocols." },
        { id: "transport", title: "🔄 TCP & UDP Protocols", desc: "Flow control, congestion control, 3-way handshake, and port multiplexing." },
        { id: "app_layer", title: "🌍 Application Protocols", desc: "HTTP, DNS, SMTP, FTP, and DHCP specifications." }
      ];
    case "se":
      return [
        { id: "sdlc", title: "🏗️ SDLC Models", desc: "Waterfall, Spiral, V-Model, and Agile frameworks." },
        { id: "requirements", title: "📋 Software Requirements", desc: "SRS documentation, functional/non-functional requirements." },
        { id: "design_patterns", title: "🎨 Design & UML", desc: "Class diagrams, sequence diagrams, and Gang of Four design patterns." },
        { id: "testing_methods", title: "🧪 Software Testing", desc: "White-box, black-box testing, unit/integration testing." },
        { id: "git_agile", title: "🔄 Git & Agile Tools", desc: "Branching strategies, Scrum/Kanban practices, and team collaboration." }
      ];
    default:
      return [
        { id: `${courseId}_intro`, title: `📘 Intro to ${courseTitle}`, desc: `Foundational concepts and principles of ${courseTitle}.` },
        { id: `${courseId}_core`, title: `📚 Core ${courseTitle} Theory`, desc: `In-depth analysis of major frameworks and mechanisms in ${courseTitle}.` },
        { id: `${courseId}_practical`, title: `💻 Practical Applications`, desc: `Hands-on tutorials, projects, and implementation techniques for ${courseTitle}.` },
        { id: `${courseId}_design`, title: `🛠️ Design & Optimization`, desc: `Performance tuning, best practices, and architecture design for ${courseTitle}.` },
        { id: `${courseId}_review`, title: `🎓 Revision & Assessment`, desc: `Comprehensive review questions and mock evaluation for ${courseTitle}.` }
      ];
  }
};
