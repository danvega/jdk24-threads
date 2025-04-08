# JDK 24 Virtual Threads Benchmark

This project demonstrates the performance improvements in JDK 24 for virtual threads when using per-object locks instead of a single shared lock object.

## About the Project

Spring Boot application with virtual threads enabled that benchmarks different synchronization approaches in concurrent applications:

- Uses Spring Boot's built-in support for virtual threads
- Implements a RESTful inventory management API with synchronized methods
- Includes a benchmarking tool to measure performance differences

## JDK 24 Virtual Threads Improvement

In JDK 21, virtual threads could experience "pinning" when using synchronized blocks, causing them to block their carrier thread and losing the main advantage of virtual threads. JDK 24 improves this situation by optimizing how synchronized blocks are handled, allowing virtual threads to be unmounted even when inside a synchronized block.

**Key improvement**: This optimization only works when using *different lock objects* for different virtual threads. If multiple virtual threads contend for the same lock object, they will still block each other (as expected from synchronized semantics).

## Implementation Details

The application uses a per-product synchronization approach:

```java
// In InventoryService.java
private final ConcurrentHashMap<String, Object> productLocks = new ConcurrentHashMap<>();

public boolean updateInventory(String productId, int quantity) {
    // Get or create a lock object specific to this productId
    Object productLock = productLocks.computeIfAbsent(productId, k -> new Object());
    
    // Synchronize on the product-specific lock
    synchronized (productLock) {
        // Each product has its own lock, allowing operations on different products
        // to proceed concurrently in JDK 24 without pinning virtual threads
        dbService.persistInventoryChange(productId, quantity);
        // ...
    }
}
```

## Project Structure

- `Application.java`: Main Spring Boot application with virtual threads enabled
- `InventoryController`: REST endpoints for inventory updates
- `InventoryService`: Service using per-product locks for synchronized updates
- `DatabaseService`: Simulates slow database operations with Thread.sleep(100ms)
- `BenchmarkController`: Endpoint to run performance benchmarks

## Running the Benchmark

1. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

2. Run the benchmark:
   ```bash
   curl http://localhost:8080/benchmark
   ```

## Comparing JDK 21 vs JDK 24

This project uses SDKMan for Java version management. The `.sdkmanrc` file is configured with the Java version for the project.

1. First, run the benchmark using JDK 21:
   - Update the .sdkmanrc file to use JDK 21:
     ```
     java=21-oracle
     ```
   - Apply the change with: `sdk env`
   - Verify with: `java -version`
   - Run the application: `./mvnw spring-boot:run`
   - Access the benchmark endpoint and note the results (typically around 1300 req/sec)

2. Then, run the same benchmark using JDK 24:
   - Update the .sdkmanrc file to use JDK 24:
     ```
     java=24-oracle
     ```
   - Apply the change with: `sdk env`
   - Verify with: `java -version`
   - Run the application: `./mvnw spring-boot:run`
   - Access the benchmark endpoint again and note the significantly improved performance (typically around 6500 req/sec)

# Benchmark Results 

```bash
❯ http :8080/benchmark
HTTP/1.1 200
Connection: keep-alive
Content-Length: 729
Content-Type: text/plain;charset=UTF-8
Date: Tue, 08 Apr 2025 17:20:16 GMT
Keep-Alive: timeout=60

=== VIRTUAL THREADS PERFORMANCE BENCHMARK ===
JDK Version: 21.0.2
===========================================

--- STARTING BENCHMARK: 10000 concurrent requests across 1000 different product IDs ---

╔════════════ BENCHMARK RESULTS ════════════╗
║ JDK Version:           21.0.2             ║
║ Total Requests:        10000              ║
║ Unique Product IDs:    1000               ║
║ Total Time:            12.486 seconds     ║
║ Avg Time Per Request:  1.249 ms           ║
║ Requests Per Second:   800.9              ║
╚═══════════════════════════════════════════╝

```

```bash
❯ http :8080/benchmark
HTTP/1.1 200
Connection: keep-alive
Content-Length: 725
Content-Type: text/plain;charset=UTF-8
Date: Tue, 08 Apr 2025 17:21:40 GMT
Keep-Alive: timeout=60

=== VIRTUAL THREADS PERFORMANCE BENCHMARK ===
JDK Version: 24
===========================================

--- STARTING BENCHMARK: 10000 concurrent requests across 1000 different product IDs ---

╔════════════ BENCHMARK RESULTS ════════════╗
║ JDK Version:           24                 ║
║ Total Requests:        10000              ║
║ Unique Product IDs:    1000               ║
║ Total Time:            2.345 seconds      ║
║ Avg Time Per Request:  0.235 ms           ║
║ Requests Per Second:   4264.4             ║
╚═══════════════════════════════════════════╝

```