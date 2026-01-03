package gr.kipouralkis.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
//import io.hypersistence.utils.hibernate.type
//import io.hypersistence.utils.hibernate.type.
//import com.vladmihalcea.hibernate.type.array.

import java.util.UUID;

/**
 * Represents a single chunk of a Job description along with its vector embedding.
 * <p>
 * JobChunks are created internally by the JobIndexService and are used to support
 * semantic search. They are not exposed through any public API.
 */
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

    @JdbcTypeCode(SqlTypes.OTHER)
    @Column(columnDefinition = "vector(1024)")
    private float[] embedding;


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

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}
