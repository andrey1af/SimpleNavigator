package algorithms;

import data.TsmResult;
import graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TspTest {
    private static final String FILE_FOLDER = "src/test/resources/";
    private final GraphAlgorithms algorithms = new GraphAlgorithmsImpl();
    private Graph graph;

    @BeforeEach
    void setUp() {
        graph = new Graph();
    }

    @Test
    void singleVertex_1() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_1.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertEquals(0, result.getDistance());
        assertEquals(1, result.getVertices().length);
        assertTrue(checkIfValid(result, graph));
    }

    @Test
    void selfLoop_2() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_2_selfLoop.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertEquals(2, result.getDistance());
        assertEquals(3, result.getVertices().length);
        assertTrue(checkIfValid(result, graph));
    }

    @Test
    void unweightedUndirected_3() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_3_unweighted_undirected.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertEquals(3, result.getDistance());
        assertEquals(4, result.getVertices().length);
        assertTrue(checkIfValid(result, graph));
    }

    @Test
    void weightedDirected_3() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_3_weighted_directed.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertEquals(5, result.getDistance());
        assertEquals(4, result.getVertices().length);
        assertTrue(checkIfValid(result, graph));
    }

    // Check that average solution is close to the best possible
    @Test
    void weightedUndirected_5() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_5_weighted_undirected.txt");
        int iterations = 100;
        double avg = 0;
        for (int i = 0; i < iterations; i++) {
            TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
            avg += result.getDistance();
            assertTrue(result.getDistance() <= 24);
            assertTrue(result.getVertices().length > graph.getVertexCount());
            assertTrue(checkIfValid(result, graph));
        }
        avg /= iterations;
        assertTrue(avg >= 19.0 && avg <= 20.0);
    }

    @Test
    void unweightedDirected_10() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_10_unweighted_directed.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertEquals(10, result.getDistance());
        assertEquals(11, result.getVertices().length);
        assertTrue(checkIfValid(result, graph));
    }

    /* Vertex 4 has 4 edges from it, but doesn't have edges to it (from another vertex)
       So, it is not possible to go to vertex 4
       If alg starts from it, it can't return to it after all
     */
    @Test
    void unweightedDirected_10_notSolvable() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_10_notSolvableByTSP.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertNull(result);
    }

    @Test
    void weightedDirected_15_complete() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_15_weighted_directed_complete.txt");
        assertTimeout(Duration.ofSeconds(1), () -> {
            TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
            assertTrue(result.getVertices().length > 15);
            assertTrue(checkIfValid(result, graph));
            assertTrue(result.getDistance() < 100 && result.getDistance() > 60);
        });
    }

    // Vertices 4 and 5 are not connected to other v., what makes it impossible to solve it
    @Test
    void weightedUndirected_6_disconnected() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_6_weighted_undirected_disconnected.txt");
        TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
        assertNull(result);
    }

    // Circle with 100 vertices
    @Test
    void unweightedUndirected_100() throws Exception {
        int vertexNum = 100;
        int[][] matrix = new int[vertexNum][vertexNum];
        for (int i = 0; i < vertexNum - 1; i++) {
            matrix[i][i + 1] = 1;
            matrix[i + 1][i] = 1;
        }
        matrix[vertexNum - 1][0] = 1;
        matrix[0][vertexNum - 1] = 1;

        Field adjacencyMatrix = graph.getClass().getDeclaredField("adjacencyMatrix");
        adjacencyMatrix.setAccessible(true);
        adjacencyMatrix.set(graph, matrix);

        Field vertexCount = graph.getClass().getDeclaredField("vertexCount");
        vertexCount.setAccessible(true);
        vertexCount.set(graph, vertexNum);

        Method isSymmetricMethod = graph.getClass().getDeclaredMethod("isSymmetricMatrix");
        isSymmetricMethod.setAccessible(true);
        assertTrue((boolean) isSymmetricMethod.invoke(graph));

        assertTimeout(Duration.ofSeconds(10), () -> {
            TsmResult result = algorithms.solveTravelingSalesmanProblem(graph);
            assertEquals(100, result.getDistance());
            assertEquals(101, result.getVertices().length);
            assertTrue(checkIfValid(result, graph));
        });
    }

    private boolean checkIfValid(TsmResult result, Graph graph) {
        int[] vertices = result.getVertices();
        // Every vertex is in the route at least once
        for (int i = 0; i < graph.getVertexCount(); i++) {
            final int ii = i;
            if (Arrays.stream(vertices).noneMatch(x -> x == ii))
                return false;
        }
        // Distance matches the route and all edges exist
        int expDistance = 0;
        for (int i = 0; i < vertices.length - 1; i++) {
            if (!graph.hasEdge(vertices[i], vertices[i + 1])) return false;
            expDistance += graph.getEdgeWeight(vertices[i], vertices[i + 1]);
        }
        assertEquals(expDistance, result.getDistance());
        // Route begin and finish at one vertex
        return vertices[0] == vertices[vertices.length - 1];
    }
}
