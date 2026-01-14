import { API_BASE } from "./config";

export async function submitApplication(payload) {
    const res = await fetch(`${API_BASE}/applications`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });
    return res.json();
}

export async function fetchMyApplications(userId) {
    const res = await fetch(`${API_BASE}/applications/user/${userId}`);
    return res.json();
}
