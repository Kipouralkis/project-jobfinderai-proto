import { useEffect, useState } from "react";
import CandidateLayout from "../layouts/CandidateLayout";
import SearchBar from "../components/SearchBar";
import JobList from "../components/JobList";
// import { searchJobs, fetchRecentJobs } from "../api/jobs";
import { fetchMyApplications } from "../api/applications";
import {searchJobs} from "../api/jobs.js";
import { smartSearch } from "../api/jobs";

export default function CandidateHome() {
    // const { user } = useUser();
    const [jobs, setJobs] = useState([]);
    const [recentJobs, setRecentJobs] = useState([]);
    const [applications, setApplications] = useState([]);
    const [cv, setCv] = useState(null);

    async function handleSearch(query) {
        const results = await searchJobs(query);
        setJobs(results);
    }

    async function handleSmartSearchClick(e) {
        e.preventDefault();
        if (!cv) return;

        const jobs = await smartSearch(cv);
        setJobs(jobs);
    }


    return (
        <>
            <h1 style={{ marginBottom: "16px" }}>Find Your Next Opportunity</h1>

            <SearchBar onSearch={handleSearch} />

            {/* Quick Filters */}
            <div style={{ marginBottom: "24px" }}>
                <button onClick={() => handleSearch("remote")} style={{ marginRight: "8px" }}>
                    Remote
                </button>
                <button onClick={() => handleSearch("junior")} style={{ marginRight: "8px" }}>
                    Entry Level
                </button>
                <button onClick={() => handleSearch("full time")}>
                    Full‑time
                </button>
            </div>

            {/* Smart Job Search */}
            <div className="card smart-search-card">
                <h3>Smart Job Search</h3>
                <p>Upload your CV and let the assistant find matching roles for you.</p>

                <form className="smart-search-form" onSubmit={handleSmartSearchClick}>
                    <input
                        type="file"
                        accept=".pdf,.doc,.docx"
                        onChange={e => setCv(e.target.files[0])}
                        required
                    />
                    <button type="submit" >Find Jobs for Me</button>
                </form>
            </div>


            {/* Search Results */}
            {jobs.length > 0 && (
                <>
                    <h2 style={{ marginBottom: "12px" }}>Search Results</h2>
                    <JobList jobs={jobs} />
                </>
            )}

            {/* Recent Jobs */}
            {jobs.length === 0 && (
                <>
                    <h2 style={{ marginBottom: "12px" }}>Recent Jobs</h2>
                    <JobList jobs={recentJobs} />
                </>
            )}

            {/* Applications Summary */}
            <div className="card" style={{ marginTop: "32px" }}>
                <h3>Your Applications</h3>
                <p>You have applied to <strong>{applications.length}</strong> jobs.</p>
                <a href="/candidate/applications">View applications →</a>
            </div>

            {/* Chat Assistant */}
            <div className="card" style={{ marginTop: "16px" }}>
                <h3>Need help?</h3>
                <p>Ask the assistant to help you find roles or improve your CV.</p>
                <a href="/candidate/chat">Open Chat Assistant →</a>
            </div>
        </>
    );
}
