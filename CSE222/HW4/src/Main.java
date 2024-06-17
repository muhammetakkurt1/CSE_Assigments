import java.util.Scanner;

/**
 * The Main class serves as the entry point for the file system management application.
 * It provides a command-line interface for interacting with a simulated file system,
 * allowing the user to perform a variety of file management tasks such as changing directories,
 * listing contents, creating or deleting files and directories, moving items, and more.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class Main {
    /**
     * The main method that sets up the file system and handles the command-line interactions.
     * It continuously displays a menu of options until the user decides to exit.
     *
     * @param args The command line arguments passed to the program (not used).
     */
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        Directory currentDirectory = fs.getRoot();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("===== File System Management Menu =====");
            System.out.println("1. Change directory");
            System.out.println("2. List directory contents");
            System.out.println("3. Create file/directory");
            System.out.println("4. Delete file/directory");
            System.out.println("5. Move file/directory");
            System.out.println("6. Search file/directory");
            System.out.println("7. Print directory tree");
            System.out.println("8. Sort contents by date created");
            System.out.println("9. Exit");
            System.out.print("Please select an option: ");

            int choice = -1;
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 1 && choice <= 9) {
                        break;
                    } else {
                        System.out.println("Please enter a number between 1 and 9.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next();
                }
            }

            switch (choice) {
                case 1:
                    System.out.println("Current directory: " + currentDirectory.getPath());
                    System.out.print("Enter new directory path: ");
                    String path = scanner.nextLine();
                    Directory newDirectory = fs.changeDirectory(path);
                    if (newDirectory != null) {
                        currentDirectory = newDirectory;
                        System.out.println("Directory changed to: " + currentDirectory.getPath());
                    }
                    break;
                case 2:
                    System.out.println("Listing contents of " + currentDirectory.getPath() + ":");
                    fs.listCurrentDirectoryContents(currentDirectory);
                    break;
                case 3:
                    System.out.println("Current directory: " + currentDirectory.getPath());
                    System.out.print("Create file or directory (f/d): ");
                    String type = scanner.nextLine();
                    System.out.print("Enter name for new file or directory: ");
                    String name = scanner.nextLine();
                    fs.createFileOrDirectory(type, name, currentDirectory);
                    break;
                case 4:
                    System.out.println("Current directory: " + currentDirectory.getPath());
                    System.out.print("Enter name of file/directory to delete: ");
                    String nameToDelete = scanner.nextLine();
                    if (currentDirectory.getName().equals(nameToDelete)) {
                        if (currentDirectory.getParent() != null) {
                            Directory newCurrent = (Directory) currentDirectory.getParent();
                            fs.deleteFileOrDirectory(nameToDelete, newCurrent);
                            currentDirectory = newCurrent;
                            System.out.println("Current directory was deleted. Moved up to " + currentDirectory.getName() + ".");
                        } else {
                            System.out.println("Cannot delete the root directory.");
                        }
                    } else {
                        fs.deleteFileOrDirectory(nameToDelete, currentDirectory);
                    }
                    break;
                case 5:
                    System.out.println("Current directory: " + currentDirectory.getPath());
                    System.out.print("Enter the name of file/directory to move: ");
                    String nameToMove = scanner.nextLine();
                    System.out.print("Enter new directory path: ");
                    String newPath = scanner.nextLine();
                    fs.moveFileOrDirectory(nameToMove, newPath, currentDirectory);
                    break;
                case 6:
                    System.out.print("Search query: ");
                    String searchQuery = scanner.nextLine();
                    System.out.println("Searching from root...");
                    fs.searchFileOrDirectory(searchQuery);
                    break;
                case 7:
                    System.out.println("Path to current directory from root:");
                    fs.printDirectoryTree(currentDirectory);
                    break;
                case 8:
                    fs.sortAndDisplayCurrentDirectoryContents(currentDirectory);
                    break;
                case 9:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
        scanner.close();
    }
}
