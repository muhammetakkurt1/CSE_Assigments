/**
 * Stock class represents a stock with a symbol, price, volume, and market capitalization.
 * It provides constructors, getters, setters, and a toString method for displaying stock information.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class Stock {
    private String symbol;
    private double price;
    private long volume;
    private long marketCap;
    /**
     * Constructs a new Stock with the specified symbol, price, volume, and market capitalization.
     *
     * @param symbol the stock symbol
     * @param price the stock price
     * @param volume the stock volume
     * @param marketCap the stock market capitalization
     */
    public Stock(String symbol, double price, long volume, long marketCap) {
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.marketCap = marketCap;
    }
    /**
     * Gets the stock symbol.
     *
     * @return the stock symbol
     */
    public String getSymbol() {
        return symbol;
    }
    /**
     * Sets the stock symbol.
     *
     * @param symbol the stock symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    /**
     * Gets the stock price.
     *
     * @return the stock price
     */
    public double getPrice() {
        return price;
    }
    /**
     * Sets the stock price.
     *
     * @param price the stock price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the stock volume.
     *
     * @return the stock volume
     */
    public long getVolume() {
        return volume;
    }
    /**
     * Sets the stock volume.
     *
     * @param volume the stock volume
     */
    public void setVolume(long volume) {
        this.volume = volume;
    }
    /**
     * Gets the stock market capitalization.
     *
     * @return the stock market capitalization
     */
    public long getMarketCap() {
        return marketCap;
    }
    /**
     * Sets the stock market capitalization.
     *
     * @param marketCap the stock market capitalization
     */
    public void setMarketCap(long marketCap) {
        this.marketCap = marketCap;
    }
    /**
     * Returns a string representation of the stock.
     *
     * @return a string representation of the stock
     */
    @Override
    public String toString() {
        return "Stock [symbol=" + symbol + ", price=" + price + ", volume=" + volume + ", marketCap=" + marketCap + "]";
    }
}
