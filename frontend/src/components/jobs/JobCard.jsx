import { Link } from "react-router-dom";
import "./jobs.css";

export default function JobCard({ job }) {
    return (
        <div className="job-card">
            <h3 className="job-card-title">
                <Link to={`/jobs/${job.id}`} className="job-card-link">
                    {job.title}
                </Link>
            </h3>

            <p className="job-card-meta">
                {job.company} — {job.location}
            </p>

            <p className="job-card-description">
                {job.description.slice(0, 150)}...
            </p>

            <small className="job-card-score">
                Score: {job.rerankScore?.toFixed(3)}
            </small>
        </div>
    );
}
