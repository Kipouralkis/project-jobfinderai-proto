package gr.kipouralkis.backend.repository;

import gr.kipouralkis.backend.converter.FloatArrayToVectorConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
