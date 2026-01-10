import { Link } from "react-router-dom";

export default function JobCard({ job }) {
    return (
        <div style={{ border: "1px solid #ddd", padding: "16px", margin: "12px 0" }}>
            <h3>
                <Link to={`/jobs/${job.id}`}>{job.title}</Link>
            </h3>
            <p>{job.company} — {job.location}</p>
            <p>{job.description.slice(0, 150)}...</p>
            <small>Score: {job.rerankScore?.toFixed(3)}</small>
        </div>
    );
}
