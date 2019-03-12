package imgseg_representation;

import java.util.ArrayList;
import java.util.List;

public class GraphSeg {

    //segmentation according to a graph
    public List<GraphSegNode> nodes = new ArrayList<>();

    public GraphSeg() {

    }
    public GraphSeg(List<GraphSegNode> nodes) {
        this.nodes = nodes;
    }

}
