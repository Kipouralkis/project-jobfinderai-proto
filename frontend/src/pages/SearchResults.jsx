import { useState } from "react";
import { useSearchParams } from "react-router-dom";
import SearchBar from "../components/SearchBar";
import JobList from "../components/jobs/JobList.jsx";
import { searchJobs } from "../api/jobs";

export default function SearchResults() {
    const [params] = useSearchParams();
    const initialQuery = params.get("q") || "";
    const [jobs, setJobs] = useState([]);

    async function handleSearch(query) {
        const results = await searchJobs(query);
        setJobs(results);
    }

    return (
        <div style={{ padding: "24px" }}>
            <h1>Search Results</h1>
            <SearchBar onSearch={handleSearch} />
            <JobList jobs={jobs} />
        </div>
    );
}
