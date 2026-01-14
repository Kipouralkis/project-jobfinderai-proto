package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.service.SmartSearchService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class SmartSearchController {

    private final SmartSearchService smartSearchService;

    public SmartSearchController(SmartSearchService smartSearchService) {
        this.smartSearchService = smartSearchService;
    }

    @PostMapping("/smart-search")
    public List<Job> smartSearch(@RequestParam("file") MultipartFile file) {
        return smartSearchService.findMatchingJobs(file);
    }
}
