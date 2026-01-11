package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.model.Intent;
import org.springframework.stereotype.Service;

@Service
public class IntentService {

    private final TextGenerationService llm;

    public IntentService(TextGenerationService llm) {
        this.llm = llm;
    }

    public Intent classify(String message){

        String prompt = """
                Classify the user's intent into one of:
                -search_jobs
                -cv_recommendations
                -apply_to_job
                -general_chat
                
                Respond ONLY with the label.
                
                User massage %s
                """.formatted(message);

        String result = llm.generateText(prompt).trim().toLowerCase();

        return switch (result) {
            case "search_jobs" -> Intent.SEARCH_JOBS;
            case "cv_recommendations" -> Intent.CV_RECOMMENDATIONS;
            case "apply_to_job" -> Intent.APPLY_TO_JOB;
            default -> Intent.GENERAL_CHAT;
        };

    }
}
