package data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Ant {
    private final List<Integer> route = new ArrayList<>();
    private int distance;

    public int routeLength() {
        return route.size();
    }

    public void addDistance(int distance) {
        this.distance += distance;
    }

    public boolean allVisited(int vertexCount) {
        for (int i = 0; i < vertexCount; i++) {
            if (!route.contains(i)) return false;
        }
        return true;
    }
}
