package algorithms;

import data.TsmResult;
import graph.Graph;

import java.util.List;

public interface GraphAlgorithms {
    List<Integer> depthFirstSearch(Graph graph, int startVertex);
    List<Integer> breadthFirstSearch(Graph graph, int startVertex);
    int getShortestPathBetweenVertices(Graph graph, int vertex1, int vertex2);
    int[][] getShortestPathsBetweenAllVertices(Graph graph);
    int[][] getLeastSpanningTree(Graph graph);
    TsmResult solveTravelingSalesmanProblem(Graph graph);
}