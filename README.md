# SimpleNavigator

## About
SimpleNavigator is a Java console application for working with weighted graphs represented by an adjacency matrix. The project includes graph loading/export, classic graph algorithms, and a Traveling Salesman Problem solver.

## Features
- Load graphs from `.txt` files (adjacency matrix format)
- Export a graph to `.dot` format for visualization
- Depth-first search (non-recursive, custom `Stack`)
- Breadth-first search (custom `Queue`)
- Shortest path between two vertices (Dijkstra)
- Shortest paths between all vertices (Floyd-Warshall)
- Least spanning tree search (Prim-based approach)
- Traveling Salesman Problem solver (ant colony optimization)

## Tech Stack
- Java 21
- Gradle
- JUnit 5
- Lombok
- Makefile

## Architecture
- `graph.Graph`
  Handles graph storage as an adjacency matrix, graph loading from file, DOT export, and base graph queries (`hasEdge`, `getEdgeWeight`, `getAdjacentVertices`).

- `algorithms.GraphAlgorithms` + `algorithms.GraphAlgorithmsImpl`
  Defines and implements core algorithms:
  - DFS and BFS
  - Dijkstra (single-source shortest path between two vertices)
  - Floyd-Warshall (all-pairs shortest paths)
  - Prim-based least spanning tree
  - Ant colony TSP solver (`TsmResult`)

- `S21_collection`
  Custom data structures used in algorithms:
  - `Stack<T>`
  - `Queue<T>`
  - shared `Collection<T>` interface and `Node<T>`

- `data`
  Supporting data models for algorithms:
  - `TsmResult`
  - `Ant`
  - `Edge`
  - `TspEdge`

- CLI layer (`Main`, `View`)
  Provides a text menu to load a graph and run all available operations interactively.

Note: vertex indexing in the current implementation is zero-based (0..N-1).

## Project Structure

```text
├ README.md
└ SimpleNavigator/
  ├── Makefile
  ├── build.gradle.kts
  ├── settings.gradle.kts
  ├── gradlew
  ├── gradlew.bat
  ├── gradle/
  │   └── wrapper/
  └── src/
      ├── main/java/
      │   ├── Main.java
      │   ├── View.java
      │   ├── graph/
      │   ├── algorithms/
      │   ├── S21_collection/
      │   └── data/
      └── test/
          ├── java/
          └── resources/
```

## Run
From repository root:

```bash
cd src/SimpleNavigator
```

Build libraries and app:

```bash
./gradlew --console=plain buildAll
```

Run CLI app:

```bash
./gradlew --console=plain run
```

Run tests:

```bash
./gradlew --console=plain test
```

Alternative (if `make` is installed):

```bash
make all
make run
make test
```

Graph input format (`.txt`):
- First line: number of vertices `N`
- Next `N` lines: `N x N` adjacency matrix (0 means no edge)
