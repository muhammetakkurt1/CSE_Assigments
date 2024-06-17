import javax.swing.*;
import java.awt.*;
import java.util.List;
/**
 * GUIVisualization class for visualizing the performance of stock operations.
 * This class extends JFrame and provides functionality to draw line  plots.
 *
 * @author Muhammet Akkurt
 * @version 1.0
 */
public class GUIVisualization extends JFrame {
    private List<Integer> dataPointsX; // List to store x-axis data points
    private List<Long> dataPointsY; // List to store y-axis data points
    private String plotType; // Type of plot ("line" or "scatter")
    private String title; // Title of the graph
    /**
     * Constructs a new GUIVisualization frame with the specified plot type, title, and data points.
     *
     * @param plotType the type of plot ("line" or "scatter")
     * @param title the title of the graph
     * @param dataPointsX the list of x-axis data points
     * @param dataPointsY the list of y-axis data points
     */
    public GUIVisualization(String plotType, String title, List<Integer> dataPointsX, List<Long> dataPointsY) {
        this.plotType = plotType; // Set the plot type
        this.title = title; // Set the title
        this.dataPointsX = dataPointsX; // Initialize x-axis data points list
        this.dataPointsY = dataPointsY; // Initialize y-axis data points list

        setTitle(title); // Set the title of the window
        setSize(800, 600); // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        setLocationRelativeTo(null); // Center the window on the screen
    }
    /**
     * Paints the component.
     *
     * @param g the Graphics object to protect
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g); // Call the superclass's paint method
        drawGraph(g); // Draw the graph
    }
    /**
     * Draws the graph based on the data points and plot type.
     *
     * @param g the Graphics object used for drawing
     */
    private void drawGraph(Graphics g) {
        int width = getWidth(); // Get the width of the window
        int height = getHeight(); // Get the height of the window
        int padding = 60; // Padding around the graph
        int labelPadding = 20; // Padding for labels

        Graphics2D g2 = (Graphics2D) g; // Cast Graphics to Graphics2D for better rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Enable anti-aliasing

        // Draw white background for the graph
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, width - 2 * padding - labelPadding, height - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // Create hatch marks and grid lines for y axis.
        int numberYDivisions = 10; // Number of divisions for the y-axis
        long maxYValue = getMaxYValue();
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = width - padding;
            int y0 = height - ((i * (height - padding * 2 - labelPadding)) / numberYDivisions + padding);
            int y1 = y0;
            if (dataPointsY.size() > 0) {
                g2.setColor(Color.LIGHT_GRAY); // Set color for grid lines
                g2.drawLine(padding + labelPadding + 1 + labelPadding, y0, x1, y1); // Draw grid line
                g2.setColor(Color.BLACK); // Set color for labels
                String yLabel = ((int) ((maxYValue * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + ""; // Generate y-axis label
                FontMetrics metrics = g2.getFontMetrics(); // Get font metrics for label width
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3); // Draw y-axis label
            }
        }

        // Create hatch marks and grid lines for x axis.
        int numberXDivisions = 10; // Number of divisions for the x-axis
        int maxXValue = getMaxXValue();
        for (int i = 0; i < numberXDivisions + 1; i++) {
            int x0 = i * (width - padding * 2 - labelPadding) / numberXDivisions + padding + labelPadding;
            int x1 = x0;
            int y0 = height - padding - labelPadding;
            int y1 = y0 - 4;
            if (dataPointsX.size() > 0) {
                g2.setColor(Color.LIGHT_GRAY); // Set color for grid lines
                g2.drawLine(x0, height - padding - labelPadding - 1 - labelPadding, x1, padding); // Draw grid line
                g2.setColor(Color.BLACK); // Set color for labels
                String xLabel = ((int) ((maxXValue * ((i * 1.0) / numberXDivisions)) * 100)) / 100.0 + ""; // Generate x-axis label
                FontMetrics metrics = g2.getFontMetrics(); // Get font metrics for label width
                int labelWidth = metrics.stringWidth(xLabel);
                g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3); // Draw x-axis label
            }
            g2.drawLine(x0, y0, x1, y1); // Draw x-axis hatch mark
        }

        // Draw axis lines.
        g2.drawLine(padding + labelPadding, height - padding - labelPadding, padding + labelPadding, padding); // y-axis
        g2.drawLine(padding + labelPadding, height - padding - labelPadding, width - padding, height - padding - labelPadding); // x-axis

        // Draw the actual graph.
        Stroke oldStroke = g2.getStroke();
        g2.setColor(Color.BLUE); // Set color for the graph
        g2.setStroke(new BasicStroke(2f)); // Set stroke for the graph

        if (plotType.equals("line")) {
            for (int i = 0; i < dataPointsX.size() - 1; i++) {
                int x1 = i * (width - padding * 2 - labelPadding) / (dataPointsX.size() - 1) + padding + labelPadding;
                int y1 = height - padding - labelPadding - (int) ((dataPointsY.get(i) * 1.0) / maxYValue * (height - padding * 2 - labelPadding));
                int x2 = (i + 1) * (width - padding * 2 - labelPadding) / (dataPointsX.size() - 1) + padding + labelPadding;
                int y2 = height - padding - labelPadding - (int) ((dataPointsY.get(i + 1) * 1.0) / maxYValue * (height - padding * 2 - labelPadding));
                g2.drawLine(x1, y1, x2, y2); // Draw line between data points
            }
        } else if (plotType.equals("scatter")) {
            for (int i = 0; i < dataPointsX.size(); i++) {
                int x = i * (width - padding * 2 - labelPadding) / (dataPointsX.size() - 1) + padding + labelPadding;
                int y = height - padding - labelPadding - (int) ((dataPointsY.get(i) * 1.0) / maxYValue * (height - padding * 2 - labelPadding));
                g2.fillOval(x - 3, y - 3, 6, 6); // Draw data point as a small circle
            }
        }

        g2.setStroke(oldStroke); // Restore original stroke
    }
    /**
     * Gets the maximum y value from the data points.
     *
     * @return the maximum y value
     */
    private long getMaxYValue() {
        long max = Long.MIN_VALUE; // Initialize max value to minimum possible value
        for (Long y : dataPointsY) {
            max = Math.max(max, y); // Find maximum y value
        }
        return max; // Return maximum y value
    }
    /**
     * Gets the maximum x value from the data points.
     *
     * @return the maximum x value
     */
    private int getMaxXValue() {
        int max = Integer.MIN_VALUE; // Initialize max value to minimum possible value
        for (Integer x : dataPointsX) {
            max = Math.max(max, x); // Find maximum x value
        }
        return max; // Return maximum x value
    }

}
