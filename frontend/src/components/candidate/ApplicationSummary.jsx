import "./candidate.css";

export default function ApplicationsSummary({ applications }) {
    return (
        <div className="card applications-card section">
            <h3>Your Applications</h3>
            <p>You have applied to <strong>{applications.length}</strong> jobs.</p>

            <button
                className="btn-secondary"
                onClick={() => (window.location.href = "/candidate/applications")}
            >
                View applications →
            </button>
        </div>
    );
}
