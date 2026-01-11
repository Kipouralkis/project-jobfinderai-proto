package gr.kipouralkis.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "rerankExecutor")
    public Executor rerankExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // How many threads to keep alive at all times
        executor.setCorePoolSize(1);
        // Maximum number of parallel LLM calls allowed
        executor.setMaxPoolSize(1);
        // How many jobs can wait in line if all threads are busy
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Rerank-Thread-");
        executor.initialize();
        return executor;
    }
}