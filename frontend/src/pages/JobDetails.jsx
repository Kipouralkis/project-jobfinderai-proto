import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getJob } from "../api/jobs";
import { useNavigate } from "react-router-dom";

export default function JobDetails() {
    const { id } = useParams();
    const [job, setJob] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        getJob(id).then(setJob);
    }, [id]);

    function handleApply() {
        navigate(`/candidate/jobs/${id}/apply`);
    }

    if (!job) return <p>Loading...</p>;

    return (
        <div style={{ padding: "24px" }}>
            <h1>{job.title}</h1>
            <p>{job.company} — {job.location}</p>
            <p><strong>Seniority:</strong> {job.seniority}</p>
            <p>{job.description}</p>

            <button
                onClick={handleApply}
                style={{ marginTop: "20px" }}>
                Apply to this job
            </button>
        </div>
    );
}
