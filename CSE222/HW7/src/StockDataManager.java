/**
 * StockDataManager class for managing stock information using an AVL tree.
 * This class supports adding, updating, removing, and searching stocks.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class StockDataManager {
    private AVLTree avlTree;
    /**
     * Constructs a new StockDataManager with an empty AVL tree.
     */
    public StockDataManager() {
        avlTree = new AVLTree();
    }
    /**
     * Adds a new stock or updates an existing stock in the AVL tree.
     * If the stock already exists, its price, volume, and market capitalization are updated.
     * Otherwise, a new stock is added to the AVL tree.
     *
     * @param symbol the stock symbol
     * @param price the stock price
     * @param volume the stock volume
     * @param marketCap the stock market capitalization
     */
    public void addOrUpdateStock(String symbol, double price, long volume, long marketCap) {
        Stock existingStock = avlTree.search(symbol);
        if (existingStock != null) {
            existingStock.setPrice(price);
            existingStock.setVolume(volume);
            existingStock.setMarketCap(marketCap);
        } else {
            Stock newStock = new Stock(symbol, price, volume, marketCap);
            avlTree.insert(newStock);
        }
    }
    /**
     * Removes a stock from the AVL tree by its symbol.
     *
     * @param symbol the symbol of the stock to be removed
     */
    public void removeStock(String symbol) {
        avlTree.delete(symbol);
    }
    /**
     * Searches for a stock in the AVL tree by its symbol.
     *
     * @param symbol the symbol of the stock to search for
     * @return the found stock, or null if not found
     */
    public Stock searchStock(String symbol) {
        return avlTree.search(symbol);
    }
    /**
     * Updates the details of an existing stock in the AVL tree.
     * If the stock is found, its symbol, price, volume, and market capitalization are updated.
     * If the stock symbol changes, the stock is reinserted with the new symbol.
     *
     * @param symbol the current symbol of the stock
     * @param newSymbol the new symbol of the stock
     * @param newPrice the new price of the stock
     * @param newVolume the new volume of the stock
     * @param newMarketCap the new market capitalization of the stock
     */
    public void updateStock(String symbol, String newSymbol, double newPrice, long newVolume, long newMarketCap) {
        Stock stock = avlTree.search(symbol);
        if (stock != null) {
            stock.setSymbol(newSymbol);
            stock.setPrice(newPrice);
            stock.setVolume(newVolume);
            stock.setMarketCap(newMarketCap);
            if (!symbol.equals(newSymbol)) {
                avlTree.delete(symbol);
                avlTree.insert(stock);
            }
        }
    }
}
