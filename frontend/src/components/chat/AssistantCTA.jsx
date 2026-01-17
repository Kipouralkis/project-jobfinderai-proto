import concave from "../../assets/concave.svg"

export default function AssistantCTA() {
    return (
        <>
            <div className="assistant-concave">
                <img src={concave} className="assistant-concave-svg" />
            </div>

            <button
                className="assistant-cta-button"
                onClick={() => window.location.href="/candidate/chat"}
            >
                Ask AI ✨
            </button>
        </>
    )
}
