package gr.kipouralkis.backend.dto;

import java.util.List;

public class BulkJobCreateRequest {
    private List<JobCreateRequest> jobs;

    public List<JobCreateRequest> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobCreateRequest> jobs) {
        this.jobs = jobs;
    }
}
