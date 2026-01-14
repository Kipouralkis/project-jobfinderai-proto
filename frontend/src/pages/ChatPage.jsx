import { useState } from "react";
import ChatCard from "../components/ChatCard.jsx";

export default function ChatPage() {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");

    async function sendMessage() {
        if (!input.trim()) return;

        const userMsg = { role: "user", content: input };

        // 1. Prepare the full history to send to the AI
        // We include the current messages PLUS the one the user just typed
        const historyPayload = [...messages, userMsg];

        // 2. Update UI immediately for a responsive feel
        setMessages(prev => [...prev, userMsg]);
        const currentInput = input;
        setInput("");

        try {
            const res = await fetch("http://localhost:8081/chat", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    message: currentInput,
                    history: historyPayload // THIS IS THE KEY
                })
            });

            const data = await res.json();

            // 3. Add the AI's response (Text + Job Cards) to the UI
            const assistantMsg = {
                role: "assistant",
                content: data.answer,
                jobs: data.data || [],
            };

            setMessages(prev => [...prev, assistantMsg]);
        } catch (error) {
            console.error("Failed to connect to backend:", error);
        }
    }

    return (
        <div style={styles.container}>
            <h2 style={styles.header}>Job Finder Assistant</h2>

            <div style={styles.chatWindow}>
                {messages.map((msg, i) => {
                    const isUser = msg.role === "user";

                    return (
                        <div key={i} style={{
                            display: "flex",
                            flexDirection: "column",
                            alignItems: isUser ? "flex-end" : "flex-start",
                            marginBottom: "20px",
                            width: "100%"
                        }}>
                            {/* AI Text Bubble: Adds the "flavor" and personality */}
                            {msg.content && (
                                <div style={{
                                    ...styles.message,
                                    background: isUser ? "#4a6cf7" : "#f0f2f5",
                                    color: isUser ? "white" : "#333",
                                    borderRadius: "15px",
                                    padding: "12px 16px",
                                    maxWidth: "80%",
                                    lineHeight: "1.5",
                                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)"
                                }}>
                                    {msg.content}
                                </div>
                            )}

                            {/* Structured Job Cards: The "functional" part of the response */}
                            {msg.jobs && msg.jobs.length > 0 && (
                                <div style={{
                                    display: "grid",
                                    gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
                                    gap: "12px",
                                    width: "100%",
                                    marginTop: "12px",
                                    paddingLeft: isUser ? "0" : "10px"
                                }}>
                                    {msg.jobs.map(job => (
                                        <ChatCard key={job.id} job={job} />
                                    ))}
                                </div>
                            )}
                        </div>
                    );
                })}

            </div>

            <div style={styles.inputRow}>
                <input
                    style={styles.input}
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    placeholder="Ask me about jobs…"
                />
                <button style={styles.button} onClick={sendMessage}>
                    Send
                </button>
            </div>
        </div>
    );
}

const styles = {
    container: {
        padding: 30,
        display: "flex",
        flexDirection: "column",
        height: "100vh"
    },
    header: {
        marginBottom: 20
    },
    chatWindow: {
        flex: 1,
        overflowY: "auto",
        display: "flex",
        flexDirection: "column",
        gap: 10,
        padding: 10,
        border: "1px solid #ddd",
        borderRadius: 8,
        background: "#fafafa"
    },
    message: {
        maxWidth: "70%",
        padding: "10px 14px",
        borderRadius: 12,
        whiteSpace: "pre-wrap"
    },
    inputRow: {
        display: "flex",
        gap: 10,
        marginTop: 20
    },
    input: {
        flex: 1,
        padding: 12,
        borderRadius: 8,
        border: "1px solid #ccc"
    },
    button: {
        padding: "12px 20px",
        borderRadius: 8,
        background: "#4a6cf7",
        color: "white",
        border: "none"
    }
};
