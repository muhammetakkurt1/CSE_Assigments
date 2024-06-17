import java.util.LinkedList;
import java.util.Comparator;

/**
 * Represents a directory in a filesystem.
 * This class extends {@link FileSystemElement} and manages child elements which can be files or other directories.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class Directory extends FileSystemElement {
    /**
     * A list containing all child elements of this directory.
     * This can include both {@link File} and {@link Directory} instances.
     * The list is used to maintain the hierarchical structure of the filesystem, allowing navigation and management
     * of files and subdirectories contained within this directory.
     */
    private LinkedList<FileSystemElement> children;

    /**
     * Constructs a new Directory instance.
     *
     * @param name the name of the directory.
     * @param parent the parent directory under which this directory exists.
     */
    public Directory(String name, FileSystemElement parent) {
        super(name, parent);
        this.children = new LinkedList<>();
    }

    /**
     * Adds a file system element to this directory.
     *
     * @param element the file system element to add. It will be added only if it is not null.
     */
    @Override
    public void addElement(FileSystemElement element) {
        if (element != null) {
            children.add(element);
            element.parent = this;
        }
    }

    /**
     * Removes a file system element from this directory.
     *
     * @param element the file system element to be removed. It is removed only if it is not null and exists in the children list.
     */
    @Override
    public void removeElement(FileSystemElement element) {
        if (element != null && children.contains(element)) {
            children.remove(element);
            element.parent = null;
        }
    }

    /**
     * Lists the contents of the directory. Marks directories with (*) and prints them with the files.
     */
    public void listContents() {
        for (FileSystemElement element : children) {
            if (element instanceof Directory) {
                System.out.println("* " + element.getName() + "/");
            } else {
                System.out.println(element.getName());
            }
        }
    }

    /**
     * Recursively searches for a file system element by name.
     *
     * @param name the name of the element to search for.
     * @return the file system element if found, otherwise null.
     */
    public FileSystemElement search(String name) {
        if (this.name.equals(name)) {
            return this;
        }
        for (FileSystemElement element : children) {
            if (element.getName().equals(name)) {
                return element;
            }
            if (element instanceof Directory) {
                FileSystemElement found = ((Directory) element).search(name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Sorts the children of this directory by their creation dates in ascending order.
     */
    public void sortChildrenByDate() {
        children.sort(new Comparator<FileSystemElement>() {
            @Override
            public int compare(FileSystemElement o1, FileSystemElement o2) {
                return o1.getDateCreated().compareTo(o2.getDateCreated());
            }
        });
    }

    /**
     * Displays the sorted contents of this directory by their creation dates.
     * Directories are marked (*) and their creation dates are displayed alongside.
     */
    public void displaySortedContents() {
        sortChildrenByDate();
        for (FileSystemElement child : children) {
            String typeIndicator = child instanceof Directory ? "/" : "";
            if (child instanceof Directory) {
                System.out.println("* " + child.getName() + typeIndicator + " (" + child.getDateCreated() + ")");
            } else {
                System.out.println(child.getName() + " (" + child.getDateCreated() + ")");
            }
        }
    }

    /**
     * Returns a new linked list containing the children of this directory.
     *
     * @return a linked list of {@link FileSystemElement}.
     */
    public LinkedList<FileSystemElement> getChildren() {
        return new LinkedList<>(this.children);
    }
    
    /**
     * Returns the path from the root to this directory.
     *
     * @return the path as a string.
     */
    @Override
    public String getPath() {
        if (this.parent == null) {
            return "/root";
        }

        LinkedList<String> parts = new LinkedList<>();
        FileSystemElement current = this;
        while (current != null && current.parent != null) {
            parts.addFirst(current.getName());
            current = current.getParent();
        }


        String path = String.join("/", parts);
        return "/" + path;
    }
}
