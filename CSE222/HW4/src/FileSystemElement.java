import java.sql.Timestamp;
/**
 * Abstract class representing a generic element in a filesystem, which can be either a file or a directory.
 * This class provides the common properties and methods that are shared by both files and directories.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public abstract class FileSystemElement {
    /**
     * The name of the filesystem element.
     */
    protected String name;

    /**
     * The timestamp when the filesystem element was created.
     */
    protected Timestamp dateCreated;

    /**
     * The parent directory of this filesystem element.  Null if this is the root element.
     */
    protected FileSystemElement parent;

    /**
     * Constructor to create a new filesystem element with the specified name and parent.
     * Automatically assigns the creation timestamp to the current system time.
     *
     * @param name   The name of the filesystem element.
     * @param parent The parent directory of this element.
     */
    public FileSystemElement(String name, FileSystemElement parent) {
        this.name = name;
        this.parent = parent;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Retrieves the full path of this filesystem element from the root.
     *
     * @return A string representing the absolute path of this element in the filesystem.
     */
    public String getPath() {
        if (parent == null) return "/" + name;
        return parent.getPath() + "/" + name;
    }

    /**
     * Gets the name of this filesystem element.
     *
     * @return The name of the element.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the parent directory of this filesystem element.
     *
     * @return The parent directory of this element.
     */
    public FileSystemElement getParent() {
        return this.parent;
    }

    /**
     * Gets the creation timestamp of this filesystem element.
     *
     * @return The timestamp indicating when this element was created.
     */
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets a new parent for this filesystem element.
     *
     * @param newParent The new parent directory to set.
     */
    public void setParent(FileSystemElement newParent) {
        this.parent = newParent;
    }

    /**
     * Abstract method to remove a child from this element.
     *
     * @param element The child element to remove.
     */
    public abstract void removeElement(FileSystemElement element);

    /**
     * Abstract method to add a new element as a child of this element.
     *
     * @param element The new element to add as a child.
     */
    public abstract void addElement(FileSystemElement element);
}
