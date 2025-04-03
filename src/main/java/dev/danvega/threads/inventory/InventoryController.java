package dev.danvega.threads.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for inventory operations.
 * In a Spring Boot application with virtual threads enabled, each request
 * will be handled by a virtual thread, demonstrating the JDK 24 improvements
 * for synchronized blocks.
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Updates the inventory for a product.
     * This endpoint demonstrates how virtual threads in JDK 24 can handle
     * synchronized methods without pinning.
     *
     * @param productId the ID of the product to update
     * @param request the update request containing the quantity change
     * @return a response indicating success or failure
     */
    @PostMapping("/{productId}")
    public ResponseEntity<String> updateInventory(
            @PathVariable String productId,
            @RequestBody InventoryUpdateRequest request) {

        // This runs on a virtual thread in Spring Boot 3.2+ with virtual threads enabled
        boolean success = inventoryService.updateInventory(productId, request.getQuantityChange());

        if (success) {
            return ResponseEntity.ok("Inventory updated");
        } else {
            return ResponseEntity.badRequest().body("Invalid inventory update");
        }
    }

    /**
     * Gets the current inventory level for a product.
     *
     * @param productId the ID of the product
     * @return the current inventory level
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getInventoryLevel(@PathVariable String productId) {
        int level = inventoryService.getInventoryLevel(productId);
        return ResponseEntity.ok(level);
    }
}