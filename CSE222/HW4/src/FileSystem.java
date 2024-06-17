import java.util.LinkedList;
/**
 * Represents the entire file system, managing the root directory and providing functionality to manipulate and query the file system.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class FileSystem {
    /**
     * The root directory of the filesystem. This is the top-level directory from which
     * all other directories and files are accessible. It acts as the entry point for navigating
     * through the directory structure and performing file system operations.
     */
    private Directory root;

    /**
     * Constructs a FileSystem with a single root directory.
     */
    public FileSystem() {
        root = new Directory("root", null);
    }

    /**
     * Changes the current directory to the specified path.
     *
     * @param path The path to the new directory as a string separated by slashes.
     * @return The new Directory object if the path is valid, otherwise null.
     */
    public Directory changeDirectory(String path) {
        String[] parts = path.split("/");
        Directory newDirectory = root;

        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            boolean found = false;
            for (FileSystemElement element : newDirectory.getChildren()) {
                if (element instanceof Directory && element.getName().equals(part)) {
                    newDirectory = (Directory) element;
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Directory not found: " + path);
                return null;
            }
        }
        return newDirectory;
    }

    /**
     * Lists the contents of the specified directory.
     *
     * @param directory The directory whose contents are to be listed.
     */
    public void listCurrentDirectoryContents(Directory directory) {
        directory.listContents();
    }

    /**
     * Creates a new file or directory within the specified parent directory.
     *
     * @param type The type of the element to create ('d' for directory, 'f' for file).
     * @param name The name of the new file or directory.
     * @param parentDirectory The directory where the new element will be created.
     */
    public void createFileOrDirectory(String type, String name, Directory parentDirectory) {
        if (type.equalsIgnoreCase("d")) {
            Directory newDirectory = new Directory(name, parentDirectory);
            parentDirectory.addElement(newDirectory);
            System.out.println("Directory created: " + name + "/");
        } else if (type.equalsIgnoreCase("f")) {
            File newFile = new File(name, parentDirectory);
            parentDirectory.addElement(newFile);
            System.out.println("File created: " + name);
        } else {
            System.out.println("Invalid type specified.");
        }
    }

    /**
     * Deletes a specified file or directory from a given parent directory.
     *
     * @param name The name of the file or directory to delete.
     * @param parentDirectory The directory from which the file or directory will be removed.
     */
    public void deleteFileOrDirectory(String name, Directory parentDirectory) {
        FileSystemElement element = parentDirectory.search(name);               /* First, it searches the parent directory for a file or directory with the specified name*/
        if (element != null) {                                              /*If the searched element is found, the deletion process continues*/
            if (element instanceof Directory) {                         /*If the element is an index, the contents of the index are recursively deleted*/
                deleteAllContents((Directory) element);
                if (element.getParent() != null) {
                    element.getParent().removeElement(element);                 /*Element is removed from the parent directory*/
                    System.out.println("Directory deleted: " + name + "/");
                }
            } else {                                        /*If the element is a file, it is deleted directly*/
                parentDirectory.removeElement(element);
                System.out.println("File deleted: " + name);
            }
        } else {
            System.out.println("File or directory not found.");
        }
    }

    /**
     * Recursively deletes all contents of a directory.
     *
     * @param directory The directory whose contents are to be deleted.
     */
    private void deleteAllContents(Directory directory) {
        FileSystemElement[] elements = directory.getChildren().toArray(new FileSystemElement[0]);       /*All elements in the index are transferred to an array*/
        for (FileSystemElement child : elements) {
            if (child instanceof Directory) {               /*If child is a directory, the contents of this directory are also recursively deleted*/
                deleteAllContents((Directory) child);
            }
            directory.removeElement(child);             /*Child element is removed from the current directory*/
        }
    }

    /**
     * Moves a file or directory to a new parent directory.
     *
     * @param name The name of the file or directory to move.
     * @param newParentPath The path to the new parent directory.
     * @param currentDirectory The current directory from where the search should start.
     */
    public void moveFileOrDirectory(String name, String newParentPath, Directory currentDirectory) {
        FileSystemElement elementToMove = currentDirectory.search(name);    /*Searches the current directory for a file or directory with the searched name*/
        if (elementToMove == null) {                                        /*If the element is not found, print an error message and terminate the method*/
            System.out.println("File or directory not found: " + name);
            return;
        }

        Directory newParent = findDirectoryByPath(newParentPath);           /*Searches for the new parent directory according to the given path*/
        if (newParent == null) {                                            /*If the target directory is not found, print an error message and terminate the method*/
            System.out.println("Target directory not found: " + newParentPath);
            return;
        }

        if (moveElement(elementToMove, newParent)) {                        /*moveElement method to move the found element to the new parent directory*/
            String elementType = elementToMove instanceof Directory ? "Directory" : "File";         /* If the element is successfully moved, print a message depending on the type of element moved*/
            System.out.println(elementType + " moved: " + name + " to " + newParentPath);
        } else {
            System.out.println("Failed to move " + name + " to " + newParentPath);      /*If the move fails, print an error message*/
        }
    }

    /**
     * Finds a directory by its path.
     *
     * @param path The path to the directory as a string separated by slashes.
     * @return The Directory object if found, otherwise null.
     */
    private Directory findDirectoryByPath(String path) {
        String[] parts = path.split("/");
        Directory current = root;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            boolean found = false;
            for (FileSystemElement child : current.getChildren()) {
                if (child instanceof Directory && child.getName().equals(part)) {
                    current = (Directory) child;
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        return current;
    }

    /**
     * Moves an element to a new parent directory.
     * If the element currently has a parent, it is removed from that parent's children list.
     * The element is then added to the new parent's children list.
     * The element's parent reference is updated to the new parent.
     *
     * @param element The filesystem element (either a file or directory) to move.
     * @param newParent The new parent directory to which the element will be moved.
     * @return true if the element was successfully moved.
     */
    private boolean moveElement(FileSystemElement element, Directory newParent) {
        if (element.getParent() != null) {                  /*If the element to be moved has a parent, remove it from the current parent's child list*/
            element.getParent().removeElement(element);
        }

        newParent.addElement(element);          /*Adds the element to the child list of the new parent directory*/
        element.setParent(newParent);           /*Updates the element's parent reference to the new parent directory*/
        return true;
    }

    /**
     * Searches for a file or directory starting from the root and prints its path if found.
     *
     * @param name The name of the file or directory to search for.
     */
    public void searchFileOrDirectory(String name) {
        FileSystemElement foundElement = root.search(name);
        if (foundElement != null) {
            System.out.println("Found: " + foundElement.getPath());
        } else {
            System.out.println("No file or directory found with name: " + name);
        }
    }

    /**
     * Prints all directories starting with the current directory and its contents up to the root directory.
     *
     * @param directory The directory from which to start printing the directory tree.
     */
    public void printDirectoryTree(Directory directory) {
        LinkedList<Directory> pathStack = new LinkedList<>();       /*Creates a stack to store the path from the current directory to the root directory using a LinkedList*/
        Directory current = directory;

        while (current != null) {                               /*Adds every directory from the current directory to the root directory to the stack*/
            pathStack.push(current);
            current = (Directory) current.getParent();
        }

        String prefix = "";
        while (!pathStack.isEmpty()) {                          /*The loop continues until it reaches the root directory*/
            Directory dir = pathStack.pop();
            System.out.print(prefix + "* " + dir.getName());
            if (dir == directory) {                             /*If the current directory is the starting directory given as a parameter, the phrase 'Current Directory' is added next to it*/
                System.out.print("/ (Current Directory)");
            } else {
                System.out.print("/");
            }
            System.out.println();

            if (pathStack.isEmpty()) {                                          /*When the bottom directory is reached, it lists the children of this list*/
                for (FileSystemElement element : dir.getChildren()) {
                    if (element instanceof Directory) {
                        System.out.println(prefix + "  * " + element.getName() + "/");
                    } else {
                        System.out.println(prefix + "  " + element.getName());
                    }
                }
            }
            prefix += "  ";
        }
    }

    /**
     * Sorts and displays the contents of the specified directory by their creation dates.
     *
     * @param directory The directory whose contents are to be sorted and displayed.
     */
    public void sortAndDisplayCurrentDirectoryContents(Directory directory) {
        System.out.println("Sorted contents of " + directory.getPath() + " by date created:");
        directory.displaySortedContents();
    }
    
    /**
     * Returns the root directory of the file system.
     *
     * @return The root directory.
     */
    public Directory getRoot() {
        return root;
    }
}
