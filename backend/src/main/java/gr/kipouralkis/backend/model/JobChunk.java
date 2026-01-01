package gr.kipouralkis.backend.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class JobChunk {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private int chunkIndex;

    @Column(columnDefinition = "TEXT")
    private String chunkContent;

//    @Column(columnDefinition = "vector(1536)")
    @Column(columnDefinition = "TEXT")
    private String embedding;

    public UUID getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getChunkContent() {
        return chunkContent;
    }

    public void setChunkContent(String chunkContent) {
        this.chunkContent = chunkContent;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }
}
