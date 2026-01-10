import { useState } from "react";

export default function SearchBar({ onSearch }) {
    const [query, setQuery] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(query);
    };

    return (
        <form onSubmit={handleSubmit} style={{ display: "flex", gap: "8px" }}>
            <input
                type="text"
                placeholder="Search for jobs..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                style={{ flex: 1, padding: "8px" }}
            />
            <button type="submit">Search</button>
        </form>
    );
}
