import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/**
 * Represents a person in the social network.
 * This class stores personal details including name, age, hobbies, and the timestamp of when the person joined the network.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class Person {
    private String name;
    private int age;
    private List<String> hobbies;
    private LocalDateTime timestamp;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  /*Formatter for timestamp*/
    /**
     * Constructor to initialize the Person object with given attributes.
     *
     * @param name The name of the person.
     * @param age The age of the person.
     * @param hobbies A list of the person's hobbies.
     * @param timestamp The date and time when the person joined the network.
     */
    public Person(String name, int age, List<String> hobbies, LocalDateTime timestamp) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
        this.timestamp = timestamp;
    }
    /**
     * Returns the name of the person.
     *
     * @return A string representing the person's name.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of the person.
     *
     * @param name A string containing the new name of the person.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns the age of the person.
     *
     * @return An integer representing the age of the person.
     */
    public int getAge() {
        return age;
    }
    /**
     * Sets the age of the person.
     *
     * @param age An integer containing the new age of the person.
     */
    public void setAge(int age) {
        this.age = age;
    }
    /**
     * Returns the list of hobbies of the person.
     *
     * @return A list of strings representing the hobbies of the person.
     */
    public List<String> getHobbies() {
        return hobbies;
    }
    /**
     * Sets the hobbies of the person.
     *
     * @param hobbies A list of strings containing the new hobbies of the person.
     */
    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }
    /**
     * Returns the timestamp when the person joined the network.
     *
     * @return A LocalDateTime object representing the timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    /**
     * Sets the timestamp when the person joined the network.
     *
     * @param timestamp A LocalDateTime object containing the new timestamp.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * Returns a formatted string of the timestamp.
     *
     * @return A string representing the formatted timestamp.
     */
    public String getFormattedTimestamp() {
        return timestamp.format(formatter);
    }
    /**
     * Returns a string representation of the person including all attributes.
     *
     * @return A string containing the name, age, hobbies, and timestamp of the person.
     */
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
}
