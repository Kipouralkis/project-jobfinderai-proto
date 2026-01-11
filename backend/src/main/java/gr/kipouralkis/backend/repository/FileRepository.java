package gr.kipouralkis.backend.repository;

import gr.kipouralkis.backend.model.Application;
import gr.kipouralkis.backend.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileRecord, UUID> {
}
