package gr.kipouralkis.backend.service;

import org.springframework.stereotype.Service;

@Service
public class CvAiService {

    private final TextGenerationService textGenerationService;

    public CvAiService(TextGenerationService textGenerationService) {
        this.textGenerationService = textGenerationService;
    }

    public String generateSearchQuery(String cvText){

        String prompt = """
                You are an expert career assistant.
                Analyze the following CV text and extract the candidate's main skills, seniority level and likely job titles.
                and aspirations/goals if available.
                
                Then generate a short job-search query (5-10 words)
                that would find relevant jobs in a job board.
                
                CV %s
                
                Return ONLY the search query.
                """.formatted(cvText);

        return textGenerationService.generateText(prompt);
    }

}
