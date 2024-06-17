import java.util.*;
/**
 * The Main class of the social network analysis system.
 * This class handles the user interface and interaction with the SocialNetworkGraph.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SocialNetworkGraph socialNetwork = new SocialNetworkGraph();
    /**
     * Main method to run the social network analysis system.
     * @param args Not used.
     */
    public static void main(String[] args) {
        initializeSocialNetwork();          /*Initialization with some predefined data*/
        while (true) {
            displayMenu();
            int option = getUserOption();

            switch (option) {
                case 1:
                    addPerson();
                    break;
                case 2:
                    removePerson();
                    break;
                case 3:
                    addFriendship();
                    break;
                case 4:
                    removeFriendship();
                    break;
                case 5:
                    findShortestPath();
                    break;
                case 6:
                    suggestFriends();
                    break;
                case 7:
                    countClusters();
                    break;
                case 8:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    /**
     * Initializes the social network with predefined data.
     */
    private static void initializeSocialNetwork() {
        try {
            socialNetwork.addPerson("Michael Green", 40, Arrays.asList("photography", "traveling", "golf"), "2024-05-26 10:15:30");
            socialNetwork.addPerson("Sarah White", 22, Arrays.asList("gaming", "reading", "music"), "2024-05-26 11:20:45");
            socialNetwork.addPerson("Kevin Black", 29, Arrays.asList("cooking", "traveling", "running"), "2024-05-26 12:25:50");
            socialNetwork.addPerson("Laura Blue", 35, Arrays.asList("painting", "hiking", "swimming"), "2024-05-26 13:30:00");
            socialNetwork.addPerson("Daniel Red", 28, Arrays.asList("running", "music", "photography"), "2024-05-26 14:35:15");
            socialNetwork.addPerson("Olivia Yellow", 33, Arrays.asList("golf", "reading", "yoga"), "2024-05-26 15:40:20");
            socialNetwork.addPerson("James Brown", 27, Arrays.asList("gaming", "music", "cooking"), "2024-05-26 16:45:25");
            socialNetwork.addPerson("Isabella Pink", 31, Arrays.asList("painting", "yoga", "swimming"), "2024-05-26 17:50:30");
            socialNetwork.addPerson("Sophia Green", 26, Arrays.asList("writing", "traveling", "biking"), "2024-05-26 18:55:35");
            socialNetwork.addPerson("Liam Blue", 32, Arrays.asList("photography", "gaming", "cooking"), "2024-05-26 19:00:40");

            socialNetwork.addFriendship("Michael Green", "2024-05-26 10:15:30", "Sarah White", "2024-05-26 11:20:45");
            socialNetwork.addFriendship("Michael Green", "2024-05-26 10:15:30", "Kevin Black", "2024-05-26 12:25:50");
            socialNetwork.addFriendship("Sarah White", "2024-05-26 11:20:45", "Laura Blue", "2024-05-26 13:30:00");
            socialNetwork.addFriendship("Laura Blue", "2024-05-26 13:30:00", "Daniel Red", "2024-05-26 14:35:15");
            socialNetwork.addFriendship("Daniel Red", "2024-05-26 14:35:15", "Olivia Yellow", "2024-05-26 15:40:20");
            socialNetwork.addFriendship("Olivia Yellow", "2024-05-26 15:40:20", "James Brown", "2024-05-26 16:45:25");
            socialNetwork.addFriendship("James Brown", "2024-05-26 16:45:25", "Isabella Pink", "2024-05-26 17:50:30");
            socialNetwork.addFriendship("Isabella Pink", "2024-05-26 17:50:30", "Michael Green", "2024-05-26 10:15:30");
            socialNetwork.addFriendship("Sophia Green", "2024-05-26 18:55:35", "Liam Blue", "2024-05-26 19:00:40");
            socialNetwork.addFriendship("Sophia Green", "2024-05-26 18:55:35", "Laura Blue", "2024-05-26 13:30:00");
            socialNetwork.addFriendship("Liam Blue", "2024-05-26 19:00:40", "Kevin Black", "2024-05-26 12:25:50");

            socialNetwork.removePerson("James Brown", "2024-05-26 16:45:25");
            socialNetwork.removeFriendship("Olivia Yellow", "2024-05-26 15:40:20", "Daniel Red", "2024-05-26 14:35:15");

            socialNetwork.findShortestPath("Michael Green", "2024-05-26 10:15:30", "Olivia Yellow", "2024-05-26 15:40:20");
            socialNetwork.findShortestPath("Sarah White", "2024-05-26 11:20:45", "Isabella Pink", "2024-05-26 17:50:30");
            socialNetwork.findShortestPath("Sophia Green", "2024-05-26 18:55:35", "Laura Blue", "2024-05-26 13:30:00");

            socialNetwork.suggestFriends("Daniel Red", "2024-05-26 14:35:15", 3);
            socialNetwork.suggestFriends("Sophia Green", "2024-05-26 18:55:35", 2);

            socialNetwork.countClusters();

            socialNetwork.addPerson("George Black", 45, Arrays.asList("hiking", "golf", "music"), "2024-05-26 20:10:50");
            socialNetwork.addFriendship("George Black", "2024-05-26 20:10:50", "Michael Green", "2024-05-26 10:15:30");
            socialNetwork.addFriendship("George Black", "2024-05-26 20:10:50", "Sophia Green", "2024-05-26 18:55:35");

            socialNetwork.findShortestPath("George Black", "2024-05-26 20:10:50", "Laura Blue", "2024-05-26 13:30:00");
            socialNetwork.suggestFriends("George Black", "2024-05-26 20:10:50", 1);
            socialNetwork.countClusters();
        } catch (Exception e) {
            System.out.println("Error initializing social network: " + e.getMessage());
        }
    }
    /**
     * Displays the main menu options to the user.
     */
    private static void displayMenu() {
        System.out.println("\n===== Social Network Analysis Menu =====");
        System.out.println("1. Add person");
        System.out.println("2. Remove person");
        System.out.println("3. Add friendship");
        System.out.println("4. Remove friendship");
        System.out.println("5. Find shortest path");
        System.out.println("6. Suggest friends");
        System.out.println("7. Count clusters");
        System.out.println("8. Exit");
        System.out.print("Please select an option: ");
    }
    /**
     * Gets the user's menu choice.
     * @return The user's menu choice as an integer.
     */
    private static int getUserOption() {
        int option = -1;
        try {
            option = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
        return option;
    }
    /**
     * Adds a new person to the social network based on user input.
     */
    private static void addPerson() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter hobbies (comma-separated): ");
            List<String> hobbies = new ArrayList<>(Arrays.asList(scanner.nextLine().split(",")));

            socialNetwork.addPerson(name, age, hobbies);
        } catch (Exception e) {
            System.out.println("Error adding person: " + e.getMessage());
        }
    }
    /**
     * Removes a person from the social network based on user input.
     */
    private static void removePerson() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp = scanner.nextLine();
            socialNetwork.removePerson(name, timestamp);
        } catch (Exception e) {
            System.out.println("Error removing person: " + e.getMessage());
        }
    }
    /**
     * Adds a friendship between two persons based on user input.
     */
    private static void addFriendship() {
        try {
            System.out.print("Enter first person’s name: ");
            String name1 = scanner.nextLine();
            System.out.print("Enter first person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp1 = scanner.nextLine();
            System.out.print("Enter second person’s name: ");
            String name2 = scanner.nextLine();
            System.out.print("Enter second person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp2 = scanner.nextLine();
            socialNetwork.addFriendship(name1, timestamp1, name2, timestamp2);
        } catch (Exception e) {
            System.out.println("Error adding friendship: " + e.getMessage());
        }
    }
    /**
     * Removes a friendship between two persons based on user input.
     */
    private static void removeFriendship() {
        try {
            System.out.print("Enter first person’s name: ");
            String name1 = scanner.nextLine();
            System.out.print("Enter first person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp1 = scanner.nextLine();
            System.out.print("Enter second person’s name: ");
            String name2 = scanner.nextLine();
            System.out.print("Enter second person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp2 = scanner.nextLine();
            socialNetwork.removeFriendship(name1, timestamp1, name2, timestamp2);
        } catch (Exception e) {
            System.out.println("Error removing friendship: " + e.getMessage());
        }
    }
    /**
     * Finds the shortest path between two persons in the social network based on user input.
     */
    private static void findShortestPath() {
        try {
            System.out.print("Enter first person’s name: ");
            String name1 = scanner.nextLine();
            System.out.print("Enter first person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp1 = scanner.nextLine();
            System.out.print("Enter second person’s name: ");
            String name2 = scanner.nextLine();
            System.out.print("Enter second person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp2 = scanner.nextLine();
            socialNetwork.findShortestPath(name1, timestamp1, name2, timestamp2);
        } catch (Exception e) {
            System.out.println("Error finding shortest path: " + e.getMessage());
        }
    }
    /**
     * Suggests friends for a person based on mutual friends and common hobbies, using user input to specify the person.
     */
    private static void suggestFriends() {
        try {
            System.out.print("Enter person’s name: ");
            String name = scanner.nextLine();
            System.out.print("Enter person’s timestamp (yyyy-MM-dd HH:mm:ss): ");
            String timestamp = scanner.nextLine();
            System.out.print("Enter maximum number of friends to suggest: ");
            int maxSuggestions = Integer.parseInt(scanner.nextLine().trim());
            socialNetwork.suggestFriends(name, timestamp, maxSuggestions);
        } catch (Exception e) {
            System.out.println("Error suggesting friends: " + e.getMessage());
        }
    }
    /**
     * Counts the clusters within the social network and prints the results.
     * The person without friends is included in the count.
     */
    private static void countClusters() {
        try {
            socialNetwork.countClusters();
        } catch (Exception e) {
            System.out.println("Error counting clusters: " + e.getMessage());
        }
    }
}
