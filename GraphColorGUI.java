import javax.swing.*;

public class GraphColorGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Node Color Sorter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new GraphColorPanel()); // this will hold all UI
            frame.pack();
            frame.setVisible(true);
        });
    }
}

