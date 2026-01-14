import { API_BASE } from "./config";

export async function searchJobs(query) {
    const res = await fetch(
        `${API_BASE}/jobs/search?q=${encodeURIComponent(query)}`
    );
    return res.json();
}

export async function getJob(id) {
    const res = await fetch(
        `${API_BASE}/jobs/${id}`);
    return res.json();
}


export async function smartSearch(cvFile) {
    const formData = new FormData();
    formData.append("file", cvFile);

    const res = await fetch(`${API_BASE}/ai/smart-search`, {
        method: "POST",
        body: formData
    });

    return res.json();

}
