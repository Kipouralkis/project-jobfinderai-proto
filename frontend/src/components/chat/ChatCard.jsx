import "./chat.css"

export default function ChatCard({ job }) {
    const preview =
        job.description.length > 160
            ? job.description.slice(0, 160) + "..."
            : job.description;

    return (
        <a href={`/jobs/${job.id}`} className="chat-card-link">
            <div className="chat-card">
                <h3 className="chat-card-title">{job.title}</h3>

                <p className="chat-card-meta">
                    {job.company} — {job.location}
                </p>

                <p className="chat-card-seniority">
                    {job.seniority?.toUpperCase()}
                </p>

                <p className="chat-card-preview">{preview}</p>
            </div>
        </a>
    );
}
