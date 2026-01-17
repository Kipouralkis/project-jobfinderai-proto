import ReactMarkdown from "react-markdown";
import ChatCard from "./ChatCard";
import "./chat.css";

export default function ChatMessage({ msg }) {
    const isUser = msg.role === "user";

    return (
        <div className={`chat-message ${isUser ? "user" : "assistant"}`}>
            {msg.content && (
                <div className={`chat-bubble ${isUser ? "user" : "assistant"}`}>
                    <ReactMarkdown>{msg.content}</ReactMarkdown>
                </div>
            )}

            {msg.jobs?.length > 0 && (
                <div className="job-grid">
                    {msg.jobs.map(job => (
                        <ChatCard key={job.id} job={job} />
                    ))}
                </div>
            )}
        </div>
    );
}
