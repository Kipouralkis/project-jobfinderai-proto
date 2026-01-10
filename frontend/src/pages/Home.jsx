import { useState, useEffect } from "react";
import SearchBar from "../components/SearchBar";
import JobList from "../components/JobList";
import { searchJobs } from "../api/jobs";

export default function Home() {
    const [jobs, setJobs] = useState([]);

    async function handleSearch(query) {
        const results = await searchJobs(query);
        setJobs(results);
    }

    return (
        <div style={{ padding: "24px" }}>
            <h1>Job Search</h1>
            <SearchBar onSearch={handleSearch} />
            <JobList jobs={jobs} />
        </div>
    );
}
