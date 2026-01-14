package gr.kipouralkis.backend.service;

import gr.kipouralkis.backend.model.Job;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SmartSearchService {

    private final JobSearchService jobSearchService;
    private final CvAiService cvAiService;

    public SmartSearchService(
            JobSearchService jobSearchService,
            CvAiService cvAiService) {
        this.jobSearchService = jobSearchService;
        this.cvAiService = cvAiService;
    }

    public List<Job> findMatchingJobs(MultipartFile file) {
        String cvText = extractText(file);
        String query = cvAiService.generateSearchQuery(cvText);
        return jobSearchService.searchJobs(query);
    }

    // Helper functions
    private String extractText(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CV file");
        }
    }

}
