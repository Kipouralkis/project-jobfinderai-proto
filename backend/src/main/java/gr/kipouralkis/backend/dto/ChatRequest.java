package gr.kipouralkis.backend.dto;

public class ChatRequest {
    private String cvText;
    private String message;

    public String getCvText() {
        return cvText;
    }

    public void setCvText(String cvText) {
        this.cvText = cvText;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
