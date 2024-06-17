import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Main class for the Stock Data Management System.
 * It reads commands from an input file, processes them, and performs performance analysis.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class Main {
    /**
     *
     * This method checks for the correct number of arguments and initializes the StockDataManager.
     * It reads commands from the specified input file, processes each command, and performs performance analysis.
     *
     * @param args the input file containing stock management commands.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <input_file>");
            return;
        }

        String inputFile = args[0];
        StockDataManager manager = new StockDataManager();
        List<String> commands = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                commands.add(line);
                processCommand(line, manager);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        performPerformanceAnalysis(commands);
    }
    /**
     * Processes a single command line.
     * This method parses the command line to determine the type of operation (ADD, REMOVE, SEARCH, UPDATE)
     * and calls the appropriate method on the StockDataManager instance to perform the operation.
     *
     * @param line the command line to process.
     * @param manager the StockDataManager instance to manage stocks.
     */
    private static void processCommand(String line, StockDataManager manager) {
        String[] tokens = line.split(" ");
        String command = tokens[0];

        switch (command) {
            case "ADD":
                if (tokens.length != 5) {
                    System.out.println("ADD command requires symbol, price, volume, and marketCap parameters.");
                    return;
                }
                try {
                    double price = Double.parseDouble(tokens[2]);
                    long volume = Long.parseLong(tokens[3]);
                    long marketCap = Long.parseLong(tokens[4]);
                    manager.addOrUpdateStock(tokens[1], price, volume, marketCap);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid parameters for ADD command. Price, volume, and marketCap must be numbers.");
                }
                break;
            case "REMOVE":
                if (tokens.length != 2) {
                    System.out.println("REMOVE command requires symbol parameter.");
                    return;
                }
                manager.removeStock(tokens[1]);
                break;
            case "SEARCH":
                if (tokens.length != 2) {
                    System.out.println("SEARCH command requires symbol parameter.");
                    return;
                }
                Stock stock = manager.searchStock(tokens[1]);
                if (stock != null) {
                    System.out.println(stock);
                } else {
                    System.out.println("Stock not found: " + tokens[1]);
                }
                break;
            case "UPDATE":
                if (tokens.length != 6) {
                    System.out.println("UPDATE command requires old symbol, new symbol, new price, new volume, and new marketCap parameters.");
                    return;
                }
                try {
                    double newPrice = Double.parseDouble(tokens[3]);
                    long newVolume = Long.parseLong(tokens[4]);
                    long newMarketCap = Long.parseLong(tokens[5]);
                    manager.updateStock(tokens[1], tokens[2], newPrice, newVolume, newMarketCap);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid parameters for UPDATE command. New price, new volume, and new marketCap must be numbers.");
                }
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }
    /**
     * Performs performance analysis for each operation.
     * This method measures the time taken for ADD, REMOVE, SEARCH, and UPDATE operations.
     * It collects the time taken for each operation and the size of the AVL tree.
     * The collected data is then visualized using GUIVisualization for each operation type.
     *
     * @param commands the list of commands to process.
     */
    private static void performPerformanceAnalysis(List<String> commands) {
        List<Integer> addDataPointsX = new ArrayList<>();
        List<Long> addTimes = new ArrayList<>();

        List<Integer> searchDataPointsX = new ArrayList<>();
        List<Long> searchTimes = new ArrayList<>();

        List<Integer> removeDataPointsX = new ArrayList<>();
        List<Long> removeTimes = new ArrayList<>();

        List<Integer> updateDataPointsX = new ArrayList<>();
        List<Long> updateTimes = new ArrayList<>();

        StockDataManager manager = new StockDataManager();
        long startTime, endTime;
        int currentSize = 0;

        for (String command : commands) {
            String[] tokens = command.split(" ");
            if (tokens.length < 2) {
                System.out.println("Invalid command: " + command);
                continue;
            }
            switch (tokens[0]) {
                case "ADD":
                    if (tokens.length != 5) {
                        System.out.println("Invalid ADD command: " + command);
                        continue;
                    }
                    startTime = System.nanoTime();
                    manager.addOrUpdateStock(tokens[1], Double.parseDouble(tokens[2]), Long.parseLong(tokens[3]), Long.parseLong(tokens[4]));
                    endTime = System.nanoTime();
                    addTimes.add(endTime - startTime);
                    currentSize++;
                    addDataPointsX.add(currentSize);
                    break;

                case "SEARCH":
                    if (tokens.length != 2) {
                        System.out.println("Invalid REMOVE command: " + command);
                        continue;
                    }
                    startTime = System.nanoTime();
                    manager.searchStock(tokens[1]);
                    endTime = System.nanoTime();
                    searchTimes.add(endTime - startTime);
                    searchDataPointsX.add(currentSize);
                    break;

                case "REMOVE":
                    if (tokens.length != 2) {
                        System.out.println("Invalid SEARCH command: " + command);
                        continue;
                    }
                    startTime = System.nanoTime();
                    manager.removeStock(tokens[1]);
                    endTime = System.nanoTime();
                    removeTimes.add(endTime - startTime);
                    if (currentSize > 0) {
                        currentSize--;
                    }
                    removeDataPointsX.add(currentSize);
                    break;

                case "UPDATE":
                    if (tokens.length != 6) {
                        System.out.println("Invalid UPDATE command: " + command);
                        continue;
                    }
                    startTime = System.nanoTime();
                    manager.updateStock(tokens[1], tokens[2], Double.parseDouble(tokens[3]), Long.parseLong(tokens[4]), Long.parseLong(tokens[5]));
                    endTime = System.nanoTime();
                    updateTimes.add(endTime - startTime);
                    updateDataPointsX.add(currentSize);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }

        SwingUtilities.invokeLater(() -> {
            GUIVisualization addGraph = new GUIVisualization("line", "ADD Operation Performance", addDataPointsX, addTimes);
            addGraph.setVisible(true);

            GUIVisualization searchGraph = new GUIVisualization("line", "SEARCH Operation Performance", searchDataPointsX, searchTimes);
            searchGraph.setVisible(true);

            GUIVisualization removeGraph = new GUIVisualization("line", "REMOVE Operation Performance", removeDataPointsX, removeTimes);
            removeGraph.setVisible(true);

            GUIVisualization updateGraph = new GUIVisualization("line", "UPDATE Operation Performance", updateDataPointsX, updateTimes);
            updateGraph.setVisible(true);
        });
    }
}
