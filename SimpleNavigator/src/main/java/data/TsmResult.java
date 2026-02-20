package data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TsmResult {
    private int[] vertices;    // an array with the route you are looking for (with the vertex traverse order).
    private double distance;  // the length of this route
}
