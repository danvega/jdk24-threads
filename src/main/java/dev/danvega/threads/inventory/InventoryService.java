package dev.danvega.threads.inventory;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service that manages product inventory with synchronized methods
 * to demonstrate virtual thread behavior in JDK 24.
 */
@Service
public class InventoryService {
    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();
    private final DatabaseService dbService;

    public InventoryService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * Updates the inventory for a product. This method is synchronized to prevent
     * concurrent modifications to the same product, and contains a blocking operation
     * (database write).
     * 
     * In JDK 21, this would cause virtual thread pinning during the database operation.
     * In JDK 24, the virtual thread can unmount from its carrier thread during the
     * blocking operation, even while holding the monitor for this object.
     *
     * @param productId the ID of the product to update
     * @param quantity the quantity change (positive for additions, negative for removals)
     * @return true if the update was successful, false if it would result in negative inventory
     */
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

    /**
     * Gets the current inventory level for a product.
     *
     * @param productId the ID of the product
     * @return the current inventory level
     */
    public int getInventoryLevel(String productId) {
        return inventory.getOrDefault(productId, 0);
    }
}