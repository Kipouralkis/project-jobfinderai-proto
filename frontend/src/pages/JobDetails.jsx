import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getJob } from "../api/jobs";
import JobDetailsCard from "../components/jobs/JobDetailsCard";

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

    return <JobDetailsCard job={job} onApply={handleApply} />;
}
