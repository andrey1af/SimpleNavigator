package data;

public record Edge(int vertex1, int vertex2, int weight) implements Comparable<Edge> {
    @Override
    public int compareTo(Edge o) {
        return this.weight - o.weight;
    }
}
