import CandidateNav from "../components/CandidateNav";

export default function CandidateLayout({ children }) {
    return (
        <div>
            <CandidateNav />
            <div style={{ padding: "24px" }}>
                {children}
            </div>
        </div>
    );
}
