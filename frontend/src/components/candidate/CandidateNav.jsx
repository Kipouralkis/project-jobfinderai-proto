import "./candidate.css"

export default function CandidateNav() {
    const path = window.location.pathname;

    return (
        <nav className="candidate-nav">
            <a href="/candidate/" className={`candidate-nav-link ${path === "/candidate/" ? "active" : ""}`}>Home</a>
            <a href="/candidate/jobs" className={`candidate-nav-link ${path === "/candidate/jobs" ? "active" : ""}`}>Jobs</a>
            <a href="/candidate/applications" className={`candidate-nav-link ${path === "/candidate/applications" ? "active" : ""}`}>My Applications</a>
            <a href="/candidate/chat" className={`candidate-nav-link ${path === "/candidate/chat" ? "active" : ""}`}>Chat Assistant</a>
        </nav>
    );
}
