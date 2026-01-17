import "./chat.css";

export default function ChatInput({ input, setInput, onSend }) {
    return (
        <div className="chat-input-row">
            <input
                className="chat-input"
                value={input}
                onChange={e => setInput(e.target.value)}
                placeholder="Ask me about jobs…"
                onKeyDown={e => e.key === "Enter" && onSend()}
            />
            <button className="chat-button" onClick={onSend}>
                Send
            </button>
        </div>
    );
}
