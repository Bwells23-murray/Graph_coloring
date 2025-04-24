import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphColorPanel extends JPanel {

    private JTextField nodeField;
    private JTextField colorField;
    private JTextArea matrixInput;
    private JTextArea resultArea;

    public GraphColorPanel() {
        setLayout(new BorderLayout());

        // Set the font for all components
        Font font = new Font("Arial", Font.PLAIN, 14);

        // Top inputs
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JLabel nodeLabel = new JLabel("Number of Nodes:");
        nodeLabel.setFont(font); // Apply font to labels
        inputPanel.add(nodeLabel);
        nodeField = new JTextField();
        nodeField.setFont(font); // Apply font to text fields
        inputPanel.add(nodeField);

        JLabel colorLabel = new JLabel("Number of Colors:");
        colorLabel.setFont(font); // Apply font to labels
        inputPanel.add(colorLabel);
        colorField = new JTextField();
        colorField.setFont(font); // Apply font to text fields
        inputPanel.add(colorField);

        JLabel matrixLabel = new JLabel("Adjacency Matrix:");
        matrixLabel.setFont(font); // Apply font to labels
        inputPanel.add(matrixLabel);
        matrixInput = new JTextArea(5, 20);
        matrixInput.setFont(font); // Apply font to text areas
        inputPanel.add(new JScrollPane(matrixInput));

        add(inputPanel, BorderLayout.NORTH);

        // Button
        JButton solveButton = new JButton("Solve");
        solveButton.setFont(font); // Apply font to button
        solveButton.setPreferredSize(new Dimension(200, 40));
        solveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        solveButton.setBackground(new Color(100, 150, 255)); // Light Blue
        solveButton.setForeground(Color.WHITE);
        solveButton.setBorder(BorderFactory.createLineBorder(new Color(60, 90, 150), 2));
        
        // Add hover effect
        solveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                solveButton.setBackground(new Color(80, 120, 255)); // Slightly darker blue on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                solveButton.setBackground(new Color(100, 150, 255)); // Original color when not hovered
            }
        });

        add(solveButton, BorderLayout.CENTER);

        // Result display
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(font); // Apply font to text area
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Action listener
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runColoring();
            }
        });
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

            if(!node_color_sorter.isValidGraph(graph)) {
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
    
                // Add drawing canvas below the result
                JFrame canvasFrame = new JFrame("Graph Visualization");
                canvasFrame.setContentPane(new GraphCanvas(graph, assignedColors));
                canvasFrame.pack();
                canvasFrame.setVisible(true);
            }
        } catch (Exception ex) {
            resultArea.setText("Error: Make sure all inputs are valid.\n");
        }
    }    
}
