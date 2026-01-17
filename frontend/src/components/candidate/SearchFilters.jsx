import "./candidate.css"

export default function SearchFilters({ onSearch }) {
    return (
        <div className="filters">
            <button onClick={() => onSearch("remote")}>Remote</button>
            <button onClick={() => onSearch("junior")}>Entry Level</button>
            <button onClick={() => onSearch("full time")}>Full‑time</button>
        </div>
    );
}
