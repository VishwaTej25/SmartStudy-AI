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
    const data = await response.json();
    return data.choices?.[0]?.message?.content || "No response";
  } catch (error: any) {
    console.error("Groq API Error:", error);
    return `ERROR: ${error.message}`;
  }
}
