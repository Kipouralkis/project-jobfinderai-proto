import { useState, useEffect } from "react";
import SearchBar from "../components/SearchBar";
import JobList from "../components/jobs/JobList.jsx";
import { searchJobs } from "../api/jobs";
import CandidateLayout from "../layouts/CandidateLayout";

export default function Home() {
    const [jobs, setJobs] = useState([]);

    async function handleSearch(query) {
        const results = await searchJobs(query);
        setJobs(results);
    }

    return (
        <>
            <h1>Job Search</h1>
            <SearchBar onSearch={handleSearch} />
            <JobList jobs={jobs} />
        </>
    );
}
