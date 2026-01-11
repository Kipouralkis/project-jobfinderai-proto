package gr.kipouralkis.backend.controller;

import gr.kipouralkis.backend.model.FileRecord;
import gr.kipouralkis.backend.repository.FileRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileRepository fileRepository;

    public FileController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping("/upload")
    public FileRecord upload(@RequestParam("file") MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path path = Paths.get("uploads/", fileName);

        Files.copy(file.getInputStream(), path);

        FileRecord rec = new  FileRecord();
        rec.setPath(path.toString());
        return fileRepository.save(rec);
    }

}
