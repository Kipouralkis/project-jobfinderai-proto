package gr.kipouralkis.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.kipouralkis.backend.model.Job;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Service
public class RerankingService {

    private final TextGenerationService llm;
    private final ObjectMapper mapper =  new ObjectMapper();
    private final Executor rerankExecutor;

    public RerankingService(TextGenerationService llm, @Qualifier("rerankExecutor") Executor rerankExecutor) {
        this.llm = llm;
        this.rerankExecutor = rerankExecutor;
    }

    public List<Job> rerankJobs(String query, List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return jobs;
        }

        // 1. Build a text representation of all jobs with temporary IDs
        StringBuilder jobsBulletList = new StringBuilder();
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobsBulletList.append("""
                [ID: %d]
                Title: %s
                Company: %s
                Description: %s
                ---
                """.formatted(i, job.getTitle(), job.getCompany(), job.getDescription()));
        }

        // 2. Create the Batch Prompt
        String batchPrompt = """
            You are an expert technical recruiter. Analyze the following list of jobs against the User Query.
            
            USER QUERY: "%s"
            
            INSTRUCTIONS:
            1. Evaluate each job's relevance to the query (skills, seniority, field).
            2. Provide a brief reasoning for each match.
            3. Assign a relevance score between 0.0 and 1.0.
            
            Return ONLY a JSON object with a key "matches" which is an array of objects:
            {
              "matches": [
                { "id": 0, "reasoning": "...", "score": 0.95 },
                { "id": 1, "reasoning": "...", "score": 0.40 }
              ]
            }

            JOBS TO ANALYZE:
            %s
            """.formatted(query, jobsBulletList.toString());

        // 3. Single API Call (Quota Friendly!)
        String result = llm.generateText(batchPrompt).trim();

        // 4. Clean and Parse the JSON Array
        int firstBracket = result.indexOf("{");
        int lastBracket = result.lastIndexOf("}");
        if (firstBracket != -1 && lastBracket != -1) {
            result = result.substring(firstBracket, lastBracket + 1);
        }

        try {
            JsonNode root = mapper.readTree(result);
            JsonNode matches = root.path("matches");

            for (JsonNode matchNode : matches) {
                int id = matchNode.path("id").asInt(-1);
                double score = matchNode.path("score").asDouble(0.0);
                String reasoning = matchNode.path("reasoning").asText("No reasoning");

                if (id >= 0 && id < jobs.size()) {
                    Job job = jobs.get(id);
                    job.setRerankScore(score);

                    // Console log for your visibility
                    System.out.println("\n--- BATCH RERANK LOG ---");
                    System.out.println("ID: " + id + " | Job: " + job.getTitle());
                    System.out.println("Score: " + score);
                    System.out.println("Reasoning: " + reasoning);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse batch rerank JSON. Raw: " + result);
        }

        // 5. Sort the original list by the new scores
        return jobs.stream()
                .sorted(Comparator.comparingDouble(Job::getRerankScore).reversed())
                .toList();
    }
}
