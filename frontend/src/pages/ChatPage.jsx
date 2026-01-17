import { useState } from "react";
import {CHAT_BASE} from "../api/config.js";
import "../components/chat/chat.css";
import ChatMessage from "../components/chat/ChatMessage.jsx";
import ChatInput from "../components/chat/ChatInput.jsx";

export default function ChatPage() {
    const [messages, setMessages] = useState([
        {role: "assistant",
        content: "Hello! I'm your AI Job Assistant. I can help you search for roles, summarize details, and submit applications. What are you looking for today?"}
    ]);

    const [input, setInput] = useState("");

    async function sendMessage() {
        if (!input.trim()) return;

        const userMsg = { role: "user", content: input };
        const historyPayload = [...messages, userMsg];

        setMessages(prev => [...prev, userMsg]);
        const currentInput = input;
        setInput("");

        try {
            const res = await fetch(`${CHAT_BASE}`, {
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
        <div className="chat-container">
            <h2 className="chat-header">Job Finder Assistant</h2>

            <div className="chat-window">
                {messages.map((msg, i) => (
                    <ChatMessage key={i} msg={msg} />
                    ))}
            </div>

            <ChatInput input={input} setInput={setInput} onSend={sendMessage} />
        </div>

    );
}