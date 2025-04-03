# JDK 24 Virtual Threads with Synchronized Blocks Demo

This project demonstrates one of the most significant improvements in JDK 24: virtual threads can now use synchronized methods and blocks without pinning to platform threads during blocking operations.

## What's the Big Deal?

In JDK 21, when a virtual thread entered a synchronized block and then performed a blocking operation (like I/O), it would get "pinned" to its carrier platform thread. This pinning prevented the platform thread from being released back to the thread pool, effectively negating one of the main benefits of virtual threads.

JDK 24 solves this problem by allowing virtual threads to acquire, hold, and release monitors independently of their carriers. This means virtual threads can unmount from their carrier platform threads when they block inside a synchronized block, allowing those platform threads to serve other virtual threads.

## Project Overview

This Spring Boot application demonstrates the improvement through a simple inventory management system:

- **Inventory Service**: Contains a synchronized method that performs a blocking database operation
- **REST Controller**: Exposes endpoints that use the inventory service
- **Performance Test**: Demonstrates the scalability improvement in JDK 24

## Key Components

### Inventory Management Example

- `InventoryService`: Uses a synchronized method with a blocking database operation
- `DatabaseService`: Simulates a slow database with artificial delays
- `InventoryController`: REST endpoints that handle inventory operations

### Synchronization Examples

- `MethodSynchronizedCounter`: Demonstrates synchronized methods
- `BlockSynchronizedCounter`: Demonstrates synchronized blocks

### Performance Test

`SimplePerformanceTest`: Runs concurrent requests to demonstrate the performance difference:
- On JDK 21: ~20 seconds for 100 requests with a single platform thread
- On JDK 24: ~200-300ms for 100 requests (all running concurrently)

## How to Run

1. Ensure you have JDK 24 installed
2. Clone this repository
3. Run the application:
   ```
   ./mvnw spring-boot:run
   ```
4. Run the performance test:
   ```
   ./mvnw exec:java -Dexec.mainClass="dev.danvega.threads.performance.SimplePerformanceTest"
   ```

## The Restaurant Analogy

Think of it like a restaurant:

**Pre-JDK 24**: When a customer (virtual thread) enters a private dining room (synchronized block) and needs to wait for food (blocking I/O), the waiter (platform thread) must stay with that customer the entire time, unable to serve others.

**JDK 24**: When a customer enters a private dining room and needs to wait for food, the waiter can leave a pager with the customer and attend to other customers. When the food is ready, any available waiter can bring it to the customer.

## Conclusion

JDK 24's improvement to virtual threads represents a significant advancement in Java's concurrency model. By allowing synchronized methods and blocks to work efficiently with virtual threads, Java has eliminated a major obstacle to adopting virtual threads in existing applications.

This means that many Java applications can now benefit from the scalability of virtual threads without having to rewrite all their synchronization code, making Java a more compelling platform for high-throughput, concurrent applications.