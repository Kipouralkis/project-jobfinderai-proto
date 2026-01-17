import "./candidate.css"

export default function SmartSearchCard({ cv, setCv, onSmartSearch }) {
    return (
        <>
            <h2>Smart Job Search</h2>
            <div className="card smart-search-card section">
                <p>Upload your CV and let the assistant find matching roles for you.</p>

                <form className="smart-search-form" onSubmit={onSmartSearch}>
                    <input
                        type="file"
                        accept=".pdf,.doc,.docx"
                        onChange={e => setCv(e.target.files[0])}
                        required
                    />
                    <button type="submit">Find Jobs for Me</button>
                </form>
            </div>
        </>
    );
}
