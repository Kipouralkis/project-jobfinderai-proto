import { useState } from "react";
import ChatCard from "../components/ChatCard.jsx";

export default function ChatPage() {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");

    async function sendMessage() {
        if (!input.trim()) return;

        const userMsg = { role: "user", content: input };
        setMessages(prev => [...prev, userMsg]);

        const res = await fetch("http://localhost:8081/chat", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message: input })
        });

        const data = await res.json();

        const assistantMsg = {
            role: "assistant",
            content: data
        };

        setMessages(prev => [...prev, assistantMsg]);
        setInput("");
    }

    return (
        <div style={styles.container}>
            <h2 style={styles.header}>Job Finder Assistant</h2>

            <div style={styles.chatWindow}>
                {messages.map((msg, i) => {
                    const isUser = msg.role === "user";

                    // 1) Job list → render cards
                    if (Array.isArray(msg.content)) {
                        return (
                            <div key={i} style={{ alignSelf: "flex-start", width: "100%" }}>
                                {msg.content.map(job => (
                                    <ChatCard key={job.id} job={job} />
                                ))}
                            </div>
                        );
                    }

                    // 2) Object with { answer } → render the answer text
                    if (typeof msg.content === "object" && msg.content !== null) {
                        return (
                            <div
                                key={i}
                                style={{
                                    ...styles.message,
                                    alignSelf: "flex-start",
                                    background: "#e5e5e5",
                                    color: "black"
                                }}
                            >
                                {msg.content.answer}
                            </div>
                        );
                    }

                    // 3) Normal text → render bubble
                    return (
                        <div
                            key={i}
                            style={{
                                ...styles.message,
                                alignSelf: isUser ? "flex-end" : "flex-start",
                                background: isUser ? "#4a6cf7" : "#e5e5e5",
                                color: isUser ? "white" : "black"
                            }}
                        >
                            {msg.content}
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
