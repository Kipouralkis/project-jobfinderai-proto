import { useState } from "react";
import { useParams } from "react-router-dom";
import CandidateLayout from "../layouts/CandidateLayout";
import { uploadFile } from "../api/files";
import { submitApplication } from "../api/applications";

export default function ApplyPage() {
    const { id } = useParams();

    const [file, setFile] = useState(null);
    const [motivation, setMotivation] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [success, setSuccess] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        if (!file) return;

        setSubmitting(true);

        const uploaded = await uploadFile(file);

        await submitApplication({
            userId: user.id,
            jobId: id,
            filePath: uploaded.path,
            motivationText: motivation
        });

        setSubmitting(false);
        setSuccess(true);
    }

    return (
        <>
            <div className="apply-container">
                <h1 className="apply-title">Apply for this Job</h1>

                {success ? (
                    <div className="card apply-success">
                        <h3>Application Submitted</h3>
                        <p>Your application has been successfully sent.</p>
                        <a href="/candidate/applications">View your applications →</a>
                    </div>
                ) : (
                    <form className="apply-form" onSubmit={handleSubmit}>
                        <label className="apply-label">Upload your CV</label>
                        <input
                            type="file"
                            className="apply-input"
                            onChange={e => setFile(e.target.files[0])}
                            required
                        />

                        <label className="apply-label">Motivation Letter</label>
                        <textarea
                            className="apply-textarea"
                            placeholder="Tell us why you're a great fit…"
                            value={motivation}
                            onChange={e => setMotivation(e.target.value)}
                        />

                        <button className="apply-button" disabled={submitting}>
                            {submitting ? "Submitting…" : "Submit Application"}
                        </button>
                    </form>
                )}
            </div>
        </>
    );
}
