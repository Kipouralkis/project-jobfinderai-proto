package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.dto.BulkJobCreateRequest;
import gr.kipouralkis.backend.dto.JobCreateRequest;
import gr.kipouralkis.backend.model.Job;
import gr.kipouralkis.backend.repository.JobRepository;
import gr.kipouralkis.backend.service.EmbeddingApiService;
import gr.kipouralkis.backend.service.JobIndexService;
import gr.kipouralkis.backend.service.JobSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final JobIndexService jobIndexService;
    private final JobSearchService jobSearchService;
    private final EmbeddingApiService embeddingApiService;

    public JobController(JobRepository jobRepository,
                         JobIndexService jobIndexService,
                         JobSearchService jobSearchService,
                         EmbeddingApiService embeddingApiService) {
        this.jobRepository = jobRepository;
        this.jobIndexService = jobIndexService;
        this.jobSearchService = jobSearchService;
        this.embeddingApiService = embeddingApiService;

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
        jobIndexService.indexJob(savedJob);
        return ResponseEntity.ok(savedJob);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Job>> createJobsBulk(@RequestBody BulkJobCreateRequest request) {

        List<Job> savedJobs = new ArrayList<>();

        for (JobCreateRequest j : request.getJobs()) {
            Job job = new Job();
            job.setTitle(j.getTitle());
            job.setCompany(j.getCompany());
            job.setLocation(j.getLocation());
            job.setSeniority(j.getSeniority());
            job.setDescription(j.getDescription());

            Job saved = jobRepository.save(job);
            savedJobs.add(saved);

            // Index each job
            jobIndexService.indexJob(saved);
        }

        return ResponseEntity.ok(savedJobs);
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

    @GetMapping("/search")
    public List<Job> search(@RequestParam String q) {
        return jobSearchService.searchJobs(q);
    }


    @PostMapping("/reindex")
    public ResponseEntity<String> reindexAllJobs() {
        System.out.println(">>> REINDEX START <<<");
        List<Job> jobs = jobRepository.findAll();

        int count = 0;
        for (Job job : jobs) {
            jobIndexService.indexJob(job);
            count++;
        }

        return ResponseEntity.ok("Reindexed " + count + " jobs");
    }

    @PostMapping("/debug/embed")
    public ResponseEntity<?> debugEmbeddings(@RequestBody Map<String, String> body) {

        String text1 = body.get("text1");
        String text2 = body.get("text2");

        if (text1 == null || text2 == null) {
            return ResponseEntity.badRequest().body("Provide text1 and text2");
        }


        float[] emb1 = embeddingApiService.embed(text1);
        float[] emb2 = embeddingApiService.embed(text2);

        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < emb1.length; i++) {
            dot += emb1[i] * emb2[i];
            norm1 += emb1[i] * emb1[i];
            norm2 += emb2[i] * emb2[i];
        }

        double similarity = dot / (Math.sqrt(norm1) * Math.sqrt(norm2));

        Map<String, Object> response = new HashMap<>();
        response.put("text1", text1);
        response.put("text2", text2);
        response.put("embedding1_length", emb1.length);
        response.put("embedding2_length", emb2.length);
        response.put("cosine_similarity", similarity);

        return ResponseEntity.ok(response);
    }



    /*
        INDEXING
    */

//    @PostMapping("/{id}/index")
//    public ResponseEntity<?> indexJob(@PathVariable UUID id) {
//        System.out.println(">>> INDEX ENDPOINT HIT <<<");
//        return jobRepository.findById(id)
//                .map(job -> {
//                    jobIndexService.indexJob(job);
//                    return ResponseEntity.ok().build();
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }




}
