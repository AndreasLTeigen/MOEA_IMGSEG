package imgseg_representation;

import java.util.ArrayList;
import java.util.List;

public class GraphSeg {

    //segmentation according to a graph
    public List<List<GraphSegNode>> nodes = new ArrayList<>();

    public GraphSeg() {

    }
    public GraphSeg(List<List<GraphSegNode>> nodes) {
        this.nodes = nodes;
    }

}
