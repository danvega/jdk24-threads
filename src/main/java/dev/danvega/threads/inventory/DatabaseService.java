package dev.danvega.threads.inventory;

import org.springframework.stereotype.Service;

/**
 * Service that simulates database operations with artificial delays.
 */
@Service
public class DatabaseService {
    
    /**
     * Simulates persisting an inventory change to a database.
     * This method intentionally blocks to simulate a database operation.
     * 
     * @param productId the ID of the product being updated
     * @param quantity the quantity change (positive for additions, negative for removals)
     */
    public void persistInventoryChange(String productId, int quantity) {
        try {
            // Simulate a slow database operation
            // Only 10ms delay to make the benchmark run faster while still showing differences
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}