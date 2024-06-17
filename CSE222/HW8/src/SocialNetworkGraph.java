import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Represents the social network graph where each node is a person and edges represent friendships.
 * This class manages contacts and their connections, with function definitions for adding/removing
 * contacts or friends, finding shortest paths, suggesting friends and counting sets.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class SocialNetworkGraph {
    private Map<String, Person> people;         /*Map of people with their name and timestamp as the key*/
    private Map<Person, List<Person>> graph;       /*Adjacency list representation of the graph*/
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Constructs a new instance of SocialNetworkGraph initializing the people and graph data structures.
     */
    public SocialNetworkGraph() {
        this.people = new HashMap<>();
        this.graph = new HashMap<>();
    }
    /**
     * Adds a person to the social network graph with the current timestamp.
     *
     * @param name The name of the person.
     * @param age The age of the person.
     * @param hobbies A list of the person's hobbies.
     */
    public void addPerson(String name, int age, List<String> hobbies) {
        Person person = new Person(name, age, hobbies, LocalDateTime.now());
        String key = generateKey(name, person.getFormattedTimestamp());
        people.put(key, person);
        graph.put(person, new ArrayList<>());
        System.out.println("Person added: " + name + " (Timestamp: " + person.getFormattedTimestamp() + ")");
    }
    /**
     * Adds a person to the social network graph with a specified timestamp. Especially for additions using the "initializeSocialNetwork" function.
     *
     * @param name The name of the person.
     * @param age The age of the person.
     * @param hobbies A list of the person's hobbies.
     * @param timestamp The timestamp in string format "yyyy-MM-dd HH:mm:ss" when the person joined the network.
     */
    public void addPerson(String name, int age, List<String> hobbies, String timestamp) {
        LocalDateTime parsedTimestamp = LocalDateTime.parse(timestamp, formatter);
        Person person = new Person(name, age, hobbies, parsedTimestamp);
        String key = generateKey(name, person.getFormattedTimestamp());
        people.put(key, person);
        graph.put(person, new ArrayList<>());
        System.out.println("Person added: " + name + " (Timestamp: " + person.getFormattedTimestamp() + ")");
    }
    /**
     * Removes a person from the social network graph.
     *
     * @param name The name of the person.
     * @param timestamp The timestamp in string format "yyyy-MM-dd HH:mm:ss" when the person joined the network.
     */
    public void removePerson(String name, String timestamp) {
        String key = generateKey(name, timestamp);
        Person person = people.get(key);
        if (person != null) {
            graph.values().forEach(friends -> friends.remove(person));
            graph.remove(person);
            people.remove(key);
            System.out.println("Person removed: " + name);
        } else {
            System.out.println("Person not found: " + name);
        }
    }
    /**
     * Adds a friendship between two persons in the social network graph.
     *
     * @param name1 The name of the first person.
     * @param timestamp1 The timestamp of the first person.
     * @param name2 The name of the second person.
     * @param timestamp2 The timestamp of the second person.
     */
    public void addFriendship(String name1, String timestamp1, String name2, String timestamp2) {
        Person person1 = findPerson(name1, timestamp1);
        Person person2 = findPerson(name2, timestamp2);

        if (person1 != null && person2 != null) {
            graph.get(person1).add(person2);
            graph.get(person2).add(person1);
            System.out.println("Friendship added between " + name1 + " and " + name2);
        } else {
            System.out.println("One or both persons not found.");
        }
    }
    /**
     * Removes a friendship between two persons in the social network graph.
     *
     * @param name1 The name of the first person.
     * @param timestamp1 The timestamp of the first person.
     * @param name2 The name of the second person.
     * @param timestamp2 The timestamp of the second person.
     */
    public void removeFriendship(String name1, String timestamp1, String name2, String timestamp2) {
        Person person1 = findPerson(name1, timestamp1);
        Person person2 = findPerson(name2, timestamp2);

        if (person1 != null && person2 != null) {
            graph.get(person1).remove(person2);
            graph.get(person2).remove(person1);
            System.out.println("Friendship removed between " + name1 + " and " + name2);
        } else {
            System.out.println("One or both persons not found.");
        }
    }
    /**
     * Finds the shortest path between two persons in the social network graph using BFS.
     *
     * @param name1 The name of the start person.
     * @param timestamp1 The timestamp of the start person.
     * @param name2 The name of the end person.
     * @param timestamp2 The timestamp of the end person.
     */
    public void findShortestPath(String name1, String timestamp1, String name2, String timestamp2) {
        Person start = findPerson(name1, timestamp1);   /*Locate the start person*/
        Person end = findPerson(name2, timestamp2);     /*Locate the end person*/

        if (start == null || end == null) {         /*Handle the case where either person is not found*/
            System.out.println("One or both persons not found.");
            return;
        }

        Map<Person, Person> prev = bfs(start);      /*Perform BFS to find all reachable persons from start*/

        if (!prev.containsKey(end)) {           /*Handle the case where no path exists*/
            System.out.println("No path found between " + name1 + " and " + name2);
            return;
        }

        List<Person> path = new ArrayList<>();      /*Store the path by backtracking from the end person*/
        for (Person at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);      /*Reverse the list to get the path from start to end*/

        String pathString = path.stream()
                .map(Person::getName)
                .reduce((p1, p2) -> p1 + " -> " + p2)
                .orElse("No path");

        System.out.println("Shortest path: " + pathString);
    }
    /**
     * Suggests friends for a person in the social network graph based on mutual friends and common hobbies.
     *
     * @param name The name of the person.
     * @param timestamp The timestamp of the person.
     * @param maxSuggestions The maximum number of friend suggestions to return.
     */
    public void suggestFriends(String name, String timestamp, int maxSuggestions) {
        Person person = findPerson(name, timestamp);        /*Locate the person within the network*/
        if (person == null) {
            System.out.println("Person not found: " + name);    /* Handle the case where the person is not found*/
            return;
        }

        Map<Person, FriendSuggestion> scores = new HashMap<>(); /*Map to hold potential friends and their scores*/

        for (Person p : people.values()) {
            if (p.equals(person) || graph.get(person).contains(p)) continue;    /*Skip if the person is self or already a friend*/

            int mutualFriends = (int) graph.get(person).stream().filter(graph.get(p)::contains).count();    /*Count mutual friends*/
            int commonHobbies = (int) person.getHobbies().stream().filter(p.getHobbies()::contains).count();    /*Count common hobbies*/
            double score = mutualFriends + commonHobbies * 0.5; /*Calculate the score*/

            if (score > 0) {        /*Add to potential friends if score is positive*/
                scores.put(p, new FriendSuggestion(score, mutualFriends, commonHobbies));
            }
        }

        System.out.println("Suggested friends for " + name + ":");  /*View recommendations in sequential order*/
        scores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().score, e1.getValue().score))
                .limit(maxSuggestions).forEach(entry -> {
                    Person p = entry.getKey();
                    FriendSuggestion suggestion = entry.getValue();
                    System.out.println(p.getName() + " (Score: " + suggestion.score + ", " +
                            suggestion.mutualFriends + " mutual friends, " +
                            suggestion.commonHobbies + " common hobbies)");
                });
    }
    /**
     * Helper method to find a person using their name and timestamp.
     *
     * @param name The name of the person.
     * @param timestamp The timestamp when the person joined.
     * @return The found Person object or null if not found.
     */
    private Person findPerson(String name, String timestamp) {
        String key = generateKey(name, timestamp);
        return people.get(key);
    }
    /**
     * Generates a unique key based on the name and timestamp of a person.
     *
     * @param name The name of the person.
     * @param timestamp The timestamp when the person joined.
     * @return A string representing the unique key.
     */
    private String generateKey(String name, String timestamp) {
        return name + "|" + timestamp;
    }
    /**
     * Counts and displays the number of distinct clusters in the social network.
     * A cluster is defined as a group of connected persons, where there is a path between any two persons within the same group.
     */
    public void countClusters() {
        Set<Person> visited = new HashSet<>();          /*Set to track visited persons to ensure each person is processed once*/
        List<List<Person>> clusters = new ArrayList<>();    /*List to hold each cluster found*/
        int clustersCount = 0;                          /*Counter for the number of clusters*/

        for (Person person : people.values()) {         /*Iterate over all persons in the network*/
            if (!visited.contains(person)) {            /*Check if the person has already been visited*/
                clustersCount++;                        /*Increment cluster count for each new cluster found*/
                List<Person> cluster = new ArrayList<>();   /*Create a new list to hold persons in the current cluster*/
                bfsCluster(person, visited, cluster);      /* Perform BFS to find all persons connected to 'person'*/
                clusters.add(cluster);                  /*Add the newly found cluster to the list of clusters*/
            }
        }

        System.out.println("Counting clusters in the social network...");
        System.out.println("Number of clusters found: " + clustersCount);
        int clusterIndex = 1;                       /*Index to label the clusters in the output*/
        for (List<Person> cluster : clusters) {     /*Iterate over each cluster to print the persons it contains*/
            System.out.println("Cluster " + clusterIndex + ":");
            for (Person person : cluster) {
                System.out.println(person.getName());
            }
            clusterIndex++;                     /*Increment the cluster index for the next cluster*/
        }
    }
    /**
     * Performs a breadth-first search (BFS) to find all persons connected to a starting person.
     * This method is used to explore a cluster in the social network, marking all found persons as visited.
     * All persons found in this cluster are added to the provided cluster list.
     *
     * @param start The starting person from which the BFS begins.
     * @param visited A set of persons that have already been visited in previous BFS operations to avoid revisiting.
     * @param cluster A list where all connected persons (including the start person) are collected.
     */
    private void bfsCluster(Person start, Set<Person> visited, List<Person> cluster) {
        Queue<Person> queue = new LinkedList<>();   /*Use a queue to manage the BFS frontier*/
        queue.add(start);                       /*Add the starting person to the queue*/
        visited.add(start);                     /*Mark the starting person as visited*/
        cluster.add(start);                     /*Add the starting person to the current cluster*/

        while (!queue.isEmpty()) {              /*Continue the search until there are no more persons to visit*/
            Person person = queue.poll();           /*Remove and get the head of the queue*/

            for (Person friend : graph.get(person)) {   /*Iterate through all friends (direct connections) of the current person*/
                if (!visited.contains(friend)) {        /*Check if this friend has already been visited*/
                    queue.add(friend);                  /*Add the unvisited friend to the queue for further exploration*/
                    visited.add(friend);                /*Mark this friend as visited to prevent reprocessing*/
                    cluster.add(friend);                /*Add this friend to the current cluster*/
                }
            }
        }
    }
    /**
     * Performs a breadth-first search (BFS) starting from a given person and maps each person to their predecessor.
     * This BFS implementation is used for finding the shortest paths between persons and also for exploring all reachable nodes
     * from a given start node, marking each as visited. This method allows finding shortest paths
     * and counting clusters within the social network.
     *
     * @param start The person from which BFS starts.
     * @return A map where each key is a person reached during the search, and the value is the person from which this person was reached.
     */
    private Map<Person, Person> bfs(Person start) {
        Map<Person, Person> prev = new HashMap<>();     /*Map to store the predecessor of each person visited*/
        Queue<Person> queue = new LinkedList<>();       /*Queue to manage the BFS frontier*/
        queue.add(start);                               /*Start the BFS with the initial person*/
        prev.put(start, null);                       /*The start person has no predecessor*/

        while (!queue.isEmpty()) {
            Person person = queue.poll();               /*Dequeue the next person to process*/

            for (Person friend : graph.get(person)) {   /*Explore all friends (direct connections) of the current person*/
                if (!prev.containsKey(friend)) {        /*Check if this friend has already been visited*/
                    queue.add(friend);                  /*Enqueue the unvisited friend*/
                    prev.put(friend, person);           /*Map this friend to the current person as its predecessor*/
                }
            }
        }

        return prev;                            /* Return the map of predecessors for all visited persons*/
    }
    /**
     * Private class for storing friend suggestion details.
     */
    private static class FriendSuggestion {
        double score;               /*Computed score based on mutual friends and common hobbies*/
        int mutualFriends;          /*Number of mutual friends with the person*/
        int commonHobbies;          /*Number of common hobbies with the person*/
        /**
         * Constructs a new FriendSuggestion.
         *
         * @param score The computed score.
         * @param mutualFriends Number of mutual friends.
         * @param commonHobbies Number of common hobbies.
         */
        FriendSuggestion(double score, int mutualFriends, int commonHobbies) {
            this.score = score;
            this.mutualFriends = mutualFriends;
            this.commonHobbies = commonHobbies;
        }
    }
}
