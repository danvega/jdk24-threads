package dev.danvega.threads.inventory;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // Import ConcurrentHashMap

/**
 * Service that manages product inventory with synchronized methods.
 * Modified to use per-product ID locks.
 */
@Service
public class InventoryService {

    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();
    // Use a ConcurrentHashMap to store locks for each product ID
    private final ConcurrentHashMap<String, Object> productLocks = new ConcurrentHashMap<>();
    private final DatabaseService dbService;

    public InventoryService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    /**
     * Updates the inventory for a product. This method uses synchronized blocks
     * on a per-product basis to prevent concurrent modifications to the same product,
     * while allowing concurrent updates to different products.
     *
     * @param productId the ID of the product to update
     * @param quantity the quantity change (positive for additions, negative for removals)
     * @return true if the update was successful, false if it would result in negative inventory
     */
    public boolean updateInventory(String productId, int quantity) {

        // Get or create a lock object specific to this productId
        // computeIfAbsent is thread-safe and ensures only one lock object is created per ID
        Object productLock = productLocks.computeIfAbsent(productId, k -> new Object());

        // Synchronize on the product-specific lock, NOT 'this'
        synchronized (productLock) {
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
    }

    /**
     * Gets the current inventory level for a product.
     * (No change needed here as getOrDefault is generally thread-safe for reads)
     *
     * @param productId the ID of the product
     * @return the current inventory level
     */
    public int getInventoryLevel(String productId) {
        return inventory.getOrDefault(productId, 0);
    }
}