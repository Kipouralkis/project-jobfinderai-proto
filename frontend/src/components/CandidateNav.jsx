export default function CandidateNav() {
    return (
        <nav style={{
            padding: "12px 24px",
            borderBottom: "1px solid #ddd",
            marginBottom: "24px"
        }}>
            <a href="/candidate/" style={{ marginRight: "16px" }}>Home</a>
            <a href="/candidate/jobs" style={{ marginRight: "16px" }}>Jobs</a>
            <a href="/candidate/applications" style={{ marginRight: "16px" }}>My Applications</a>
            <a href="/candidate/chat">Chat Assistant</a>
        </nav>
    );
}
