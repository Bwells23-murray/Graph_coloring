import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GraphColorPanel extends JPanel 
{

    private JTextField nodeField;
    private JTextField colorField;
    private JTextArea matrixInput;
    private JTextArea resultArea;
    private JFrame canvasFrame = null;
    private int[][] lastGraph = null;
    private int[] lastColors = null;

    public GraphColorPanel() 
    {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 245, 255));

        // Title
        JLabel titleLabel = new JLabel("Node Color Graph");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(20, 80, 160));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Input 
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(230, 245, 255));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        nodeField = createStyledField("Number of Nodes:", inputPanel);
        colorField = createStyledField("Number of Colors:", inputPanel);

        // Matrix input
        JPanel matrixPanel = new JPanel(new BorderLayout());
        matrixPanel.setBackground(new Color(230, 245, 255));
        matrixPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        JLabel matrixLabel = new JLabel("Adjacency Matrix:");
        matrixLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        matrixPanel.add(matrixLabel, BorderLayout.NORTH);
        matrixInput = new JTextArea(5, 20);
        matrixInput.setLineWrap(true);
        matrixInput.setFont(new Font("Segoe UI", Font.PLAIN, 16)); 
        matrixInput.setBorder(BorderFactory.createLineBorder(new Color(180, 210, 240), 2));
        matrixPanel.add(new JScrollPane(matrixInput), BorderLayout.CENTER);
        inputPanel.add(matrixPanel);

        add(inputPanel, BorderLayout.WEST);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(230, 245, 255)); 
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 10, 20, 10));

        JButton solveButton = createBigBlueButton("Solve");
        JButton resetButton = createBigRedButton("Reset");
        JButton replayButton = createBigGreenButton("Replay");
        replayButton.setEnabled(true);

        solveButton.addActionListener(e -> runColoring(replayButton));
        resetButton.addActionListener(e -> {
            resetFields();
            replayButton.setEnabled(true);
        });
        replayButton.addActionListener(e -> replayGraph());

        buttonPanel.add(solveButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(resetButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(replayButton);
        buttonPanel.add(Box.createVerticalStrut(15));

        buttonPanel.setPreferredSize(new Dimension(200, 300));
        add(buttonPanel, BorderLayout.EAST);

        // Result display
        resultArea = new JTextArea(10, 30);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createLineBorder(new Color(180, 210, 240), 2));
        resultArea.setBackground(new Color(245, 250, 255));
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private JTextField createStyledField(String label, JPanel container) 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 245, 255));
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));  
        field.setBorder(BorderFactory.createLineBorder(new Color(180, 210, 240), 2));
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        container.add(panel);
        return field;
    }

    private JButton createBigBlueButton(String text) 
    {
        JButton button = new JButton(text);
        button.setBackground(new Color(60, 130, 230));
        button.setForeground(Color.WHITE);
        styleBigButton(button, new Color(80, 160, 255));
        return button;
    }

    private JButton createBigRedButton(String text)
    {
        JButton button = new JButton(text);
        button.setBackground(new Color(220, 70, 90));
        button.setForeground(Color.WHITE);
        styleBigButton(button, new Color(240, 100, 120));
        return button;
    }

    private JButton createBigGreenButton(String text) 
    {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 190, 130));
        button.setForeground(Color.WHITE);
        styleBigButton(button, new Color(90, 220, 160));
        return button;
    }

    private void styleBigButton(JButton button, Color hoverColor) 
    {
        button.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 250), 1));
        
        
        button.setMaximumSize(new Dimension(200, 80));
        button.setAlignmentX(CENTER_ALIGNMENT); 
    
        button.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { button.setBackground(((JButton)e.getSource()).getBackground()); }
        });
    }
    

    private void runColoring(JButton replayButton) 
    {
        try {
            int nodes = Integer.parseInt(nodeField.getText().trim());
            int colors = Integer.parseInt(colorField.getText().trim());

            String[] rows = matrixInput.getText().trim().split("\n");
            int[][] graph = new int[nodes][nodes];

            for (int i = 0; i < nodes; i++) 
            {
                String row = rows[i].trim();
                for (int j = 0; j < nodes; j++) 
                {

                    graph[i][j] = Character.getNumericValue(row.charAt(j));
                }
            }

            if (!node_color_sorter.isValidGraph(graph)) 
            {
                throw new Exception("Invalid Graph");
            }

            node_color_sorter sorter = new node_color_sorter(graph, colors);
            resultArea.setText("");

            if (!sorter.solve(colors))
            {
                resultArea.setText("No solution exists.");
                replayButton.setEnabled(false);
            } else {
                int[] assignedColors = sorter.getColors();
                StringBuilder result = new StringBuilder("Coloring Result:\n");

                for (int i = 0; i < nodes; i++) 
                {
                    result.append("Node ").append(i).append(" → Color ").append(assignedColors[i]).append("\n");
                }
                resultArea.setText(result.toString());

                lastGraph = graph;
                lastColors = assignedColors;
                replayButton.setEnabled(true);

                if (canvasFrame != null && canvasFrame.isDisplayable()) canvasFrame.dispose();
                canvasFrame = new JFrame("Graph Visualization");
                canvasFrame.setContentPane(new GraphCanvas(graph, assignedColors));
                canvasFrame.pack();
                canvasFrame.setLocationRelativeTo(null);
                canvasFrame.setVisible(true);
            }
        } catch (Exception ex) 
        {
            resultArea.setText("Error: Check that all inputs are filled and valid.");
            replayButton.setEnabled(false);
        }
    }

    private void replayGraph() 
    {
        if (lastGraph != null && lastColors != null)
        {
            if (canvasFrame != null && canvasFrame.isDisplayable()) canvasFrame.dispose();
            canvasFrame = new JFrame("Graph Visualization");
            canvasFrame.setContentPane(new GraphCanvas(lastGraph, lastColors));
            canvasFrame.pack();
            canvasFrame.setLocationRelativeTo(null);
            canvasFrame.setVisible(true);
        }
    }

    private void resetFields() 
    {
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