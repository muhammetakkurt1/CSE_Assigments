public class GeneralDevice implements Device{
    private String category;
    private String name;
    private double price;
    private int quantity;
    /**
     * Constructs a new GeneralDevice with specified properties. Initializes
     * the device with the provided category, name, price, and quantity ensuring
     * that price and quantity are not negative.
     * Time Complexity: O(1)
     *
     * @param category The category of the device.
     * @param name The name of the device.
     * @param price The price of the device. Negative values are adjusted to 0.
     * @param quantity The quantity of the device in stock. Negative values are adjusted to 0.
     */
    public GeneralDevice(String category, String name, double price, int quantity) {
        this.category = category;
        this.name = name;
        this.price = Math.max(price, 0);
        this.quantity = Math.max(quantity, 0);
    }

    /**
     * Gets the category of this device.
     * Time Complexity: O(1)
     *
     * @return The category of the device.
     */
    @Override
    public String getCategory() {
        return category;
    }
    /**
     * Gets the name of this device.
     * Time Complexity: O(1)
     *
     * @return The name of the device.
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * Gets the price of this device.
     * Time Complexity: O(1)
     *
     * @return The price of the device.
     */
    @Override
    public double getPrice() {
        return price;
    }
    /**
     * Gets the quantity of this device in stock.
     * Time Complexity: O(1)
     *
     * @return The current stock quantity of the device.
     */
    @Override
    public int getQuantity() {
        return quantity;
    }
    /**
     * Sets the name of this device to the specified name.
     * Time Complexity: O(1)
     *
     * @param name The new name for this device.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Sets the price of this device to the specified value. If a negative value
     * is provided, the price is set to 0.
     * Time Complexity: O(1)
     *
     * @param price The new price for this device. Negative values are adjusted to 0.
     */
    @Override
    public void setPrice(double price) {
        this.price = Math.max(price, 0);
    }
    /**
     * Sets the quantity of this device in stock to the specified value. If a
     * negative value is provided, the quantity is set to 0.
     * Time Complexity: O(1)
     *
     * @param quantity The new stock quantity for this device. Negative values are adjusted to 0.
     */
    @Override
    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 0);
    }
}
