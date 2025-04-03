package dev.danvega.threads.inventory;

/**
 * Request model for inventory updates.
 */
public class InventoryUpdateRequest {
    private int quantityChange;

    // Default constructor for JSON deserialization
    public InventoryUpdateRequest() {
    }

    public InventoryUpdateRequest(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    /**
     * Gets the quantity change requested.
     * Positive values indicate additions to inventory, negative values indicate removals.
     *
     * @return the quantity change
     */
    public int getQuantityChange() {
        return quantityChange;
    }

    /**
     * Sets the quantity change.
     *
     * @param quantityChange the quantity change to set
     */
    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }
}