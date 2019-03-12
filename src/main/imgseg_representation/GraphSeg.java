package imgseg_representation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphSeg {

    //segmentation according to a graph
    public List<List<GraphSegNode>> nodes = new ArrayList<>();

    public GraphSeg() {

    }
    public GraphSeg(List<List<GraphSegNode>> nodes) {
        this.nodes = nodes;
    }

    public float getWidth() {
        return nodes.get(0).size();
    }
    public float getHeight() {
        return nodes.size();
    }
    public GraphSegNode getNode(int x, int y) {
        return nodes.get(y).get(x);
    }

    /**
     * Get labels in the order: right, left, top, bot, right_top, right_bot, left_top, left_bot
     * The labels will be null, if a neghbour would be out of bounds
     */
    public List<GraphSegNode> getNeighbours(int x, int y) {
        List<GraphSegNode> neighbours = new ArrayList<>();
        int[][] neighbourCoords = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 1}, {-1, -1}, {-1, 1}};

        for (int i = 0; i < neighbourCoords.length; i++) {
            int nx = x + neighbourCoords[i][0];
            int ny = y + neighbourCoords[i][1];

            //check if out of bounds
            if (nx < 0 || nx >= getWidth() || ny < 0 || ny >= getHeight()) {
                neighbours.add(null);
            } else {
                neighbours.add(getNode(nx, ny));
            }
        }
        return neighbours;
    }

    public Set<GraphSegNode> getAllNodes() {
        return streamAll().collect(Collectors.toSet());
    }
    public Stream<GraphSegNode> streamAll() {
        return nodes.stream().flatMap(List::stream);
    }

}
