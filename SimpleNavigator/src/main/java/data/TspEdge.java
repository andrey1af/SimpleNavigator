package data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
 * Info about edges for ant colony algorithm for TSP solving
 *
 * Proximity is based on the weight of the edge
 * Pheromones describe how often ants go through this edge in better routes
 */
@AllArgsConstructor
@NoArgsConstructor
public class TspEdge {
    private double proximity;
    private double pheromones;

    public double getDesire() {
        return proximity * pheromones;
    }

    public void addPheromones(double pheromones) {
        this.pheromones += pheromones;
    }
}
