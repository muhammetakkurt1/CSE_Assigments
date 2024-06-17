public interface Device {
    /**
     * Gets the category of the device.
     * Time complexity: O(1)
     */
    String getCategory();

    /**
     * Gets the name of the device.
     * Time complexity: O(1)
     */
    String getName();

    /**
     * Gets the price of the device.
     * Time complexity: O(1)
     */
    double getPrice();

    /**
     * Gets the quantity of the device available in inventory.
     * Time complexity: O(1)
     */
    int getQuantity();

    /**
     * Sets the name of the device.
     * Time complexity: O(1)
     */
    void setName(String name);

    /**
     * Sets the price of the device.
     * Time complexity: O(1)
     */
    void setPrice(double price);

    /**
     * Sets the quantity of the device available in inventory.
     * Time complexity: O(1)
     */
    void setQuantity(int quantity);
}