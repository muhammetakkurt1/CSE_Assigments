import java.util.Scanner;

public class Main {
    /**
     *Electronics Inventory Management System  provides a text-based user interface for interacting with the inventory, allowing users
     * to add, remove, update, and list devices, among other functionalities.
     */
    public static void main(String[] args) {
        Inventory inventory = new Inventory();      /*Initialize the inventory management system*/
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWelcome to the Electronics Inventory Management System!\n");
            System.out.println("Please select an option:");
            System.out.println("1. Add a new device");
            System.out.println("2. Remove a device");
            System.out.println("3. Update device details");
            System.out.println("4. List all devices");
            System.out.println("5. Find the cheapest device");
            System.out.println("6. Sort devices by price");
            System.out.println("7. Calculate total inventory value");
            System.out.println("8. Restock a device");
            System.out.println("9. Export inventory report");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = -1;
            boolean validInput = false;

            while (!validInput) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice >= 0 && choice <= 9) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid choice. Please enter a number between 0 and 9.");
                    }
                } else {
                    System.out.println("That's not a number. Please enter a number between 0 and 9.");
                    scanner.next();
                }
                if (!validInput) {
                    System.out.print("Enter your choice: ");
                }
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter category name: ");
                    String category = scanner.nextLine();
                    System.out.print("Enter device name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter price: ");
                    String priceStr = scanner.nextLine().replace("$", "");
                    double price = Double.parseDouble(priceStr);
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    boolean isAdded = inventory.addDevice(category, new GeneralDevice(category, name, price, quantity));
                    if (isAdded) {
                        System.out.println(name + " added to inventory under the " + category + " category.");
                    } else {
                        System.out.println("Failed to add " + name + ". A device with the same name may already exist in this category.");
                    }
                    break;
                case 2:
                    System.out.print("Enter the category of the device to remove: ");
                    category = scanner.nextLine();
                    System.out.print("Enter the name of the device to remove: ");
                    name = scanner.nextLine();
                    boolean isRemoved = inventory.removeDevice(category, name);
                    if (isRemoved) {
                        System.out.println(name + " has been successfully removed from the inventory.");
                    } else {
                        System.out.println("Device not found or could not be removed.");
                    }
                    break;
                case 3:
                    System.out.print("Enter the category of the device to update: ");
                    category = scanner.nextLine();

                    System.out.print("Enter the current name of the device to update: ");
                    String oldName = scanner.nextLine();

                    System.out.print("Enter new name of the device (leave blank to keep current name): ");
                    String newName = scanner.nextLine();
                    if (newName.isEmpty()) {
                        newName = oldName;
                    }

                    System.out.print("Enter new price (leave blank to keep current price): ");
                    priceStr = scanner.nextLine();
                    price = priceStr.isEmpty() ? -1 : Double.parseDouble(priceStr.replace("$", ""));

                    System.out.print("Enter new quantity (leave blank to keep current quantity): ");
                    String quantityInput = scanner.nextLine();
                    quantity = quantityInput.isEmpty() ? -1 : Integer.parseInt(quantityInput);

                    boolean isUpdated = inventory.updateDeviceDetails(category, oldName, newName, price, quantity);
                    if (isUpdated) {
                        System.out.println((oldName.equals(newName) ? newName : oldName + " renamed to " + newName)
                                + " details updated in " + category + " category.");
                    } else {
                        System.out.println("Device not found or could not be updated.");
                    }
                    break;
                case 4:
                    inventory.listAllDevices();
                    break;
                case 5:
                    Device cheapest = inventory.findCheapestDevice();
                    if (cheapest != null) {
                        System.out.println("The cheapest device is:\n" + "Category: " + cheapest.getCategory() + ", Name: " + cheapest.getName() + ", Price: $" + cheapest.getPrice() + ", Quantity: " + cheapest.getQuantity());
                    } else {
                        System.out.println("No devices in inventory.");
                    }
                    break;
                case 6:
                    inventory.sortAndListDevicesByPrice();
                    break;
                case 7:
                    double totalValue = inventory.calculateTotalInventoryValue();
                    String formattedTotalValue = String.format("%,.2f", totalValue);

                    System.out.println("Total inventory value: $" + formattedTotalValue);
                    break;
                case 8:
                    System.out.print("Enter the category of the device to restock: ");
                    category = scanner.nextLine();
                    System.out.print("Enter the name of the device to restock: ");
                    name = scanner.nextLine();
                    System.out.print("Do you want to add or remove stock? (Add/Remove): ");
                    String action = scanner.nextLine();
                    System.out.print("Enter the quantity to " + (action.equalsIgnoreCase("Add") ? "add: " : "remove: "));
                    int stockChange = scanner.nextInt();
                    scanner.nextLine();

                    boolean addStock = action.equalsIgnoreCase("Add");
                    int newQuantity = inventory.restockDevice(category, name, stockChange, addStock);
                    if (newQuantity != -1) {
                        System.out.println(name + (addStock ? " restocked. " : " stock reduced. ") + "New quantity: " + newQuantity);
                    } else {
                        System.out.println("Device not found.");
                    }
                    break;
                case 9:
                    String filename = "InventoryReport.txt";
                    inventory.exportInventoryReport(filename);
                    System.out.println("Inventory report has been successfully exported to " + filename);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }
}