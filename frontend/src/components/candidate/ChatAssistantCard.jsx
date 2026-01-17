import "./candidate.css"

export default function ChatAssistantCard() {
    return (
        <div className="card chat-assistant-card section">
            <h3>Need help?</h3>
            <p>Ask the assistant to help you find roles or improve your CV.</p>
            <a href="/candidate/chat">Open Chat Assistant →</a>
        </div>
    );
}
