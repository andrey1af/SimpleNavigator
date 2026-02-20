package graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    private Graph graph;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        graph = new Graph();
    }

    @Test
    void loadGraphFromFile_withUndirectedGraph_shouldLoadCorrectly() throws IOException {
        String fileContent = """
                3
                0 1 2
                1 0 3
                2 3 0
                """;
        Path testFile = createTempFile(fileContent);

        graph.loadGraphFromFile(testFile.toString());

        assertEquals(3, graph.getVertexCount());
        assertFalse(graph.isDirected());
        assertTrue(graph.hasEdge(0, 1));
        assertEquals(1, graph.getEdgeWeight(0, 1));
        assertEquals(3, graph.getEdgeWeight(1, 2));
    }

    @Test
    void loadGraphFromFile_withDirectedGraph_shouldLoadCorrectly() throws IOException {
        String fileContent = """
                3
                0 1 0
                0 0 2
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);

        graph.loadGraphFromFile(testFile.toString());

        assertEquals(3, graph.getVertexCount());
        assertTrue(graph.isDirected());
        assertTrue(graph.hasEdge(0, 1));
        assertFalse(graph.hasEdge(1, 0));
    }

    @Test
    void loadGraphFromFile_withSelfLoops_shouldLoadCorrectly() throws IOException {
        String fileContent = """
                2
                5 1
                1 3
                """;
        Path testFile = createTempFile(fileContent);

        graph.loadGraphFromFile(testFile.toString());

        assertEquals(2, graph.getVertexCount());
        assertFalse(graph.isDirected());
        assertTrue(graph.hasEdge(0, 0));
        assertEquals(5, graph.getEdgeWeight(0, 0));
        assertEquals(3, graph.getEdgeWeight(1, 1));
        assertTrue(graph.hasEdge(0, 1));
        assertEquals(1, graph.getEdgeWeight(0, 1));
    }

    @Test
    void loadGraphFromFile_withNonexistentFile_shouldThrowFileNotFoundException() {
        assertThrows(FileNotFoundException.class, () ->
                graph.loadGraphFromFile("nonexistent_file.txt"));
    }

    @Test
    void loadGraphFromFile_withSingleVertex_shouldLoadCorrectly() throws IOException {
        String fileContent = """
                1
                0
                """;
        Path testFile = createTempFile(fileContent);

        graph.loadGraphFromFile(testFile.toString());

        assertEquals(1, graph.getVertexCount());
        assertFalse(graph.isDirected());
    }

    @Test
    void loadGraphFromFile_withZeroVertices_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = "0\n";
        Path testFile = createTempFile(fileContent);

        assertThrows(IllegalArgumentException.class, () ->
                graph.loadGraphFromFile(testFile.toString()));
    }

    @Test
    void loadGraphFromFile_withNegativeVertexCount_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = "-5\n";
        Path testFile = createTempFile(fileContent);

        assertThrows(IllegalArgumentException.class, () ->
                graph.loadGraphFromFile(testFile.toString()));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void loadGraphFromFile_withDifferentSizes_shouldLoadCorrectly(int size) throws IOException {
        StringBuilder content = new StringBuilder(size + "\n");
        for (int i = 0; i < size; i++) {
            content.repeat("0 ", size);
            content.append("\n");
        }

        Path testFile = createTempFile(content.toString());
        graph.loadGraphFromFile(testFile.toString());

        assertEquals(size, graph.getVertexCount());
        assertFalse(graph.isDirected());
    }

    @Test
    void loadGraphFromFile_withLargeWeights_shouldLoadCorrectly() throws IOException {
        String fileContent = """
                2
                0 999999
                999999 0
                """;
        Path testFile = createTempFile(fileContent);

        graph.loadGraphFromFile(testFile.toString());

        assertEquals(999999, graph.getEdgeWeight(0, 1));
    }

    @Test
    void exportGraphToDot_withUndirectedGraph_shouldExportCorrectly() throws IOException {
        String fileContent = """
                3
                0 1 2
                1 0 3
                2 3 0
                """;
        Path inputFile = createTempFile(fileContent);
        graph.loadGraphFromFile(inputFile.toString());
        Path outputFile = tempDir.resolve("output.dot");

        graph.exportGraphToDot(outputFile.toString());

        String output = Files.readString(outputFile);
        assertTrue(output.startsWith("graph graphname {"));
        assertTrue(output.contains("0 -- 1 [label=\"1\"];"));
        assertTrue(output.contains("0 -- 2 [label=\"2\"];"));
        assertTrue(output.contains("1 -- 2 [label=\"3\"];"));
        assertTrue(output.endsWith("}\n"));
        
        // Проверяем, что нет дублирующих рёбер для неориентированного графа
        long edgeCount = output.lines()
                .filter(line -> line.contains("--"))
                .count();
        assertEquals(3, edgeCount);
    }

    @Test
    void exportGraphToDot_withDirectedGraph_shouldExportCorrectly() throws IOException {
        String fileContent = """
                3
                0 1 0
                0 0 2
                0 0 0
                """;
        Path inputFile = createTempFile(fileContent);
        graph.loadGraphFromFile(inputFile.toString());
        Path outputFile = tempDir.resolve("output.dot");

        graph.exportGraphToDot(outputFile.toString());

        String output = Files.readString(outputFile);
        assertTrue(output.startsWith("digraph graphname {"));
        assertTrue(output.contains("0 -> 1 [label=\"1\"];"));
        assertTrue(output.contains("1 -> 2 [label=\"2\"];"));
        assertTrue(output.endsWith("}\n"));
        assertFalse(output.contains("1 -> 0"));
    }

    @Test
    void exportGraphToDot_withSelfLoop_shouldExportCorrectly() throws IOException {
        String fileContent = """
                2
                5 1
                1 3
                """;
        Path inputFile = createTempFile(fileContent);
        graph.loadGraphFromFile(inputFile.toString());
        Path outputFile = tempDir.resolve("output.dot");

        graph.exportGraphToDot(outputFile.toString());

        String output = Files.readString(outputFile);
        assertTrue(output.startsWith("graph graphname {"));
        assertTrue(output.contains("0 -- 0 [label=\"5\"];"));
        assertTrue(output.contains("1 -- 1 [label=\"3\"];"));
        assertTrue(output.contains("0 -- 1 [label=\"1\"];"));
        assertTrue(output.endsWith("}\n"));
    }

    @Test
    void exportGraphToDot_withEmptyGraph_shouldExportCorrectly() throws IOException {
        String fileContent = """
                3
                0 0 0
                0 0 0
                0 0 0
                """;
        Path inputFile = createTempFile(fileContent);
        graph.loadGraphFromFile(inputFile.toString());
        Path outputFile = tempDir.resolve("output.dot");

        graph.exportGraphToDot(outputFile.toString());

        String output = Files.readString(outputFile);
        assertTrue(output.startsWith("graph graphname {"));
        assertTrue(output.endsWith("}\n"));
        
        // Не должно быть рёбер
        long edgeCount = output.lines()
                .filter(line -> line.contains("--") || line.contains("->"))
                .count();
        assertEquals(0, edgeCount);
    }

    @Test
    void getVertexCount_beforeLoadingGraph_shouldReturnZero() {
        assertEquals(0, graph.getVertexCount());
    }

    @Test
    void getVertexCount_afterLoadingGraph_shouldReturnCorrectCount() throws IOException {
        String fileContent = """
                5
                0 1 0 0 0
                1 0 1 0 0
                0 1 0 1 0
                0 0 1 0 1
                0 0 0 1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertEquals(5, graph.getVertexCount());
    }

    @Test
    void isDirected_withSymmetricMatrix_shouldReturnFalse() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertFalse(graph.isDirected());
    }

    @Test
    void isDirected_withAsymmetricMatrix_shouldReturnTrue() throws IOException {
        String fileContent = """
                2
                0 1
                0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertTrue(graph.isDirected());
    }

    @Test
    void getAdjacentVertices_withMultipleNeighbors_shouldReturnAllNeighbors() throws IOException {
        String fileContent = """
                4
                0 1 1 1
                1 0 0 0
                1 0 0 0
                1 0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        List<Integer> adjacentVertices = graph.getAdjacentVertices(0);

        assertEquals(3, adjacentVertices.size());
        assertTrue(adjacentVertices.contains(1));
        assertTrue(adjacentVertices.contains(2));
        assertTrue(adjacentVertices.contains(3));
    }

    @Test
    void getAdjacentVertices_withNoNeighbors_shouldReturnEmptyList() throws IOException {
        String fileContent = """
                3
                0 0 0
                0 0 0
                0 0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        List<Integer> adjacentVertices = graph.getAdjacentVertices(0);

        assertTrue(adjacentVertices.isEmpty());
    }

    @Test
    void getAdjacentVertices_withSelfLoop_shouldIncludeSelf() throws IOException {
        String fileContent = """
                2
                5 0
                0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        List<Integer> adjacentVertices = graph.getAdjacentVertices(0);

        assertEquals(1, adjacentVertices.size());
        assertTrue(adjacentVertices.contains(0));
    }

    @Test
    void getAdjacentVertices_withInvalidNegativeIndex_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.getAdjacentVertices(-1));
    }

    @Test
    void getAdjacentVertices_withInvalidLargeIndex_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.getAdjacentVertices(5));
    }

    @Test
    void hasEdge_withExistingEdge_shouldReturnTrue() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertTrue(graph.hasEdge(0, 1));
        assertTrue(graph.hasEdge(1, 0));
    }

    @Test
    void hasEdge_withNonExistingEdge_shouldReturnFalse() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertFalse(graph.hasEdge(0, 0));
        assertFalse(graph.hasEdge(1, 1));
    }

    @Test
    void hasEdge_withInvalidSourceIndex_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.hasEdge(-1, 0));
    }

    @Test
    void hasEdge_withInvalidTargetIndex_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.hasEdge(0, 10));
    }

    @Test
    void hasEdge_withBothInvalidIndices_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.hasEdge(-1, 10));
    }

    @Test
    void getEdgeWeight_withExistingEdge_shouldReturnCorrectWeight() throws IOException {
        String fileContent = """
                2
                0 5
                5 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertEquals(5, graph.getEdgeWeight(0, 1));
        assertEquals(5, graph.getEdgeWeight(1, 0));
    }

    @Test
    void getEdgeWeight_withNonExistingEdge_shouldReturnZero() throws IOException {
        String fileContent = """
                2
                0 0
                0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertEquals(0, graph.getEdgeWeight(0, 1));
        assertEquals(0, graph.getEdgeWeight(1, 0));
    }

    @Test
    void getEdgeWeight_withInvalidSourceIndex_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.getEdgeWeight(-1, 0));
    }

    @Test
    void getEdgeWeight_withInvalidTargetIndex_shouldThrowIllegalArgumentException() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        assertThrows(IllegalArgumentException.class, () ->
                graph.getEdgeWeight(0, 5));
    }

    @Test
    void toString_shouldReturnFormattedString() throws IOException {
        String fileContent = """
                2
                0 1
                1 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        String result = graph.toString();

        assertTrue(result.contains("Graph size: 2"));
        assertTrue(result.contains("Undirected Graph"));
        assertTrue(result.contains("Adjacency matrix:"));
        assertTrue(result.contains("0 1"));
        assertTrue(result.contains("1 0"));
    }

    @Test
    void toString_withDirectedGraph_shouldIndicateDirected() throws IOException {
        String fileContent = """
                2
                0 1
                0 0
                """;
        Path testFile = createTempFile(fileContent);
        graph.loadGraphFromFile(testFile.toString());

        String result = graph.toString();

        assertTrue(result.contains("Directed Graph"));
    }

    @Test
    void toString_beforeLoadingGraph_shouldNotThrowException() {
        String result = graph.toString();

        assertNotNull(result);
        assertTrue(result.contains("Graph size: 0"));
    }

    // Helper
    private Path createTempFile(String content) throws IOException {
        Path file = tempDir.resolve("test_graph_" + System.nanoTime() + ".txt");
        Files.writeString(file, content);
        return file;
    }
}
