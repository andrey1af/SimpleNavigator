package algorithms;


import graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class GraphAlgorithmsImplTest {
    @TempDir
    Path tempDir;
    private static final String FILE_FOLDER = "";
    private Graph graph;
    private final GraphAlgorithms graphAlgorithms = new GraphAlgorithmsImpl();

    @BeforeEach
    public void setUp() {
        graph = new Graph();
    }

    private String getResourcePath(String filename) {
        URL resource = getClass().getClassLoader().getResource(filename);
        if (resource == null) {
            throw new RuntimeException("Resource not found: " + filename);
        }
        return resource.getPath();
    }
    @Test
    void depthFirstSearch() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_1.txt"));
            List<Integer> result = graphAlgorithms.depthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void depthFirstSearch_2() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_2_selfLoop.txt"));
            List<Integer> result = graphAlgorithms.depthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 1));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void depthFirstSearch_5() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_5_weighted_undirected.txt"));
            List<Integer> result = graphAlgorithms.depthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 4, 3, 2, 1));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void depthFirstSearch_6() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_6_weighted_undirected_disconnected.txt"));
            List<Integer> result = graphAlgorithms.depthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 2, 3, 1));
            result = graphAlgorithms.depthFirstSearch(graph, 5);
            assertIterableEquals(result, List.of(5, 4));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void depthFirstSearch_10() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_10_notSolvableByTSP.txt"));
            List<Integer> result = graphAlgorithms.depthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 2, 7, 6, 9, 3, 8, 5, 1));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void breadthFirstSearch() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_1.txt"));
            List<Integer> result = graphAlgorithms.breadthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void breadthFirstSearch_2() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_2_selfLoop.txt"));
            List<Integer> result = graphAlgorithms.breadthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 1));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void breadthFirstSearch_5() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_5_weighted_undirected.txt"));
            List<Integer> result = graphAlgorithms.breadthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 1, 2, 3, 4));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void breadthFirstSearch_6() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_6_weighted_undirected_disconnected.txt"));
            List<Integer> result = graphAlgorithms.breadthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 1, 2, 3));
            result = graphAlgorithms.breadthFirstSearch(graph, 5);
            assertIterableEquals(result, List.of(5, 4));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void breadthFirstSearch_10() {
        try {
            graph.loadGraphFromFile(getResourcePath("graph_10_notSolvableByTSP.txt"));
            List<Integer> result = graphAlgorithms.breadthFirstSearch(graph, 0);
            assertIterableEquals(result, List.of(0, 2, 1, 7, 6, 8, 3, 5, 9));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getShortestPathBetweenVertices_withSameVertex_shouldReturnZero() throws IOException {
        String fileContent = """
                3
                0 1 2
                1 0 3
                2 3 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int distance = graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 0);

        assertEquals(0, distance);
    }

    @Test
    void getShortestPathBetweenVertices_withDirectPath_shouldReturnDirectWeight() throws IOException {
        String fileContent = """
                3
                0 5 0
                0 0 10
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int distance = graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 1);

        assertEquals(5, distance);
    }

    @Test
    void getShortestPathBetweenVertices_withComplexGraph_shouldReturnOptimalPath() throws IOException {
        String fileContent = """
                5
                0 4 2 0 0
                0 0 0 5 0
                0 1 0 8 10
                0 0 0 0 2
                0 0 0 3 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int distance = graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 4);

        assertEquals(10, distance);
    }

    @Test
    void getShortestPathBetweenVertices_withNoPath_shouldThrowException() throws IOException {
        String fileContent = """
                3
                0 1 0
                0 0 0
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 2));
    }

    @Test
    void getShortestPathBetweenVertices_withInvalidVertices_shouldThrowException() throws IOException {
        String fileContent = """
                3
                0 1 2
                1 0 3
                2 3 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graphAlgorithms.getShortestPathBetweenVertices(graph, -1, 1));
        assertThrows(IllegalArgumentException.class, () ->
                graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 10));
    }

    @Test
    void getShortestPathBetweenVertices_withSingleVertex_shouldReturnZero() throws IOException {
        String fileContent = """
                1
                0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int distance = graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 0);

        assertEquals(0, distance);
    }

    @Test
    void getShortestPathBetweenVertices_withLargeWeights_shouldHandleCorrectly() throws IOException {
        String fileContent = """
                3
                0 999999 0
                0 0 1000000
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int distance = graphAlgorithms.getShortestPathBetweenVertices(graph, 0, 2);

        assertEquals(1999999, distance);
    }

    @Test
    void getShortestPathsBetweenAllVertices_withSimpleGraph_shouldReturnCorrectMatrix() throws IOException {
        String fileContent = """
                3
                0 1 4
                1 0 2
                4 2 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int[][] distances = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);

        assertEquals(0, distances[0][0]);
        assertEquals(1, distances[0][1]);
        assertEquals(3, distances[0][2]);
        assertEquals(1, distances[1][0]);
        assertEquals(0, distances[1][1]);
        assertEquals(2, distances[1][2]);
        assertEquals(3, distances[2][0]);
        assertEquals(2, distances[2][1]);
        assertEquals(0, distances[2][2]);
    }

    @Test
    void getShortestPathsBetweenAllVertices_withDirectedGraph_shouldThrowException() throws IOException {
        String fileContent = """
                3
                0 5 0
                0 0 3
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graphAlgorithms.getShortestPathsBetweenAllVertices(graph));
    }

    @Test
    void getShortestPathsBetweenAllVertices_withFullyConnectedDirectedGraph_shouldReturnCorrectMatrix() throws IOException {
        String fileContent = """
                3
                0 5 8
                10 0 3
                7 6 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int[][] distances = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);

        assertEquals(0, distances[0][0]);
        assertEquals(5, distances[0][1]);
        assertEquals(8, distances[0][2]);
        assertEquals(10, distances[1][0]);
        assertEquals(0, distances[1][1]);
        assertEquals(3, distances[1][2]);
        assertEquals(7, distances[2][0]);
        assertEquals(6, distances[2][1]);
        assertEquals(0, distances[2][2]);
    }

    @Test
    void getShortestPathsBetweenAllVertices_withSingleVertex_shouldReturnZeroMatrix() throws IOException {
        String fileContent = """
                1
                0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int[][] distances = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);

        assertEquals(1, distances.length);
        assertEquals(0, distances[0][0]);
    }

    @Test
    void getShortestPathsBetweenAllVertices_withDisconnectedGraph_shouldThrowException() throws IOException {
        String fileContent = """
                3
                0 1 0
                1 0 0
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graphAlgorithms.getShortestPathsBetweenAllVertices(graph));
    }

    @Test
    void getShortestPathsBetweenAllVertices_withNoEdges_shouldThrowException() throws IOException {
        String fileContent = """
                3
                0 0 0
                0 0 0
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graphAlgorithms.getShortestPathsBetweenAllVertices(graph));
    }

    @Test
    void getShortestPathsBetweenAllVertices_shouldBeSymmetricForUndirectedGraph() throws IOException {
        String fileContent = """
                3
                0 5 10
                5 0 3
                10 3 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int[][] distances = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(distances[i][j], distances[j][i],
                        "Distance from " + i + " to " + j + " should equal distance from " + j + " to " + i);
            }
        }
    }

    @Test
    void getShortestPathsBetweenAllVertices_withLargeWeights_shouldHandleCorrectly() throws IOException {
        String fileContent = """
                3
                0 1000 2000
                1000 0 500
                2000 500 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int[][] distances = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);

        assertEquals(1000, distances[0][1]);
        assertEquals(1500, distances[0][2]);
        assertEquals(500, distances[1][2]);
    }

    @Test
    void getShortestPathsBetweenAllVertices_withTwoVertices_shouldWorkCorrectly() throws IOException {
        String fileContent = """
                2
                0 5
                5 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        int[][] distances = graphAlgorithms.getShortestPathsBetweenAllVertices(graph);

        assertEquals(0, distances[0][0]);
        assertEquals(5, distances[0][1]);
        assertEquals(5, distances[1][0]);
        assertEquals(0, distances[1][1]);
    }

    // Helpers
    private Path createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test_graph_" + System.nanoTime() + ".txt");
        Files.writeString(file, content);
        return file;
    }
}