package gr.kipouralkis.backend.dto;

import java.util.UUID;

public record JobChunkSearchResult(
        UUID jobId,
        String chunkContent,
        double distance
) {}
