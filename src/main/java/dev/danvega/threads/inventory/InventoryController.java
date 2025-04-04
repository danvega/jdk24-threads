package dev.danvega.threads.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Random; // Import Random

/**
 * REST controller for inventory operations.
 * Includes endpoint for randomized updates for benchmarking.
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final DatabaseService dbService; // Keep if needed, though not directly used here
    private final Random random = new Random(); // Instance for generating random numbers
    private static final int MAX_PRODUCT_ID = 3; // Example: Use product IDs 1 through 10

    public InventoryController(InventoryService inventoryService, DatabaseService dbService) {
        this.inventoryService = inventoryService;
        this.dbService = dbService;
    }

    // Your existing endpoints...
    @PostMapping("/{productId}")
    public ResponseEntity<String> updateInventory(
            @PathVariable String productId,
            @RequestBody InventoryUpdateRequest request) {
        // Existing logic
        boolean success = inventoryService.updateInventory(productId, request.getQuantityChange());
        if (success) {
            return ResponseEntity.ok("Inventory updated");
        } else {
            return ResponseEntity.badRequest().body("Invalid inventory update");
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getInventoryLevel(@PathVariable String productId) {
        // Existing logic
        int level = inventoryService.getInventoryLevel(productId);
        return ResponseEntity.ok(level);
    }

    // *** NEW ENDPOINT FOR RANDOMIZED BENCHMARKING ***
    @PostMapping("/random-update")
    public ResponseEntity<String> updateRandomInventory(
            @RequestBody InventoryUpdateRequest request) { // Still accept the payload

        // Generate a random product ID (e.g., between 1 and MAX_PRODUCT_ID inclusive)
        int randomIdInt = random.nextInt(MAX_PRODUCT_ID) + 1;
        String randomProductId = String.valueOf(randomIdInt);

        // Call the existing service logic with the random ID
        boolean success = inventoryService.updateInventory(randomProductId, request.getQuantityChange());

        if (success) {
            // You might return the ID updated for clarity, or just "ok"
            return ResponseEntity.ok("Inventory updated for random product: " + randomProductId);
        } else {
            // Less likely to happen with random positive updates, but handle nonetheless
            return ResponseEntity.badRequest().body("Invalid inventory update for random product: " + randomProductId);
        }
    }

}