package algorithms;

import S21_collection.Collection;
import S21_collection.Queue;
import S21_collection.Stack;

import data.Edge;
import data.TspEdge;
import graph.Graph;
import data.TsmResult;
import data.Ant;

import java.util.*;


public class GraphAlgorithmsImpl implements GraphAlgorithms {
    private static final double INIT_PHEROMONES = 0.2;
    private static final int ITERATIONS_PER_VERTEX = 100;
    private static final int MAX_STAGNATION = 1000;
    private static final int VERTEX_PENALTY = 10;
    private static final int MIN_STAGNATION = 50;
    private static final int DESIRE_REDUCTION = 100;
    private static final Random random = new Random();

    @Override
    public List<Integer> depthFirstSearch(Graph graph, int startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        Stack<Integer> vertexes = new Stack<>();

        return searchResult(graph, startVertex,vertexes);

    }

    @Override
    public List<Integer> breadthFirstSearch(Graph graph, int startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        Queue<Integer> vertexes = new Queue<>();

        return searchResult(graph, startVertex,vertexes);
    }

    private List<Integer> searchResult(Graph graph, int startVertex, Collection<Integer> vertexes) {
        Set<Integer> placed = new HashSet<>();
        List<Integer> result = new ArrayList<>();

        vertexes.push(startVertex);


        while(!vertexes.isEmpty()) {
            int currentVertex = vertexes.pop();
            if(placed.contains(currentVertex)) {
                continue;
            }

            placed.add(currentVertex);
            result.add(currentVertex);
            List<Integer> vertexChildren = graph.getAdjacentVertices(currentVertex);

            for(Integer vertexChild : vertexChildren) {
                if(!placed.contains(vertexChild)) {
                    vertexes.push(vertexChild);
                }
            }
        }
        return result;
    }

    @Override
    public int getShortestPathBetweenVertices(Graph graph, int vertex1, int vertex2) {
        int vertexCount = graph.getVertexCount();

        if (vertex1 < 0 || vertex1 >= vertexCount || vertex2 < 0 || vertex2 >= vertexCount) {
            throw new IllegalArgumentException("Invalid vertex index");
        }

        if (vertex1 == vertex2) {
            return 0;
        }

        int[] distancesToVertex = new int[vertexCount];
        Arrays.fill(distancesToVertex, Integer.MAX_VALUE);
        distancesToVertex[vertex1] = 0;
        boolean[] visitedVertex = new boolean[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            int minDistance = Integer.MAX_VALUE;
            int minVertex = -1;

            for (int j = 0; j < vertexCount; j++) {
                if (!visitedVertex[j] && distancesToVertex[j] < minDistance) {
                    minDistance = distancesToVertex[j];
                    minVertex = j;
                }
            }

            // Если граф несвязный
            if (minVertex == -1) {
                break;
            }
            // Выходим, если обработали нужную вершину
            if (minVertex == vertex2) {
                break;
            }

            visitedVertex[minVertex] = true;

            for (int j = 0; j < vertexCount; j++) {
                if (j != minVertex && !visitedVertex[j] && graph.hasEdge(minVertex, j)) {
                    int edgeWeight = graph.getEdgeWeight(minVertex, j);
                    if (edgeWeight > 0 && distancesToVertex[minVertex] != Integer.MAX_VALUE) {
                        int newDistance = distancesToVertex[minVertex] + edgeWeight;
                        if (newDistance < distancesToVertex[j]) {
                            distancesToVertex[j] = newDistance;
                        }
                    }
                }
            }
        }
        if (distancesToVertex[vertex2] == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("No path exists between vertex " + vertex1 + " and vertex " + vertex2);
        }
        return distancesToVertex[vertex2];
    }

    @Override
    public int[][] getShortestPathsBetweenAllVertices(Graph graph) {
        int vertexCount = graph.getVertexCount();
        int[][] distanceMatrix = new int[vertexCount][vertexCount];

        // Инициализация матрицы расстояний
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else if (graph.hasEdge(i, j)) {
                    distanceMatrix[i][j] = getShortestPathBetweenVertices(graph, i, j);
                } else {
                    distanceMatrix[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        // Алгоритм Флойда-Уоршелла
        for (int k = 0; k < vertexCount; k++) {
            for (int i = 0; i < vertexCount; i++) {
                for (int j = 0; j < vertexCount; j++) {
                    if (distanceMatrix[i][k] != Integer.MAX_VALUE && distanceMatrix[k][j] != Integer.MAX_VALUE) {
                        distanceMatrix[i][j] = Math.min(distanceMatrix[i][j], distanceMatrix[i][k] + distanceMatrix[k][j]);
                    }
                }
            }
        }
        // Проверка на связный граф
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (distanceMatrix[i][j] == Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("No path exists between vertex " + i + " and vertex " + j);
                }
            }
        }

        return distanceMatrix;
    }

    @Override
    public int[][] getLeastSpanningTree(Graph graph) {

        int[][] leastSpanningTree = null;
        int leastTreeWeight = Integer.MAX_VALUE;
        int vertexCount = graph.getVertexCount();

        for (int start = 0; start < vertexCount; start++) {
            int[][] spanningTree = new int[vertexCount][vertexCount];
            PriorityQueue<Edge> edges = new PriorityQueue<>();
            boolean[] visited = new boolean[vertexCount];
            addVertex(edges, visited, graph, start);

            int treeWeight = 0;
            while (true) {
                Edge edge;
                do {
                    edge = edges.poll();
                } while (edge != null && visited[edge.vertex2()]);
                if (edge == null) break;
                spanningTree[edge.vertex1()][edge.vertex2()] = edge.weight();
                spanningTree[edge.vertex2()][edge.vertex1()] = edge.weight();
                treeWeight += edge.weight();
                addVertex(edges, visited, graph, edge.vertex2());
            }
            if (allVisited(visited)) {
                if (treeWeight < leastTreeWeight) {
                    leastTreeWeight = treeWeight;
                    leastSpanningTree = spanningTree;
                }
            }
        }
        return leastSpanningTree;
    }

    private void addVertex(PriorityQueue<Edge> edges, boolean[] visited, Graph graph, int vertex) {
        visited[vertex] = true;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (visited[i]) continue;
            int weight = graph.getEdgeWeight(vertex, i);
            if (weight != 0) {
                edges.add(new Edge(vertex, i, weight));
            }
        }
    }

    private boolean allVisited(boolean[] visited) {
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    @Override
    public TsmResult solveTravelingSalesmanProblem(Graph graph) {
        final double Q = graph.getMinEdgeWeight();
        int vertexCount = graph.getVertexCount();
        TspEdge[][] edges = new TspEdge[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                if (graph.hasEdge(i, j))
                    edges[i][j] = new TspEdge(Q / graph.getEdgeWeight(i, j), INIT_PHEROMONES);
                else edges[i][j] = new TspEdge();
            }
        }

        int iterationsNum = vertexCount * ITERATIONS_PER_VERTEX;
        Ant bestAnt = null;
        int lastImprove = 0;
        for (int i = 0; i < iterationsNum; i++) {
            if (i - lastImprove > Math.max(MAX_STAGNATION - vertexCount * VERTEX_PENALTY, MIN_STAGNATION))
                break;
            List<Ant> ants = antsMoveFromEachVertex(graph, edges);
            if (ants.isEmpty()) continue;
            addPheromonesOnEdges(ants, edges, Q);
            if (bestAnt == null)
                bestAnt = ants.getFirst();
            for (Ant ant : ants) {
                if (ant.getDistance() < bestAnt.getDistance()) {
                    bestAnt = ant;
                    lastImprove = i;
                }
            }
        }
        if (bestAnt == null) return null;
        return new TsmResult(bestAnt.getRoute().stream().mapToInt(Integer::intValue).toArray(), bestAnt.getDistance());
    }

    private List<Ant> antsMoveFromEachVertex(Graph graph, TspEdge[][] edges) {
        List<Ant> ants = new ArrayList<>();
        int vertexCount = graph.getVertexCount();

        for (int start = 0; start < graph.getVertexCount(); start++) {
            int currVertex = start;
            Ant ant = new Ant();
            ant.getRoute().add(currVertex);
            boolean allVisited = ant.allVisited(vertexCount);
            boolean notSolvable = false;
            while (!allVisited || ant.getRoute().getLast() != start) {
                if (ant.routeLength() > vertexCount * 2) {
                    notSolvable = true;
                    break;
                }
                allVisited = ant.allVisited(vertexCount);
                double desireSum = 0;
                double[] desireArray = new double[vertexCount];
                for (int i = 0; i < vertexCount; i++) {
                    if (i == currVertex) continue;
                    if (!ant.getRoute().contains(i) || (allVisited && i == start))
                        desireArray[i] = edges[currVertex][i].getDesire();
                    else desireArray[i] = edges[currVertex][i].getDesire() / DESIRE_REDUCTION;
                    desireSum += desireArray[i];
                }
                if (desireSum == 0) {
                    notSolvable = true;
                    break;
                }
                double randomChoice = random.nextDouble();
                for (int i = 0; i < vertexCount; i++) {
                    randomChoice -= desireArray[i] / desireSum;
                    if (randomChoice < 0) {
                        ant.getRoute().add(i);
                        ant.addDistance(graph.getEdgeWeight(currVertex, i));
                        currVertex = i;
                        break;
                    }
                }
            }
            if (!notSolvable)
                ants.add(ant);
        }
        return ants;
    }

    private void addPheromonesOnEdges(List<Ant> ants, TspEdge[][] edges, final double Q) {
        for (Ant ant : ants) {
            List<Integer> route = ant.getRoute();
            for (int i = 0; i < route.size() - 1; i++) {
                edges[route.get(i)][route.get(i + 1)].addPheromones(Q / ant.getDistance());
            }
        }
    }
}
