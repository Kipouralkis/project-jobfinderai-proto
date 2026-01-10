export async function searchJobs(query) {
    const res = await fetch(
        `http://localhost:8081/api/jobs/search?q=${encodeURIComponent(query)}`
    );
    return res.json();
}

export async function getJob(id) {
    const res = await fetch(`http://localhost:8081/api/jobs/${id}`);
    return res.json();
}
