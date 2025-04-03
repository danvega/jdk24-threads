# JDK 24 Virtual Threads with Synchronized Blocks Demo

## Unlocking the Full Power of Virtual Threads

Java's virtual threads revolutionized concurrent programming, but they faced a critical limitation in earlier versions—until now. This project showcases one of JDK 24's most substantial improvements: **virtual threads can now use synchronized methods and blocks without pinning to platform threads during blocking operations**.

What does this mean for your applications? Dramatically improved scalability with minimal code changes.

## The Evolution of Virtual Threads

### The Previous Limitation

In JDK 21, whenever a virtual thread entered a synchronized block and performed a blocking operation (like I/O or network calls), it would become "pinned" to its carrier platform thread. This pinning prevented the platform thread from serving other virtual threads, effectively neutralizing a key benefit of the virtual thread model.

### The JDK 24 Breakthrough

JDK 24 resolves this constraint by allowing virtual threads to acquire, hold, and release monitors independently of their carriers. Now, virtual threads can unmount from carrier platform threads even when blocked inside synchronized sections, freeing those platform threads to handle other tasks.

## Project Architecture

This Spring Boot application demonstrates the improvement through a simple yet practical inventory management system:

### Core Components

- **Inventory Service**: Contains synchronized methods that perform blocking database operations
- **REST Controller**: Exposes endpoints to interact with the inventory service
- **Performance Test**: Demonstrates the dramatic scalability improvements in JDK 24

### Virtual Threads Configuration

The application enables virtual threads through a simple property in `application.properties`:

```properties
spring.threads.virtual.enabled=true
```

This configuration tells Spring Boot to use virtual threads for each incoming HTTP request, allowing you to handle thousands of concurrent connections with minimal resources.

## Project Requirements

- JDK 24
- Maven 3.6+
- Spring Boot 3.4.4

## Dependencies

The project uses minimal dependencies:

- `spring-boot-starter-web`: Provides web server and REST capabilities
- `spring-boot-starter-test`: Testing framework

## Getting Started

### How to Run the Application

Build and run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

This starts a web server on port 8080 with the `/inventory` endpoints available.

### Running the Performance Test

To experience the dramatic difference in throughput:

```bash
./mvnw exec:java -Dexec.mainClass="dev.danvega.threads.performance.SimplePerformanceTest"
```

## Key Code Examples

### Synchronized Method with Blocking Operation

The heart of the demonstration is in the `InventoryService` class, where a synchronized method performs a blocking database operation:

```java
public synchronized boolean updateInventory(String productId, int quantity) {
    // Check current inventory
    int currentStock = inventory.getOrDefault(productId, 0);

    if (currentStock + quantity < 0) {
        return false; // Can't have negative inventory
    }

    // This simulates a slow database write that blocks
    dbService.persistInventoryChange(productId, quantity);

    // Update in-memory inventory
    inventory.put(productId, currentStock + quantity);
    return true;
}
```

With JDK 24, this method can now be called concurrently by many virtual threads without causing the underlying platform threads to be pinned during the database operation.

### Performance Test Results

The `SimplePerformanceTest` class demonstrates the dramatic performance difference:

```java
// Create concurrent requests
List<Thread> threads = new ArrayList<>();
// Create virtual threads for each request
for (int i = 0; i < requestCount; i++) {
    String productId = "product-" + (i % 100);
    Thread t = Thread.ofVirtual().start(() -> {
        // Make REST call to inventory service
        // ...
    });
    threads.add(t);
}
```

Results comparison:
- **JDK 21**: ~20 seconds for 100 requests with a single platform thread
- **JDK 24**: ~200-300ms for all 100 requests running concurrently

## Understanding the Improvement: The Restaurant Analogy

Think of it like a busy restaurant:

**Pre-JDK 24**: When a customer (virtual thread) enters a private dining room (synchronized block) and waits for food (blocking I/O), the waiter (platform thread) must stay with that customer the entire time, unable to serve others.

**JDK 24**: When a customer enters a private dining room and waits for food, the waiter can leave a pager with the customer and attend to other customers. When the food is ready, any available waiter can bring it to the customer.


## Conclusion

JDK 24's enhancement to virtual threads represents a major advancement in Java's concurrency capabilities. By allowing synchronized methods and blocks to work efficiently with virtual threads, Java has eliminated a significant barrier to adopting virtual threads in existing applications.

This means your Java applications can now benefit from the exceptional scalability of virtual threads without having to rewrite synchronization code, making Java an even more compelling platform for high-throughput, concurrent applications.

Give it a try and experience the difference yourself!
