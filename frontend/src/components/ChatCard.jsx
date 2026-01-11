export default function ChatCard({ job }) {

    const preview =
        job.description.length > 160
            ? job.description.slice(0, 160) + "..."
            : job.description;

    return (
        <a
            href={`/jobs/${job.id}`}
            style={{
                textDecoration: "none",
                color: "inherit"
            }}
        >
            <div
                style={{
                    border: "1px solid #ddd",
                    borderRadius: 10,
                    padding: 16,
                    background: "white",
                    cursor: "pointer",
                    boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                    transition: "0.2s",
                    marginBottom: 12
                }}
            >
                <h3 style={{ margin: 0, color: "#555" }}>{job.title}</h3>

                <p style={{ margin: "4px 0", color: "#555" }}>
                    {job.company} — {job.location}
                </p>

                <p style={{ margin: "4px 0", fontSize: 14, color: "#777" }}>
                    {job.seniority?.toUpperCase()}
                </p>

                <p style={{ marginTop: 10, fontSize: 14, color: "#444" }}>
                    {preview}
                </p>
            </div>
        </a>
    );
}
