package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.dto.JobCreateRequest;
import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.repository.JobRepository;
import gr.kipouralkis.backend.service.JobIndexService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final JobIndexService jobIndexService;

    public JobController(JobRepository jobRepository, JobIndexService jobIndexService) {
        this.jobRepository = jobRepository;
        this.jobIndexService = jobIndexService;
    }

    // list all jobs
    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // get single job
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable UUID id) {
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody JobCreateRequest request) {
        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setSeniority(request.getSeniority());
        job.setDescription(request.getDescription());
        Job savedJob = jobRepository.save(job);
        return ResponseEntity.ok(savedJob);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable UUID id, @RequestBody JobCreateRequest request) {
        return jobRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setCompany(request.getCompany());
                    existing.setLocation(request.getLocation());
                    existing.setSeniority(request.getSeniority());
                    existing.setDescription(request.getDescription());
                    Job savedJob = jobRepository.save(existing);
                    return ResponseEntity.ok(savedJob);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable UUID id) {
        if (!jobRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        jobRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /*
        INDEXING
    */

    @PostMapping("/{id}/index")
    public ResponseEntity<?> indexJob(@PathVariable UUID id) {
        System.out.println(">>> INDEX ENDPOINT HIT <<<");
        return jobRepository.findById(id)
                .map(job -> {
                    jobIndexService.indexJob(job);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
