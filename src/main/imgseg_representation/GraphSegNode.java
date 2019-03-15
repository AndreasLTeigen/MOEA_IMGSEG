package imgseg_representation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphSegNode {
    public final int x, y;

    public GraphSegNode next;
    public List<GraphSegNode> previous = new ArrayList<>();

    public GraphSegNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * returns the direction if the node referenced in next,
     * in the range 0 - 3, right, left, up, down.
     * Returns -1 if next is itself or null
     */
    public int getNextDirection() {
        if (next == null || next == this) return -1;

        int xdir = next.x - x;
        int ydir = next.y - y;
        if (xdir > 0) return 0;
        if (xdir < 0) return 1;
        if (ydir < 0) return 2;
        if (ydir > 0) return 3;
        return -1;
    }

    public String toString() {
        return "GraphSegNode:[x:" + x + ", y:" + y +"]";
    }
}
