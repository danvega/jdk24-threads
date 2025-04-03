package dev.danvega.threads.inventory;

import org.springframework.stereotype.Service;

/**
 * Service that simulates database operations with artificial delays
 * to demonstrate the behavior of virtual threads with synchronized blocks.
 */
@Service
public class DatabaseService {
    
    /**
     * Simulates persisting an inventory change to a database.
     * This method intentionally blocks to demonstrate virtual thread behavior.
     * 
     * @param productId the ID of the product being updated
     * @param quantity the quantity change (positive for additions, negative for removals)
     */
    public void persistInventoryChange(String productId, int quantity) {
        try {
            // Simulate a slow database operation
            Thread.sleep(200);  // 200ms per operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}