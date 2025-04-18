import java.util.Scanner;

public class node_color_sorter {

    private int numVertices;
    private int[][] graph; // this one is for the adjcentcy matrix
    private int[] colors;

    public node_color_sorter(int[][] graph, int numColors) {
        this.graph = graph;
        this.numVertices = graph.length;
        this.colors = new int[numVertices];
    }

    public boolean solve(int numColors) {
        if (!colorGraph(0, numColors)) {
            System.out.println("No solution exists.");
            return false;
        }

        System.out.println("Solution Exists: the Following are the colors:");
        for (int i = 0; i < numVertices; i++) {
            System.out.println("Node " + i + " --> Color " + colors[i]);
        }
        return true;
    }

    private boolean colorGraph(int vertex, int numColors) {  // actually asigns the colors 
        if (vertex == numVertices) {
            return true;
        }

        for (int c = 1; c <= numColors; c++) {
            if (isSafe(vertex, c)) {
                colors[vertex] = c;

                if (colorGraph(vertex + 1, numColors)) {
                    return true;
                }

                colors[vertex] = 0; // Backtrack
            }
        }

        return false;
    }

    private boolean isSafe(int vertex, int c) { // makes sure that adjacent nodes arents the same color
        for (int i = 0; i < numVertices; i++) {
            if (graph[vertex][i] == 1 && colors[i] == c) {
                return false;
            }
        }
        return true;
    }
    public static int[][] inputGraph(Scanner scanner) {
        int n;
        while (true) {
            System.out.print("Enter the number of nodes in the graph: ");
            String input = scanner.nextLine();
            try {
                n = Integer.parseInt(input);
                if (n <= 0) {
                    System.out.println("Number of nodes must be a positive integer. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }


        int[][] graph = new int[n][n];
        System.out.println("Enter the adjacency matrix row by row (e.g., 0110):");

        for (int i = 0; i < n; i++) {
            while (true) {
                System.out.print("Row " + (i + 1) + ": ");
                String line = scanner.nextLine().trim();

                if (line.length() != n) {
                    System.out.println("Each row must have exactly " + n + " digits. Please try again.");
                    continue;
                }

                boolean valid = true;
                for (int j = 0; j < n; j++) {
                    char ch = line.charAt(j);
                    if (ch != '0' && ch != '1') {
                        System.out.println("Matrix can only contain 0 or 1. Please try again.");
                        valid = false;
                        break;
                    }
                }

                if (!valid) {
                    continue;
                }

                for (int j = 0; j < n; j++) {
                    graph[i][j] = line.charAt(j) - '0';
                }
                break;
            }
        }

        return graph;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[][] graph = inputGraph(scanner);

        int m;
        while (true) {
            System.out.print("Enter the number of colors: "); // this part will probably want to be hardcoded later
            String input = scanner.next();                      // when we actually have set colors lol
            try {
                m = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        node_color_sorter gc = new node_color_sorter(graph, m);
        gc.solve(m);
    }
}
