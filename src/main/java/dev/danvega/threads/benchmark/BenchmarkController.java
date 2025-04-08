package dev.danvega.threads.benchmark;

import dev.danvega.threads.inventory.InventoryUpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Simple controller with a GET endpoint to run the benchmark
 */
@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {

    private final RestClient restClient;

    public BenchmarkController(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    @GetMapping
    public String runBenchmark() {
        int requestCount = 10_000;
        int productIdCount = 1000;
        
        StringBuilder result = new StringBuilder();
        result.append("\n=== VIRTUAL THREADS PERFORMANCE BENCHMARK ===\n");
        result.append("JDK Version: ").append(System.getProperty("java.version")).append("\n");
        result.append("===========================================\n\n");
        
        result.append("--- STARTING BENCHMARK: ")
              .append(requestCount)
              .append(" concurrent requests across ")
              .append(productIdCount)
              .append(" different product IDs ---\n");
        
        try {
            // Create an executor with virtual threads
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                // Record start time
                Instant start = Instant.now();
                
                // Create and submit all tasks
                CompletableFuture<?>[] futures = IntStream.range(0, requestCount)
                    .mapToObj(i -> {
                        // Calculate product ID (distribute across different IDs)
                        String productId = String.valueOf(1 + (i % productIdCount));
                        
                        return CompletableFuture.runAsync(() -> {
                            try {
                                // Send request to update inventory using RestClient
                                restClient.post()
                                    .uri("/inventory/{productId}", productId)
                                    .body(new InventoryUpdateRequest(1))
                                    .retrieve()
                                    .toEntity(String.class);
                            } catch (Exception e) {
                                // Ignore exceptions
                            }
                        }, executor);
                    })
                    .toArray(CompletableFuture[]::new);
                
                // Wait for all requests to complete
                CompletableFuture.allOf(futures).get();
                
                // Record end time and calculate duration
                Instant end = Instant.now();
                Duration duration = Duration.between(start, end);
                
                // Calculate statistics
                double totalTimeSeconds = duration.toMillis() / 1000.0;
                double requestsPerSecond = requestCount / totalTimeSeconds;
                double avgTimePerRequest = (duration.toMillis() / (double) requestCount);
                
                // Format results for display
                result.append("\n╔════════════ BENCHMARK RESULTS ════════════╗\n");
                result.append("║ JDK Version:           ").append(String.format("%-18s", System.getProperty("java.version"))).append("║\n");
                result.append("║ Total Requests:        ").append(String.format("%-18s", requestCount)).append("║\n");
                result.append("║ Unique Product IDs:    ").append(String.format("%-18s", productIdCount)).append("║\n");
                result.append("║ Total Time:            ").append(String.format("%-18s", String.format("%.3f seconds", totalTimeSeconds))).append("║\n");
                result.append("║ Avg Time Per Request:  ").append(String.format("%-18s", String.format("%.3f ms", avgTimePerRequest))).append("║\n");
                result.append("║ Requests Per Second:   ").append(String.format("%-18s", String.format("%.1f", requestsPerSecond))).append("║\n");
                result.append("╚═══════════════════════════════════════════╝\n");
                
                return result.toString();
            }
        } catch (Exception e) {
            result.append("Benchmark failed: ").append(e.getMessage());
            e.printStackTrace();
            return result.toString();
        }
    }
}