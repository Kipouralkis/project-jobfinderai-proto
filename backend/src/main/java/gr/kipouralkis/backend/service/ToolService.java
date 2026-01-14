package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.agent.ToolCall;
import org.springframework.stereotype.Service;


@Service
public class ToolService {
    private final JobSearchService jobSearchService;

    public ToolService(JobSearchService jobSearchService) {
        this.jobSearchService = jobSearchService;
    }

    public String execute(ToolCall call) {
        // Log what the agent is doing
        System.out.println("Executing Tool: " + call.name() + " with args: " + call.arguments());

        return switch (call.name()) {
            case "semantic_search" -> {
                String query = (String) call.arguments().get("query");
                // Return result as a String (JSON) so the LLM can read it
                yield jobSearchService.searchJobs(query).toString();
            }
            case "job_apply" -> {
                String jobId = call.arguments().get("job_id").toString();
                yield "Successfully applied to job ID: " + jobId;
            }
            default -> "Error: Unknown tool " + call.name();
        };
    }
}
