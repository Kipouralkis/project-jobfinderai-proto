import { useState } from "react";
import SearchBar from "../components/SearchBar";
import JobList from "../components/jobs/JobList.jsx";
import SearchFilters from "../components/candidate/SearchFilters";
import SmartSearchCard from "../components/candidate/SmartSearchCard";
import ApplicationSummary from "../components/candidate/ApplicationSummary";
import ChatAssistantCard from "../components/candidate/ChatAssistantCard";
import { searchJobs, smartSearch } from "../api/jobs";
import "../components/candidate/candidate.css";
import AssistantCTA from "../components/chat/AssistantCTA.jsx";

export default function CandidateHome() {
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

        const results = await smartSearch(cv);
        setJobs(results);
    }

    return (
        <div className="page-container">
            <AssistantCTA />

            <h1 className="page-title">Find Your Next Opportunity</h1>

            <div className="section">
                <SearchBar onSearch={handleSearch} />
                <SearchFilters onSearch={handleSearch} />
            </div>

            <SmartSearchCard
                cv={cv}
                setCv={setCv}
                onSmartSearch={handleSmartSearchClick}
            />

            {jobs.length > 0 ? (
                <>
                    <h2>Search Results</h2>
                    <JobList jobs={jobs} />
                </>
            ) : (
                <>
                    <h2>Recent Jobs</h2>
                    <JobList jobs={recentJobs} />
                </>
            )}

            <ApplicationSummary applications={applications} />

            <ChatAssistantCard />

        </div>
    );
}
