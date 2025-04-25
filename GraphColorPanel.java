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
    private int[][] lastGraph = null;   // Stores last graph for replay
    private int[] lastColors = null;    // Stores last coloring result

    public GraphColorPanel() {
        setLayout(new BorderLayout());

        // Top inputs
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        add(inputPanel, BorderLayout.NORTH);

        // Buttons panel
        JButton solveButton = new JButton("Solve");
        JButton resetButton = new JButton("Reset");
        JButton replayButton = new JButton("Replay");
        replayButton.setEnabled(false);

        solveButton.setPreferredSize(new Dimension(100, 40));
        solveButton.setBackground(new Color(50, 150, 255));
        solveButton.setForeground(Color.WHITE);
        solveButton.setFocusPainted(false);
        solveButton.setFont(new Font("Arial", Font.PLAIN, 14));
        solveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                solveButton.setBackground(new Color(70, 180, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                solveButton.setBackground(new Color(50, 150, 255));
            }
        });
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runColoring(replayButton);
            }
        });

        resetButton.setPreferredSize(new Dimension(100, 40));
        resetButton.setBackground(new Color(220, 53, 69));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resetButton.setBackground(new Color(255, 80, 95));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                resetButton.setBackground(new Color(220, 53, 69));
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
                replayButton.setEnabled(false);
            }
        });

        replayButton.setPreferredSize(new Dimension(100, 40));
        replayButton.setBackground(new Color(100, 200, 100));
        replayButton.setForeground(Color.WHITE);
        replayButton.setFocusPainted(false);
        replayButton.setFont(new Font("Arial", Font.PLAIN, 14));
        replayButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (replayButton.isEnabled()) replayButton.setBackground(new Color(120, 220, 120));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (replayButton.isEnabled()) replayButton.setBackground(new Color(100, 200, 100));
            }
        });
        replayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastGraph != null && lastColors != null) {
                    if (canvasFrame != null && canvasFrame.isDisplayable()) {
                        canvasFrame.dispose();
                    }
                    canvasFrame = new JFrame("Graph Visualization");
                    canvasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    canvasFrame.setContentPane(new GraphCanvas(lastGraph, lastColors));
                    canvasFrame.pack();
                    canvasFrame.setLocationRelativeTo(null);
                    canvasFrame.setVisible(true);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(solveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(replayButton);
        add(buttonPanel, BorderLayout.CENTER);

        // Result display
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void runColoring(JButton replayButton) {
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
                throw new Exception("Invalid Graph: the adjacency matrix is not symmetric.");
            }

            node_color_sorter sorter = new node_color_sorter(graph, colors);
            resultArea.setText("");

            if (!sorter.solve(colors)) {
                resultArea.append("No solution exists.\n");
                replayButton.setEnabled(false);
            } else {
                int[] assignedColors = sorter.getColors();
                for (int i = 0; i < nodes; i++) {
                    resultArea.append("Node " + i + " --> Color " + assignedColors[i] + "\n");
                }

                lastGraph = graph;
                lastColors = assignedColors;
                replayButton.setEnabled(true);

                if (canvasFrame != null && canvasFrame.isDisplayable()) {
                    canvasFrame.dispose();
                }

                canvasFrame = new JFrame("Graph Visualization");
                canvasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                canvasFrame.setContentPane(new GraphCanvas(graph, assignedColors));
                canvasFrame.pack();
                canvasFrame.setLocationRelativeTo(null);
                canvasFrame.setVisible(true);
            }
        } catch (Exception ex) {
            resultArea.setText("Error: Make sure all inputs are valid.\n");
            replayButton.setEnabled(false);
        }
    }

    private void resetFields() {
        nodeField.setText("");
        colorField.setText("");
        matrixInput.setText("");
        resultArea.setText("");

        if (canvasFrame != null && canvasFrame.isDisplayable()) {
            canvasFrame.dispose();
            canvasFrame = null;
        }
    }
}
