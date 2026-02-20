package algorithms;

import data.Edge;
import graph.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class MinimalSpanningTreeTest {
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
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 0);
        int[][] expectedTree = {{0}};
        assertArrayEquals(expectedTree, tree);
    }

    @Test
    void selfLoop_2() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_2_selfLoop.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 1);
        int[][] expectedTree = {
                {0, 1},
                {1, 0}
        };
        assertArrayEquals(expectedTree, tree);
    }

    @Test
    void unweightedUndirected_3() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_3_unweighted_undirected.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 2);
        int sum = 0;
        for (int[] row : tree)
            for (int i : row) {
                assertTrue(i == 0 || i == 1);
                sum += i;
            }
        assertEquals(4, sum);
    }


    @Test
    void weightedDirected_3() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_3_weighted_directed.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 3);
    }

    @Test
    void weightedUndirected_5() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_5_weighted_undirected.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 10);
    }

    @Test
    void unweightedDirected_10() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_10_unweighted_directed.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 9);
    }

    /* Vertex 4 has 4 edges from it, but doesn't have edges to it (from another vertex)
       So, the only possible solution is to start tree from vertex 4 */
    @Test
    void unweightedDirected_10_hard() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_10_notSolvableByTSP.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 9);
    }

    @Test
    void weightedUndirected_6_disconnected() throws FileNotFoundException {
        graph.loadGraphFromFile(FILE_FOLDER + "graph_6_weighted_undirected_disconnected.txt");
        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertNull(tree);
    }

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

        int[][] tree = algorithms.getLeastSpanningTree(graph);
        assertEquals(graph.getVertexCount(), tree.length);
        assertEquals(graph.getVertexCount(), tree[0].length);
        bfsTreeCheck(tree, 99);

        adjacencyMatrix.set(graph, tree);
        assertTrue((boolean) isSymmetricMethod.invoke(graph));
    }

    /* Check if there is no cycles
       And count weight of all edges */
    private void bfsTreeCheck(int[][] tree, int expectedWeight) {
        Queue<Edge> queue = new LinkedList<>();
        int[] visited = new int[tree.length];
        addVertex(queue, visited, tree, 0, 0);
        int treeWeight = 0;

        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            treeWeight += edge.weight();
            addVertex(queue, visited, tree, edge.vertex1(), edge.vertex2());
        }

        boolean isAcyclic = true;
        for (int v : visited) {
            if (v > 1) {
                isAcyclic = false;
                break;
            }
        }
        assertTrue(isAcyclic);
        assertEquals(expectedWeight, treeWeight);
    }

    private void addVertex(Queue<Edge> queue, int[] visited, int[][] tree, int fromVertex, int vertex) {
        visited[vertex]++;
        if (visited[vertex] > 1) return;
        for (int i = 0; i < tree.length; i++) {
            if (i == fromVertex) continue;
            int weight = tree[vertex][i];
            if (weight != 0) {
                queue.add(new Edge(vertex, i, weight));
            }
        }
    }
}
