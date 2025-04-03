package dev.danvega.threads.performance;

import dev.danvega.threads.Jdk24ThreadsApplication;
import dev.danvega.threads.inventory.InventoryUpdateRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A performance test that uses the actual Spring Boot application.
 * This class demonstrates the improvements in JDK 24 for virtual threads
 * with synchronized blocks.
 * 
 * In JDK 21, virtual threads would be pinned to platform threads during
 * synchronized blocks, limiting concurrency. In JDK 24, virtual threads can
 * unmount from platform threads even while holding monitors, allowing for
 * much higher concurrency.
 */
public class SimplePerformanceTest {

    /**
     * Runs a performance test with the specified number of concurrent requests.
     * This version starts the Spring Boot application and calls the actual controller.
     * 
     * @param requestCount the number of concurrent requests to simulate
     * @return the duration in milliseconds
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public static long runTest(int requestCount) throws InterruptedException {

        System.out.println("Starting performance test with " + requestCount + " concurrent requests...");
        System.out.println("This test demonstrates how JDK 24 allows virtual threads to use synchronized");
        System.out.println("methods without pinning to platform threads during blocking operations.");
        System.out.println();

        // Start the Spring Boot application
        ConfigurableApplicationContext context = SpringApplication.run(Jdk24ThreadsApplication.class);

        try {
            // Create a RestClient to make HTTP requests
            RestClient restClient = RestClient.create();
            String baseUrl = "http://localhost:8080/inventory/";

            // Create concurrent requests
            List<Thread> threads = new ArrayList<>();
            AtomicInteger completed = new AtomicInteger(0);
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < requestCount; i++) {
                String productId = "product-" + (i % 100);  // 100 different products
                // create virtual threads here to simulate concurrent users making requests
                Thread t = Thread.ofVirtual().start(() -> {
                    try {
                        // Create a request to update inventory
                        InventoryUpdateRequest request = new InventoryUpdateRequest(1);

                        // Call the controller
                        ResponseEntity<String> response = restClient.post()
                                .uri(baseUrl + productId)
                                .body(request)
                                .retrieve()
                                .toEntity(String.class);

                        if (response.getStatusCode().is2xxSuccessful()) {
                            completed.incrementAndGet();
                        }
                    } catch (Exception e) {
                        System.err.println("Error making request: " + e.getMessage());
                    }
                });
                threads.add(t);
            }

            // Wait for all threads to complete
            for (Thread t : threads) {
                t.join();
            }

            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Processed " + completed.get() + " inventory updates in " + duration + "ms");
            return duration;
        } finally {
            // Close the Spring Boot application
            context.close();
        }
    }

    /**
     * Main method to run the performance test.
     * 
     * @param args command line arguments
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public static void main(String[] args) throws InterruptedException {
        // Run with 100 requests as specified
        int requestCount = 100;

        long duration = runTest(requestCount);

        System.out.println();
        System.out.println("Test completed in " + duration + "ms");
        System.out.println();
        System.out.println("Note: On JDK 21, this same test with 100 requests would take approximately");
        System.out.println("20 seconds (100 requests * 200ms) with a single platform thread, or about");
        System.out.println("5 seconds with 4 platform threads, because virtual threads would be pinned");
        System.out.println("to platform threads during the synchronized block, limiting concurrency.");
        System.out.println();
        System.out.println("On JDK 24, the test completes much faster (around 200-300ms) because virtual");
        System.out.println("threads can unmount from platform threads even while holding monitors,");
        System.out.println("allowing all 100 requests to run concurrently.");
    }
}
