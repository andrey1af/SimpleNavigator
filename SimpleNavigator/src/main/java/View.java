import algorithms.GraphAlgorithms;
import algorithms.GraphAlgorithmsImpl;
import data.TsmResult;
import graph.Graph;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.FileNotFoundException;
import java.util.*;

public class View {
    private final Graph graph = new Graph();
    private static final Scanner scanner = new Scanner(System.in);
    private final GraphAlgorithms graphAlgorithms = new GraphAlgorithmsImpl();
    boolean needExit = false;
    @EqualsAndHashCode
    @ToString
    private static final class MenuItem {
        private final String description;
        private final Runnable action;

        @Getter
        @Setter
        private boolean isVisible;

        private MenuItem(String description, Runnable action, boolean isVisible) {
            this.description = description;
            this.action = action;
            this.isVisible = isVisible;
        }

        public String description() {
            return description;
        }

        public Runnable action() {
            return action;
        }


    }
    private final Map<Integer, MenuItem> menuItems = new LinkedHashMap<>();
    public View() {
        MenuItem openGraphItem = new MenuItem("Open graph", this::openGraph, true);
        MenuItem DFSOnGraphItem = new MenuItem("Depth first search", this::depthFirstSearch, false);
        MenuItem BFSOnGraphItem = new MenuItem("Breadth first search", this::breadthFirstSearch, false);
        MenuItem shortestPathBetweenTwoVertexesItem = new MenuItem("Shortest path between two vertexes", this::shortestPathBetweenTwoVertexes, false);
        MenuItem shortestPathBetweenAllVertexesItem = new MenuItem("Shortest path between all vertexes", this::shortestPathBetweenAllVertexes, false);
        MenuItem minimumSpanningTreeItem = new MenuItem("Minimum spanning Tree", this::minimumSpanningTree, false);
        MenuItem salesmanProblemItem = new MenuItem("Minimum spanning Tree", this::salesmanProblem, false);
        MenuItem exitProgramItem = new MenuItem("Exit program", this::exitProgram, true);
        menuItems.put(1, openGraphItem);
        menuItems.put(2, DFSOnGraphItem);
        menuItems.put(3, BFSOnGraphItem);
        menuItems.put(4, shortestPathBetweenTwoVertexesItem);
        menuItems.put(5, shortestPathBetweenAllVertexesItem);
        menuItems.put(6, minimumSpanningTreeItem);
        menuItems.put(7, salesmanProblemItem);
        menuItems.put(8, exitProgramItem);
    }
    public void showMenu() {

        while(!this.needExit) {
            printMenu();
            Integer userChoice = readInt();
            runUserAction(userChoice);
        }
    }

    private void runUserAction(Integer userChoice) {
        MenuItem item = menuItems.get(userChoice);
        if(item == null) {
            String errorString = String.format("No menu item: %d", userChoice);
            System.out.println(errorString);
            return;
        }
        if(!item.isVisible) {
            System.out.println("Graph is not loaded. First load graph.");
        }
        item.action.run();
    }

    private void printMenu() {
        for(Map.Entry<Integer, MenuItem> entry : menuItems.entrySet()) {
            MenuItem item = entry.getValue();
            if(item.isVisible()) {
                System.out.printf("%d. %s.\n", entry.getKey(), item.description());
            }
        }
    }


    private void openGraph() {
        try {
            String path = scanner.next();
            graph.loadGraphFromFile(path);
            System.out.println("Graph loaded");
            enableGraphItems(true);

        } catch (FileNotFoundException e) {
            enableGraphItems(false);
            System.out.println("Graph not loaded");
        }
    }

    private void enableGraphItems(boolean enable) {
        for(Map.Entry<Integer, MenuItem> entry : menuItems.entrySet()) {
            if (entry.getKey() > 1 && entry.getKey() < 8) {
                MenuItem item = entry.getValue();
                item.setVisible(enable);
            }
        }
    }

    private void depthFirstSearch() {
        try {
            List<Integer> vertexes = graphAlgorithms.depthFirstSearch(graph, readInt());
            System.out.println(vertexes.toString());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void breadthFirstSearch() {
        try {
            List<Integer> vertexes = graphAlgorithms.breadthFirstSearch(graph, readInt());
            for(Integer vertex : vertexes) {
                System.out.println(vertex.toString());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void shortestPathBetweenTwoVertexes() {
        try {
            int length = graphAlgorithms.getShortestPathBetweenVertices(graph, readInt(), readInt());
            System.out.println("Length is: " + length);
        } catch (Exception e) {
            System.out.println("Unknown error:" + e.getMessage());
        }
    }

    private void shortestPathBetweenAllVertexes() {
        try {
            int[][] lengths = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);
            for(int[] row : lengths) {
                for(int length : row) {
                    System.out.print(length + " ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Unknown error:" + e.getMessage());
        }
    }

    private void minimumSpanningTree() {
        try {
            int[][] lengths = graphAlgorithms.getLeastSpanningTree(graph);
            for(int[] row : lengths) {
                for(int length : row) {
                    System.out.println("Length is: " + length);
                }
            }

        } catch (Exception e) {
            System.out.println("Unknown error:" + e.getMessage());
        }
    }

    private void salesmanProblem() {
        try {
            TsmResult result = graphAlgorithms.solveTravelingSalesmanProblem(graph);
            System.out.println("Vertexes: " + Arrays.toString(result.getVertices()));
            System.out.println("Distance:" + result.getDistance());

        } catch (Exception e) {
            System.out.println("Unknown error:" + e.getMessage());
        }
    }

    private void exitProgram() {
        needExit = true;
    }

    private static int readInt() {
        while(true) {
            if(scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                System.out.println("Could not parse a number. Please, try again");
                scanner.nextLine();
            }
        }
    }
}
