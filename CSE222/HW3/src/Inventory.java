import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Inventory {
    /**
     * LinkedList contains a structure where each element holds a list of devices belonging to a category.
     */
    private LinkedList<CategoryDevices> categories;

    /**
     * Constructs an empty inventory.
     *  Time complexity: O(1)
     */
    public Inventory() {
        this.categories = new LinkedList<>();
    }

    /**
     * Adds a device to the inventory under a specified category. If the category does not exist, it is created.
     * Time Complexity: O(n+m) where n is the number of categories and m is the maximum number of devices in a category.
     *
     * @param category The category under which to add the device.
     * @param device The device to be added.
     */
    public boolean addDevice(String category, Device device) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty.");
        }
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null.");
        }
        for (CategoryDevices categoryDevices : categories) {
            if (categoryDevices.getCategory().equals(category)) {
                for (Device existingDevice : categoryDevices.getDevices()) {
                    if (existingDevice.getName().equals(device.getName())) {

                        return false;
                    }
                }
                categoryDevices.getDevices().add(device);
                return true;
            }
        }

        CategoryDevices newCategoryDevices = new CategoryDevices(category);
        newCategoryDevices.getDevices().add(device);
        categories.add(newCategoryDevices);
        return true;
    }

    /**
     * Lists all devices in the inventory, categorized. If a category contains no devices, it notes that specifically.
     * Time Complexity: O(n*m) where n is the number of categories and m is the maximum number of devices in a category.
     */
    public void listAllDevices() {
        if (categories.isEmpty()) {
            System.out.println("There are no devices in the inventory.");
            return;
        }

        int count = 1;
        for (CategoryDevices categoryDevices : categories) {
            if (!categoryDevices.getDevices().isEmpty()) {
                System.out.println(count + ". Category: " + categoryDevices.getCategory());
                count++;
                for (Device device : categoryDevices.getDevices()) {
                    System.out.println("  " + "Name: " + device.getName() +
                            ", Price: $" + device.getPrice() +
                            ", Quantity: " + device.getQuantity());
                }
            } else {
                System.out.println(count + ". Category: " + categoryDevices.getCategory() + " has no devices.");
                count++;
            }
        }
    }
    /**
     * Updates the details of a specific device within a given category.
     * Time Complexity: O(n+2m) where n is the number of categories and m is the maximum number of devices in a category.
     *
     * @param category The category of the device to update.
     * @param oldName The name of the device to update.
     * @param newName The new name of the device to be updated.
     * @param price The new price of the device.
     * @param quantity The new quantity of the device in stock.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDeviceDetails(String category, String oldName, String newName, double price, int quantity) {
        boolean isUpdated = false;
        for (CategoryDevices categoryDevices : categories) {
            if (categoryDevices.getCategory().equals(category)) {
                if (!newName.isEmpty() && !oldName.equals(newName)) {
                    for (Device device : categoryDevices.getDevices()) {
                        if (device.getName().equals(newName)) {
                            System.out.println("A device with the name " + newName + " already exists in the " + category + " category.");
                            return false;
                        }
                    }
                }

                for (Device device : categoryDevices.getDevices()) {
                    if (device.getName().equals(oldName)) {
                        if (!newName.isEmpty() && !oldName.equals(newName)) {
                            device.setName(newName);
                            isUpdated = true;
                        }
                        if (price >= 0) {
                            device.setPrice(price);
                            isUpdated = true;
                        }
                        if (quantity >= 0) {
                            device.setQuantity(quantity);
                            isUpdated = true;
                        }
                        break;
                    }
                }
                break;
            }
        }
        return isUpdated;
    }



    /**
     * Removes a device from the inventory based on its category and name.
     * Time Complexity: O(n+m) where n is the number of categories and m is the maximum number of devices in a category.
     *
     * @param category The category of the device to be removed.
     * @param name The name of the device to be removed.
     * @return true if the device was successfully removed, false otherwise.
     */
    public boolean removeDevice(String category, String name) {
        for (CategoryDevices categoryDevices : categories) {
            if (categoryDevices.getCategory().equals(category)) {
                boolean isRemoved = categoryDevices.getDevices().removeIf(device -> device.getName().equals(name));
                return isRemoved;
            }
        }
        return false;
    }
    /**
     * Finds and returns the device with the lowest price in the inventory.
     * Time Complexity: O(n) where n is the total number of devices across all categories.
     *
     * @return The device with the lowest price or null if the inventory is empty.
     */
    public Device findCheapestDevice() {
        Device cheapestDevice = null;
        double cheapestPrice = Double.MAX_VALUE;


        for (CategoryDevices categoryDevices : categories) {

            for (Device device : categoryDevices.getDevices()) {

                if (device.getPrice() < cheapestPrice) {
                    cheapestDevice = device;
                    cheapestPrice = device.getPrice();
                }
            }
        }

        return cheapestDevice;
    }

    /**
     * Sorts all devices in the inventory by price and lists them.
     * Time Complexity: O(n log n) where n is the total number of devices.
     */
    public void sortAndListDevicesByPrice() {
        List<Device> allDevices = new ArrayList<>();
        for (CategoryDevices categoryDevices : categories) {
            allDevices.addAll(categoryDevices.getDevices());
        }

        if (allDevices.isEmpty()) {
            System.out.println("There are no devices in the inventory to sort.");
            return;
        }

        allDevices.sort(Comparator.comparing(Device::getPrice));

        System.out.println("Devices sorted by price:");
        int count = 1;
        for (Device device : allDevices) {
            System.out.println(count++ + ". " +
                    "Category: " + device.getCategory() + ", " +
                    "Name: " + device.getName() + ", " +
                    "Price: $" + String.format("%.2f", device.getPrice()) + ", " +
                    "Quantity: " + device.getQuantity());
        }
    }
    /**
     * Calculates the total value of the inventory by summing the price*quantity of all devices.
     * Time Complexity: O(n) where n is the total number of devices across all categories.
     *
     * @return The total value of the inventory.
     */
    public double calculateTotalInventoryValue() {
        double totalValue = 0;
        for (CategoryDevices categoryDevices : categories) {
            for (Device device : categoryDevices.getDevices()) {
                totalValue += device.getPrice() * device.getQuantity();
            }
        }

        return totalValue;
    }

    /**
     * Adjusts the stock quantity of a specific device, either adding or removing stock.
     * Time Complexity: O(n+m) where n is the number of categories and m is the maximum number of devices in a category.
     *
     * @param category The category of the device to restock.
     * @param name The name of the device.
     * @param quantity The quantity to adjust by.
     * @param add Whether to add (true) or remove (false) stock.
     * @return The new quantity in stock or -1 if the device could not be found.
     */
    public int restockDevice(String category, String name, int quantity, boolean add) {
        for (CategoryDevices categoryDevices : categories) {
            if (categoryDevices.getCategory().equals(category)) {
                for (Device device : categoryDevices.getDevices()) {
                    if (device.getName().equals(name)) {
                        int newQuantity = add ? device.getQuantity() + quantity : device.getQuantity() - quantity;
                        device.setQuantity(Math.max(0, newQuantity));
                        return device.getQuantity();
                    }
                }
            }
        }
        return -1;
    }
    /**
     * Prints a report of the inventory, listing all devices with their details and the total inventory value at the end.
     * Time Complexity: O(n) where n is the total number of devices across all categories.
     */
    public void exportInventoryReport(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            double totalValue = calculateTotalInventoryValue();
            String formattedTotalValue = String.format("%,.2f", totalValue);

            writer.println("Electronics Shop Inventory Report");
            writer.println("Generated on: " + LocalDate.now());
            writer.println("---------------------------------------");
            writer.println("| No. | Category | Name | Price | Quantity |");
            writer.println("---------------------------------------");

            int no = 1;
            int totalDevices = 0;
            for (CategoryDevices categoryDevices : categories) {
                for (Device device : categoryDevices.getDevices()) {
                    writer.printf("| %d | %s | %s | $%.2f | %d |\n", no++, categoryDevices.getCategory(), device.getName(), device.getPrice(), device.getQuantity());
                    totalDevices++;
                }
            }

            writer.println("---------------------------------------");
            writer.println("Summary:");
            writer.println("- Total Number of Devices: " + totalDevices);
            writer.println("- Total inventory value: $" + formattedTotalValue);
            writer.println("\n\nEnd of Report");

        } catch (IOException e) {
            System.out.println("An error occurred while writing the inventory report to file: " + e.getMessage());
        }
    }

    /**
     * Inner class representing a category of devices in the inventory.
     * Each category is associated with a list of devices belonging to it.
     */
    private static class CategoryDevices {

        private String category;
        private List<Device> devices;
        /**
         * Constructs a new CategoryDevices instance for the specified category name.
         * Initially, the category contains no devices.
         * Time Complexity: O(1), as the operation does not depend on the size of the inventory.
         *
         * @param category The name of the category to be created.
         */
        public CategoryDevices(String category) {
            this.category = category;
            this.devices = new ArrayList<>();
        }
        /**
         * Returns the name of the category.
         * Time Complexity: O(1), as returning a string property is a constant time operation.
         *
         * @return The name of the category.
         */
        public String getCategory() {
            return category;
        }
        /**
         * Returns the list of devices in this category.
         * Time Complexity: O(1), as returning a reference to a list is a constant time operation.
         *
         * @return A list of devices belonging to this category.
         */
        public List<Device> getDevices() {
            return devices;
        }
    }
}