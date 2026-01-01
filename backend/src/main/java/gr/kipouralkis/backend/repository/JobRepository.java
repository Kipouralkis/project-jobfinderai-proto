package gr.kipouralkis.backend.repository;

import gr.kipouralkis.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

}
