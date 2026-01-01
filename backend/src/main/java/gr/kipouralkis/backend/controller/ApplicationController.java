package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.dto.ApplicationCreateRequest;
import gr.kipouralkis.backend.model.Application;
import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.model.User;
import gr.kipouralkis.backend.repository.ApplicationRepository;
import gr.kipouralkis.backend.repository.JobRepository;
import gr.kipouralkis.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationController(ApplicationRepository applicationRepository,
                                 UserRepository userRepository,
                                 JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable UUID id) {
        return applicationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Application> createApplication(@RequestBody ApplicationCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setFilePath(request.getFilePath());
        application.setMotivationText(request.getMotivationText());

        return ResponseEntity.ok(applicationRepository.save(application));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(@PathVariable UUID id, @RequestBody ApplicationCreateRequest request) {
        return applicationRepository.findById(id)
                .map(existing -> {

                    User user = userRepository.findById(request.getUserId())
                                    .orElseThrow(() -> new RuntimeException("User not found"));

                    Job job = jobRepository.findById(request.getJobId())
                                    .orElseThrow(() -> new RuntimeException("Job not found"));

                    existing.setUser(user);
                    existing.setJob(job);
                    existing.setFilePath(request.getFilePath());
                    existing.setMotivationText(request.getMotivationText());

                    return ResponseEntity.ok(applicationRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable UUID id) {
        if (!applicationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        applicationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
