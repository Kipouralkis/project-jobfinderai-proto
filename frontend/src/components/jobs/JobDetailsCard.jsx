import "./jobs.css";

export default function JobDetailsCard({ job, onApply }) {
    return (
        <div className="job-details-container">
            <h1 className="job-title">{job.title}</h1>

            <p className="job-meta">
                {job.company} — {job.location}
            </p>

            <p className="job-seniority">
                <strong>Seniority:</strong> {job.seniority}
            </p>

            <p className="job-description">{job.description}</p>

            <button className="apply-button" onClick={onApply}>
                Apply to this job
            </button>
        </div>
    );
}
