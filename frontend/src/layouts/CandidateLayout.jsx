import CandidateNav from "../components/candidate/CandidateNav.jsx";
import "../index.css"

export default function CandidateLayout({ children }) {
    return (
        <div>
            <CandidateNav />
            <div>
                {children}
            </div>
        </div>
    );
}
