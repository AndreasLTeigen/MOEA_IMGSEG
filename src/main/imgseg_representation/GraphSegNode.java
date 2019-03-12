package imgseg_representation;

import java.util.ArrayList;
import java.util.List;

public class GraphSegNode {
    public int x, y;
    public GraphSegNode next;
    public List<GraphSegNode> previous = new ArrayList<>();
    public List<GraphSegNode> neighbours = new ArrayList<>();
}
