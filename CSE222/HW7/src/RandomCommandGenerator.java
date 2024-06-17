import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * RandomCommandGenerator class for generating random stock management commands.
 * This class generates commands for adding, removing, searching, and updating stocks
 * and writes them to a specified file.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class RandomCommandGenerator {

    private static final List<String> validSymbols = new ArrayList<>();
    private static final Random random = new Random();
    private static final double VALID_SYMBOL_PROBABILITY = 0.5;
    private static final String[] symbols = generateSymbols(1000);
    /**
     * The main method to generate random stock management commands and write them to a file.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        int numCommands = 10000;
        String filename = "commands.txt";

        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < numCommands; i++) {
                String command = generateRandomCommand();
                writer.write(command + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a random command based on predefined probabilities.
     *
     * @return a random stock management command
     */
    private static String generateRandomCommand() {
        int commandType = random.nextInt(10);
        if (commandType < 5) {
            return generateAddCommand();
        } else if (commandType < 7) {
            return generateRemoveCommand();
        } else if (commandType < 9) {
            return generateSearchCommand();
        } else {
            return generateUpdateCommand();
        }
    }
    /**
     * Generates an ADD command with random parameters.
     *
     * @return a formatted ADD command string
     */
    private static String generateAddCommand() {
        String symbol = getRandomSymbol();
        double price = getRandomPrice();
        long volume = getRandomVolume();
        long marketCap = getRandomMarketCap();
        validSymbols.add(symbol);
        return String.format("ADD %s %.2f %d %d", symbol, price, volume, marketCap);
    }
    /**
     * Generates a REMOVE command with a random or valid symbol.
     *
     * @return a formatted REMOVE command string
     */
    private static String generateRemoveCommand() {
        String symbol = getRandomSymbol(true);
        return String.format("REMOVE %s", symbol);
    }
    /**
     * Generates a SEARCH command with a random or valid symbol.
     *
     * @return a formatted SEARCH command string
     */
    private static String generateSearchCommand() {
        String symbol = getRandomSymbol(true);
        return String.format("SEARCH %s", symbol);
    }
    /**
     * Generates an UPDATE command with random parameters.
     *
     * @return a formatted UPDATE command string
     */
    private static String generateUpdateCommand() {
        String oldSymbol = getRandomSymbol(true);
        String newSymbol = getRandomSymbol();
        double newPrice = getRandomPrice();
        long newVolume = getRandomVolume();
        long newMarketCap = getRandomMarketCap();
        return String.format("UPDATE %s %s %.2f %d %d", oldSymbol, newSymbol, newPrice, newVolume, newMarketCap);
    }
    /**
     * Gets a random symbol from the predefined symbols array.
     *
     * @return a random symbol string
     */
    private static String getRandomSymbol() {
        return symbols[random.nextInt(symbols.length)];
    }
    /**
     * Gets a random symbol, with a probability of selecting a valid symbol if required.
     *
     * @param useValidSymbol flag indicating whether to use a valid symbol
     * @return a random or valid symbol string
     */
    private static String getRandomSymbol(boolean useValidSymbol) {
        if (useValidSymbol && !validSymbols.isEmpty() && random.nextDouble() < VALID_SYMBOL_PROBABILITY) {
            return validSymbols.get(random.nextInt(validSymbols.size()));
        } else {
            return getRandomSymbol();
        }
    }
    /**
     * Generates a random price between 10 and 1000.
     *
     * @return a random price
     */
    private static double getRandomPrice() {
        return 10 + (1000 - 10) * random.nextDouble();
    }
    /**
     * Generates a random volume between 100,000 and 1,000,000.
     *
     * @return a random volume
     */
    private static long getRandomVolume() {
        return 100000 + random.nextInt(900000);
    }

    /**
     * Generates a random market cap between 1,000,000 and 10,000,000.
     *
     * @return a random market cap
     */
    private static long getRandomMarketCap() {
        return 1000000 + random.nextInt(9000000);
    }
    /**
     * Generates an array of random symbols.
     *
     * @param count the number of symbols to generate
     * @return an array of generated symbols
     */
    private static String[] generateSymbols(int count) {
        String[] symbols = new String[count];
        for (int i = 0; i < count; i++) {
            symbols[i] = generateRandomString(4);
        }
        return symbols;
    }
    /**
     * Generates a random string of specified length from uppercase alphabets.
     *
     * @param length the length of the string to generate
     * @return a random string
     */
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

}
