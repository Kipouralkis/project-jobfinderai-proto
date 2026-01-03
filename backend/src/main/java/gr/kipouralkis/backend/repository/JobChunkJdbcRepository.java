package gr.kipouralkis.backend.repository;

import gr.kipouralkis.backend.converter.FloatArrayToVectorConverter;
import gr.kipouralkis.backend.dto.JobChunkSearchResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JobChunkJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FloatArrayToVectorConverter converter = new FloatArrayToVectorConverter();

    public JobChunkJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertJobChunk(
            UUID id,
            UUID jobId,
            int chunkIndex,
            String chunkContent,
            float[] embedding
    ){
        String sql = """
                INSERT INTO job_chunk (id, job_id, chunk_index, chunk_content, embedding)
                VALUES (?, ?, ?, ?, ?::vector)
                """;

        jdbcTemplate.update(sql, id, jobId, chunkIndex, chunkContent, converter.convertToDatabaseColumn(embedding));
    }

    private String vectorLiteral(float[] v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(v[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    public List<JobChunkSearchResult> searchChunks(float[] queryEmbedding, int topK) {

        String sql = """
        SELECT job_id, chunk_content,
               embedding <=> ?::vector AS distance
        FROM job_chunk
        ORDER BY distance
        LIMIT ?
        """;

        return jdbcTemplate.query(
                sql,
                new Object[]{ vectorLiteral(queryEmbedding), topK },
                (rs, rowNum) -> new JobChunkSearchResult(
                        UUID.fromString(rs.getString("job_id")),
                        rs.getString("chunk_content"),
                        rs.getDouble("distance")
                )
        );
    }

}
