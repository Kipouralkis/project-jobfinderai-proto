import { API_BASE } from "./config";

export async function uploadFile(file) {
    const form = new FormData();
    form.append("file", file);

    const res = await fetch(`${API_BASE}/files/upload`, {
        method: "POST",
        body: form
    });

    return res.json();
}
