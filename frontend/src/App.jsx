import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import SearchResults from "./pages/SearchResults";
import JobDetails from "./pages/JobDetails";
import ChatPage from "./pages/ChatPage.jsx";

import {useState} from "react";
import CandidateLayout from "./layouts/CandidateLayout.jsx";
import Landing from "./pages/Landing.jsx";
import Login from "./pages/Login.jsx";
import ApplyPage from "./pages/ApplyPage.jsx";
import CandidateHome from "./pages/CandidateHome.jsx";

export default function App() {
    const [user, setUser] = useState({role: "candidate"});

    function CandidateRoute({user}) {
        if (!user || user.role !=="candidate") {
            return <div>Access Denied</div>
        }

        return (
            <CandidateLayout>
                <Routes>
                    <Route index element={<CandidateHome />} />
                    {/*<Route path="jobs" element={<Home />} />*/}
                    <Route path="jobs/:id" element={<JobDetails />} />
                    <Route path="jobs/:id/apply" element={<ApplyPage />} />
                    {/*<Route path="applications" element={<MyApplications />} />*/}
                    <Route path="chat" element={<ChatPage />} />
                </Routes>
            </CandidateLayout>
        );
    }

    function AdminRoute({ user }) {
        if (!user || user.role !== "admin") {
            return <div>Access denied</div>;
        }

        return (
            <AdminLayout>
                <Routes>
                    <Route path="jobs" element={<AdminJobList />} />
                    <Route path="jobs/upload" element={<AdminUploadJobs />} />
                    <Route path="jobs/:id/applicants" element={<AdminApplicants />} />
                </Routes>
            </AdminLayout>
        );
    }


    return (
        <BrowserRouter>
            <Routes>
                {/*Public*/}
                {/*<Route path="/" element={<Landing />} />*/}
                <Route path="/login" element={<Login />} />

                {/*Candidate*/}
                <Route path="/candidate/*"
                       element={<CandidateRoute user={user} /> }
                />

                {/*Admin*/}
                <Route path="/candidate/*"
                       element={<AdminRoute user={user} /> }
                />

                <Route path="/" element={<Home />} />
                <Route path="/search" element={<SearchResults />} />
                <Route path="/jobs/:id" element={<JobDetails />} />
                <Route path="/chat" element={<ChatPage />} />
            </Routes>
        </BrowserRouter>
    );
}
