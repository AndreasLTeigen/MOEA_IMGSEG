package imgseg_representation;

import java.util.ArrayList;
import java.util.List;

public class GraphSegNode {
    public final int x, y;

    public GraphSegNode next;
    public List<GraphSegNode> previous = new ArrayList<>();

    public GraphSegNode(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
