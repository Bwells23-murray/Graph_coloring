import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GraphCanvas extends JPanel 
{
    private int[][] graph;
    private int[] colors;
    private final Color[] colorPalette = new Color[] 
    {
            new Color(255, 179, 186), // Baby Pink
            new Color(255, 223, 186), // Peach
            new Color(255, 255, 186), // Lemon
            new Color(186, 255, 201), // Mint
            new Color(186, 225, 255), // Baby Blue
            new Color(218, 186, 255), // Lavender
            new Color(255, 186, 245) // Cotton Candy
    };

    private int currentStep = 0; // Animation step counter
    private Timer animationTimer;

    public GraphCanvas(int[][] graph, int[] colors) {
        this.graph = graph;
        this.colors = colors;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);

        animationTimer = new Timer(500, e -> {
            currentStep++;
            if (currentStep > graph.length) {
                animationTimer.stop();
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        int n = graph.length;
        int radius = 20;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int circleRadius = 200;

        // Calculate node positions around a circle
        Point[] nodePositions = new Point[n];
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            int x = centerX + (int) (circleRadius * Math.cos(angle));
            int y = centerY + (int) (circleRadius * Math.sin(angle));
            nodePositions[i] = new Point(x, y);
        }

        // Draw edges
        g2.setStroke(new BasicStroke(1.25f));
        g.setColor(Color.BLACK);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph[i][j] == 1) {
                    g.drawLine(nodePositions[i].x, nodePositions[i].y, nodePositions[j].x, nodePositions[j].y);
                }
            }
        }

        // Draw nodes with animation up to currentStep
        for (int i = 0; i < n; i++) {
            int x = nodePositions[i].x - radius;
            int y = nodePositions[i].y - radius;

            if (i < currentStep) {
                int colorIndex = (colors[i] - 1) % colorPalette.length;
                g.setColor(colorPalette[colorIndex]);
            } else {
                g.setColor(Color.LIGHT_GRAY); // Not yet colored
            }

            g.fillOval(x, y, 2 * radius, 2 * radius);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, 2 * radius, 2 * radius);
            g.drawString("N" + i, x + radius - 6, y + radius + 5);
        }
    }
}