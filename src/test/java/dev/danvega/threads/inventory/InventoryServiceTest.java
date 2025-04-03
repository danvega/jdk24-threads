package dev.danvega.threads.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the InventoryService class.
 * These tests verify that the inventory service behaves correctly
 * with synchronized methods.
 */
@SpringBootTest
class InventoryServiceTest {

    /**
     * Tests that the updateInventory method correctly updates inventory
     * and prevents negative inventory.
     */
    @Test
    void testUpdateInventory() {
        DatabaseService dbService = new DatabaseService();
        InventoryService service = new InventoryService(dbService);
        
        String productId = "test-product";
        
        // Add 10 items to inventory
        boolean result1 = service.updateInventory(productId, 10);
        assertTrue(result1, "Should successfully add items to inventory");
        assertEquals(10, service.getInventoryLevel(productId), "Inventory level should be 10");
        
        // Remove 5 items from inventory
        boolean result2 = service.updateInventory(productId, -5);
        assertTrue(result2, "Should successfully remove items from inventory");
        assertEquals(5, service.getInventoryLevel(productId), "Inventory level should be 5");
        
        // Try to remove more items than available (should fail)
        boolean result3 = service.updateInventory(productId, -10);
        assertFalse(result3, "Should not allow negative inventory");
        assertEquals(5, service.getInventoryLevel(productId), "Inventory level should remain at 5");
    }

    /**
     * Tests that concurrent updates to different products work correctly.
     * This test demonstrates that synchronized methods don't block updates
     * to different products.
     */
    @Test
    void testConcurrentUpdates() throws Exception {
        DatabaseService dbService = new DatabaseService();
        InventoryService service = new InventoryService(dbService);
        
        String product1 = "product-1";
        String product2 = "product-2";
        
        // Create two threads updating different products
        Thread thread1 = Thread.ofVirtual().start(() -> {
            service.updateInventory(product1, 10);
        });
        
        Thread thread2 = Thread.ofVirtual().start(() -> {
            service.updateInventory(product2, 20);
        });
        
        // Wait for both threads to complete
        thread1.join();
        thread2.join();
        
        // Verify both products were updated correctly
        assertEquals(10, service.getInventoryLevel(product1), "Product 1 should have 10 items");
        assertEquals(20, service.getInventoryLevel(product2), "Product 2 should have 20 items");
    }
}