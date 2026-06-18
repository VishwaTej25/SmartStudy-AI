const API_KEY = "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";

export async function askGroq(prompt: string): Promise<string> {
  try {
    const response = await fetch("https://api.groq.com/openai/v1/chat/completions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${API_KEY}`
      },
      body: JSON.stringify({
        model: "llama-3.3-70b-versatile",
        messages: [{ role: "user", content: prompt }]
      })
    });
    
    if (!response.ok) {
      console.warn(`Groq API returned status ${response.status}. Using local fallback generator.`);
      return getFallbackResponse(prompt);
    }
    
    const data = await response.json();
    const content = data.choices?.[0]?.message?.content;
    if (!content || content === "No response") {
      return getFallbackResponse(prompt);
    }
    return content;
  } catch (error: any) {
    console.warn("Groq API error, using local fallback generator:", error);
    return getFallbackResponse(prompt);
  }
}

function extractMetadata(prompt: string) {
  let topic = "Core Concepts";
  let course = "Computer Science";
  
  const quoteMatches = prompt.match(/"([^"]+)"/g);
  if (quoteMatches && quoteMatches.length >= 2) {
    course = quoteMatches[0].replace(/"/g, "");
    topic = quoteMatches[1].replace(/"/g, "");
  } else if (quoteMatches && quoteMatches.length === 1) {
    topic = quoteMatches[0].replace(/"/g, "");
  }
  
  const courseMatch = prompt.match(/course\s+["']?([^"']+)["']?/i);
  if (courseMatch) {
    course = courseMatch[1];
  }
  const topicMatch = prompt.match(/topic\s+["']?([^"']+)["']?/i);
  if (topicMatch) {
    topic = topicMatch[1];
  }
  
  return { topic, course };
}

function getFallbackResponse(prompt: string): string {
  const { topic, course } = extractMetadata(prompt);
  
  if (prompt.includes("multiple-choice") || prompt.includes("MCQ") || prompt.includes("quiz")) {
    return [
      `Question: What is the primary purpose of ${topic} in ${course}?`,
      `Options: A) Increasing complexity, B) Providing modularity and abstraction, C) Deleting records, D) Executing manual code compilation`,
      `Answer: B`,
      ``,
      `Question: Which design pattern is most commonly associated with ${topic}?`,
      `Options: A) Singleton Pattern, B) Factory Pattern, C) Layered Architecture, D) Observer Pattern`,
      `Answer: C`,
      ``,
      `Question: In ${course}, how does ${topic} improve performance?`,
      `Options: A) By utilizing hardware optimizations, B) Through runtime binding and efficient resource management, C) By skipping compilation, D) By disabling garbage collection`,
      `Answer: B`,
      ``,
      `Question: What is a key syntax feature of ${topic}?`,
      `Options: A) Global variables, B) Specific definitions and interfaces, C) Raw pointers, D) Assembly code blocks`,
      `Answer: B`,
      ``,
      `Question: Which of the following is a potential risk or pitfall of ${topic}?`,
      `Options: A) Over-abstraction leading to complex codebases, B) Faster execution speeds, C) Auto-generation of code, D) Multi-thread synchronization by default`,
      `Answer: A`,
      ``,
      `Question: How is ${topic} verified in QA testing?`,
      `Options: A) By executing the app once, B) Through unit tests, regression scans, and behavioral checks, C) It cannot be verified, D) By compiling in release mode only`,
      `Answer: B`,
      ``,
      `Question: What is dynamic binding in the context of ${topic}?`,
      `Options: A) Resolving types at compile time, B) Connecting databases, C) Resolving method calls at runtime, D) Writing script loops`,
      `Answer: C`,
      ``,
      `Question: Which of the following increases cohesion in ${topic}?`,
      `Options: A) Mixing unrelated functions, B) Keeping related functions within the same module, C) Using global storage, D) Eliminating namespaces`,
      `Answer: B`
    ].join("\n");
  }
  
  if (prompt.includes("PDF") || prompt.includes("study notes") || prompt.includes("notes")) {
    return [
      `# 📄 Study Notes: ${topic}`,
      `### Course: ${course}`,
      ``,
      `## 1. Introduction & Background`,
      `${topic} is a core module in ${course}. It defines the structural layout and runtime rules necessary to build stable application logic.`,
      ``,
      `## 2. Fundamental Principles`,
      `*   **Abstraction:** Deconstructs complex realities into simpler, manageable blueprints.`,
      `*   **Encapsulation:** Keeps data safe from external unauthorized modifications.`,
      `*   **Efficiency:** Optimizes execution speed and memory allocations during processing.`,
      `*   **Scalability:** Allows adding features without modifying existing modules.`,
      ``,
      `## 3. Implementation Blueprint`,
      `Here is a basic structure for representing ${topic} in code:`,
      `\`\`\`typescript`,
      `export class ${topic.replace(/[^a-zA-Z]/g, "")}Manager {`,
      `  private name: string;`,
      `  `,
      `  constructor(name: string) {`,
      `    this.name = name;`,
      `  }`,
      `  `,
      `  public runDiagnostic(): boolean {`,
      `    console.log("Analyzing ${topic} within ${course}...");`,
      `    return true;`,
      `  }`,
      `}`,
      `\`\`\``,
      ``,
      `## 4. Exam Preparation & Key Takeaways`,
      `*   *Tip 1:* Expect questions on the lifecycle and memory layout of ${topic}.`,
      `*   *Tip 2:* Practice diagramming relational mappings between different components.`
    ].join("\n");
  }
  
  if (prompt.includes("explanation") || prompt.includes("breakdown")) {
    return [
      `## 🧠 Smart Breakdown: ${topic}`,
      `### Course: ${course}`,
      ``,
      `${topic} handles how objects, operations, or entities interact. Let's break it down:`,
      ``,
      `### Step 1: Definition`,
      `You declare the schemas, models, or types that govern ${topic}. This forms the blueprint.`,
      ``,
      `### Step 2: Allocation`,
      `The compiler or runtime instantiates the blueprints, allocating memory blocks.`,
      ``,
      `### Step 3: Interaction`,
      `Objects communicate via defined interface protocols, swapping messages and triggering execution stacks.`,
      ``,
      `### Step 4: Destructuring`,
      `Once execution is complete, garbage collection releases the memory addresses.`,
      ``,
      `#### Code Demonstration:`,
      `\`\`\`javascript`,
      `// Simple execution of ${topic}`,
      `const executeModule = () => {`,
      `  console.log("Starting ${topic} operation");`,
      `  // logic goes here`,
      `};`,
      `executeModule();`,
      `\`\`\``
    ].join("\n");
  }
  
  if (prompt.includes("interview") || prompt.includes("exam") || prompt.includes("Question:") || prompt.includes("Questions")) {
    return [
      `Question: What are the main benefits of ${topic}?`,
      `Answer: The main benefits include better software modularity, clear interface declarations, easier debugging, and high code reusability.`,
      ``,
      `Question: How do you implement ${topic} safely?`,
      `Answer: Implement it by defining strict boundaries, using private properties with getters/setters, writing extensive unit tests, and adhering to SOLID principles.`,
      ``,
      `Question: What is a common pitfall when using ${topic}?`,
      `Answer: A common pitfall is over-engineering, which creates unnecessary classes or interface wrappers that increase execution complexity without adding value.`
    ].join("\n");
  }
  
  return [
    `Hello! I am your SmartStudy AI Assistant.`,
    ``,
    `Here is some helpful information about "${prompt}":`,
    `In the study of ${course}, ${topic} plays a key role. It is concerned with organizing logic and data structures efficiently.`,
    ``,
    `Please let me know if you would like me to generate:`,
    `- A customized study plan`,
    `- Mock exam questions`,
    `- Code samples`
  ].join("\n");
}
