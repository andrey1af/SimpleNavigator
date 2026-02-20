package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a weighted graph using an adjacency matrix.
 * Supports both directed and undirected graphs.
 */
public class Graph {
    private int[][] adjacencyMatrix;
    private int vertexCount;
    private boolean isDirected;

    public Graph() {}

    /**
     * Returns a string representation of the graph, including the adjacency matrix.
     *
     * @return string representation of the graph
     */
    public String toString() {
        StringBuilder graphRepresentation = new StringBuilder();
        graphRepresentation.append("Graph size: ").append(vertexCount).append("\n")
                .append(isDirected ? "Directed" : "Undirected").append(" Graph\n")
                .append("Adjacency matrix:\n");

        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                graphRepresentation.append(adjacencyMatrix[i][j]).append(" ");
            }
            graphRepresentation.append("\n");
        }
        return graphRepresentation.toString();
    }

    /**
     * Loads a graph from a file. The first line contains the number of vertices,
     * and the following lines contain the adjacency matrix.
     *
     * @param filename the path to the input file
     * @throws FileNotFoundException if the file does not exist
     */
    public void loadGraphFromFile(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(filename))) {
            vertexCount = Integer.parseInt(scanner.nextLine().trim());
            if (vertexCount < 1) {
                throw new IllegalArgumentException("Invalid vertex count: " + vertexCount);
            }
            adjacencyMatrix = new int[vertexCount][vertexCount];

            for (int i = 0; i < vertexCount; i++) {
                String line = scanner.nextLine().trim();
                String[] values = line.split("\\s+");
                for (int j = 0; j < vertexCount; j++) {
                    adjacencyMatrix[i][j] = Integer.parseInt(values[j]);
                }
            }
            isDirected = !isSymmetricMatrix();
        }
    }

    /**
     * Exports the graph to a DOT format for visualization.
     *
     * @param filename the path to the output file
     * @throws FileNotFoundException if the file cannot be created
     */
    public void exportGraphToDot(String filename) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            String graphType = isDirected ? "digraph" : "graph";
            writer.write(graphType + " graphname {\n");

            for (int i = 0; i < vertexCount; i++) {
                for (int j = (isDirected ? 0 : i); j < vertexCount; j++) {
                    if (adjacencyMatrix[i][j] != 0) {
                        StringBuilder edge = new StringBuilder();
                        if (i == j) {
                            edge.append("\t").append(i).append(isDirected ? " -> " : " -- ").append(i)
                                    .append(" [label=\"").append(adjacencyMatrix[i][j]).append("\"]");
                        } else {
                            edge.append("\t").append(i).append(isDirected ? " -> " : " -- ").append(j)
                                    .append(" [label=\"").append(adjacencyMatrix[i][j]).append("\"]");
                        }
                        writer.write(edge.append(";\n").toString());
                    }
                }
            }
            writer.write("}\n");
        }
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return the number of vertices in the graph
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Returns whether the graph is directed.
     *
     * @return true if the graph is directed, false if it is undirected
     */
    public boolean isDirected() {
        return isDirected;
    }

    /**
     * Returns a list of adjacent vertices for the specified vertex.
     *
     * @param vertex the vertex index
     * @return list of adjacent vertices
     */
    public List<Integer> getAdjacentVertices(int vertex) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("Invalid vertex index: " + vertex);
        }
        List<Integer> adjacentVertices = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            if (adjacencyMatrix[vertex][i] != 0) {
                adjacentVertices.add(i);
            }
        }
        return adjacentVertices;
    }

    /**
     * Checks if there is an edge between two specified vertices.
     *
     * @param sourceVertex the index of the source vertex
     * @param targetVertex the index of the target vertex
     * @return true if there is an edge, false otherwise
     */
    public boolean hasEdge(int sourceVertex, int targetVertex) {
        if (sourceVertex < 0 || sourceVertex >= vertexCount || targetVertex < 0 || targetVertex >= vertexCount) {
            throw new IllegalArgumentException("Invalid vertex index: " + sourceVertex + " or " + targetVertex);
        }
        return adjacencyMatrix[sourceVertex][targetVertex] != 0;
    }

    /**
     * Retrieves the weight of the edge between the specified source and target vertices.
     *
     * @param sourceVertex the index of the source vertex
     * @param targetVertex the index of the target vertex
     * @return the weight of the edge between the source and target vertices
     */
    public int getEdgeWeight(int sourceVertex, int targetVertex) {
        if (sourceVertex < 0 || sourceVertex >= vertexCount || targetVertex < 0 || targetVertex >= vertexCount) {
            throw new IllegalArgumentException("Invalid vertex index: " + sourceVertex + " or " + targetVertex);
        }
        return adjacencyMatrix[sourceVertex][targetVertex];
    }

    /**
     * Returns the minimal weight of the edge that exists it this graph, except zero
     */
    public int getMinEdgeWeight() {
        int minWeight = Integer.MAX_VALUE;
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (adjacencyMatrix[i][j] != 0 && adjacencyMatrix[i][j] < minWeight)
                    minWeight = adjacencyMatrix[i][j];
            }
        }
        return minWeight;
    }

    /**
     * Helper method to check if the adjacency matrix is symmetric, indicating an undirected graph.
     *
     * @return true if the matrix is symmetric, false otherwise
     */
    private boolean isSymmetricMatrix() {
        for (int i = 0; i < vertexCount; i++) {
            for (int j = i + 1; j < vertexCount; j++) {
                if (adjacencyMatrix[i][j] != adjacencyMatrix[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }
}
