import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraphColorPanel extends JPanel {

    private JTextField nodeField;
    private JTextField colorField;
    private JTextArea matrixInput;
    private JTextArea resultArea;

    private JFrame canvasFrame = null; // Track current graph window

    public GraphColorPanel() {
        setLayout(new BorderLayout());

        // Top inputs
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Use vertical layout for inputs
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the panel

        // Node field
        JPanel nodePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nodePanel.add(new JLabel("Number of Nodes:"));
        nodeField = new JTextField(10);
        nodeField.setToolTipText("Enter the number of nodes in the graph");
        nodeField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        nodePanel.add(nodeField);
        inputPanel.add(nodePanel);

        // Color field
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.add(new JLabel("Number of Colors:"));
        colorField = new JTextField(10);
        colorField.setToolTipText("Enter the number of colors to be used for the nodes");
        colorField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        colorPanel.add(colorField);
        inputPanel.add(colorPanel);

        // Matrix input
        JPanel matrixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        matrixPanel.add(new JLabel("Adjacency Matrix:"));
        matrixInput = new JTextArea(5, 20);
        matrixInput.setToolTipText("Enter the adjacency matrix for the graph (each row on a new line)");
        matrixInput.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        matrixPanel.add(new JScrollPane(matrixInput));
        inputPanel.add(matrixPanel);

        // Add the inputPanel to the top part
        add(inputPanel, BorderLayout.NORTH);

        // Solve Button
        JButton solveButton = new JButton("Solve");
        solveButton.setPreferredSize(new Dimension(100, 40));
        solveButton.setBackground(new Color(50, 150, 255)); // Blue background
        solveButton.setForeground(Color.WHITE); // White text
        solveButton.setFocusPainted(false); // Remove focus outline
        solveButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Set button font

        // Add MouseListener for hover effect
        solveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                solveButton.setBackground(new Color(70, 180, 255)); // Lighter blue when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                solveButton.setBackground(new Color(50, 150, 255)); // Original blue when not hovered
            }
        });

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runColoring();
            }
        });
        add(solveButton, BorderLayout.CENTER);

        // Result display
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void runColoring() {
        try {
            int nodes = Integer.parseInt(nodeField.getText().trim());
            int colors = Integer.parseInt(colorField.getText().trim());

            String[] rows = matrixInput.getText().trim().split("\n");
            int[][] graph = new int[nodes][nodes];
            for (int i = 0; i < nodes; i++) {
                String row = rows[i].trim();
                for (int j = 0; j < nodes; j++) {
                    graph[i][j] = Character.getNumericValue(row.charAt(j));
                }
            }

            if (!node_color_sorter.isValidGraph(graph)) {
                throw new Exception("Invalid Graph: the adjacency matrix is not symmetric ");
            }

            node_color_sorter sorter = new node_color_sorter(graph, colors);
            resultArea.setText("");

            if (!sorter.solve(colors)) {
                resultArea.append("No solution exists.\n");
            } else {
                int[] assignedColors = sorter.getColors();
                for (int i = 0; i < nodes; i++) {
                    resultArea.append("Node " + i + " --> Color " + assignedColors[i] + "\n");
                }

                // Add drawing canvas
                // Close old canvas if it exists
                if (canvasFrame != null && canvasFrame.isDisplayable()) {
                    canvasFrame.dispose();
                }

                // Create new canvas window
                canvasFrame = new JFrame("Graph Visualization");
                canvasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                canvasFrame.setContentPane(new GraphCanvas(graph, assignedColors));
                canvasFrame.pack();
                canvasFrame.setLocationRelativeTo(null); // Center on screen
                canvasFrame.setVisible(true);

            }
        } catch (Exception ex) {
            resultArea.setText("Error: Make sure all inputs are valid.\n");
        }
    }
}
