import JobCard from "./JobCard.jsx";
import "./jobs.css";

export default function JobList({ jobs }) {
    return (
        <div className="job-list">
            {jobs.map(job => (
                <JobCard key={job.id} job={job} />
            ))}
        </div>
    );
}
