package gr.kipouralkis.backend.repository;

import gr.kipouralkis.backend.model.JobChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobChunkRepository extends JpaRepository<JobChunk, UUID> {


}
