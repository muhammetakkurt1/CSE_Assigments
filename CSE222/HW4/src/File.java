import java.sql.Timestamp;
/**
 * Represents a file in a filesystem.
 * This class extends {@link FileSystemElement} and embodies a single file,
 * which cannot contain other filesystem elements.
 * Attempts to add or remove children from a file will result in an exception.
 * 
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class File extends FileSystemElement {
    /**
     * Constructs a new File instance.
     *
     * @param name the name of the file.
     * @param parent the directory in which this file is located; it acts as the parent in the directory tree.
     */
    public File(String name, FileSystemElement parent) {
        super(name, parent);
        this.dateCreated = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Throws UnsupportedOperationException because a file cannot have child elements.
     *
     * @param element This parameter is ignored as a file cannot contain children.
     * @throws UnsupportedOperationException when invoked.
     */
    @Override
    public void removeElement(FileSystemElement element) {
        throw new UnsupportedOperationException("Files do not support child elements.");
    }
    
    /**
     * Throws UnsupportedOperationException because elements cannot be added to a file.
     *
     * @param element This parameter is ignored as a file cannot contain children.
     * @throws UnsupportedOperationException when invoked.
     */
    @Override
    public void addElement(FileSystemElement element) {
        throw new UnsupportedOperationException("Cannot add elements to a file.");
    }

}
