package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.agent.ToolCall;
import gr.kipouralkis.backend.model.Application;
import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.model.User;
import gr.kipouralkis.backend.repository.ApplicationRepository;
import gr.kipouralkis.backend.repository.JobRepository;
import gr.kipouralkis.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ToolService {
    private final JobSearchService jobSearchService;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ToolService(
            JobSearchService jobSearchService,
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.jobSearchService = jobSearchService;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public String execute(ToolCall call) {
        System.out.println("Executing Tool: " + call.name() + " with args: " + call.arguments());

        return switch (call.name()) {
            case "semantic_search" -> {
                String query = (String) call.arguments().get("query");
                yield jobSearchService.searchJobs(query).toString();
            }
            case "job_apply" -> handleApply(call);
            default -> "Error: Unknown tool " + call.name();
        };
    }

    private String handleApply(ToolCall call) {
        try {
            // 1. Parse the UUIDs
            UUID jobId = UUID.fromString(call.arguments().get("job_id").toString());
            String motivation = (String) call.arguments().get("motivation_text");

            // 2. Prototype logic: Find the first user or a specific test user
            User user = userRepository.findAll().stream().findFirst()
                    .orElseGet(() -> {
                        User testUser = new User();
                        testUser.setFirstName("Test");
                        testUser.setLastName("User");
                        testUser.setEmail("test@example.com");
                        testUser.setPassword("password123");
                        return userRepository.save(testUser); // Create one on the fly if missing
                    });
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            // 3. Create the real Entity
            Application application = new Application();
            application.setUser(user);
            application.setJob(job);
            application.setMotivationText(motivation);
            application.setFilePath("uploads/resumes/default_cv.pdf"); // Prototype placeholder

            applicationRepository.save(application);

            return "Success! You have officially applied for the " + job.getTitle() +
                    " position at " + job.getCompany() + ".";
        } catch (Exception e) {
            return "Failed to apply: " + e.getMessage();
        }
    }
}
