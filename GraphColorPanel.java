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

        // Top inputs
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Number of Nodes:"));
        nodeField = new JTextField();
        inputPanel.add(nodeField);

        inputPanel.add(new JLabel("Number of Colors:"));
        colorField = new JTextField();
        inputPanel.add(colorField);

        inputPanel.add(new JLabel("Adjacency Matrix (rows separated by newlines):"));
        matrixInput = new JTextArea(5, 20);
        inputPanel.add(new JScrollPane(matrixInput));

        add(inputPanel, BorderLayout.NORTH);

        // Button
        JButton solveButton = new JButton("Color Graph");
        add(solveButton, BorderLayout.CENTER);

        // Result display
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
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
