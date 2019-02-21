package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Segmentation {

    private List<List<SegLabel>> segmentation = new ArrayList<>();

    /**
     * Create a segmentation to the size of the given image
     * @param img
     */
    public Segmentation(Image img) {
        this(img.getWidth(), img.getHeight());
    }
    public Segmentation(int width, int height) {
        for (int y = 0; y < height; y++) {
            ArrayList<SegLabel> row = new ArrayList<>();

            for (int x = 0; x < width; x++) {
                row.add(new SegLabel(x, y, -1));
            }

            segmentation.add(row);
        }
    }

    public int getWidth() {
        return segmentation.get(0).size();
    }
    public int getHeight() {
        return segmentation.size();
    }

    public SegLabel getLabel(int x, int y) {
        return segmentation.get(y).get(x);
    }

    /**
     * Get labels in the order: right, left, top, bot, right_top, right_bot, left_top, left_bot
     * The labels will be null, if a neghbour would be out of bounds
     */
    public List<SegLabel> getNeighbours(int x, int y) {
        List<SegLabel> neighbours = new ArrayList<>();
        int[][] neighbourCoords = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
        for (int i = 0; i < neighbourCoords.length; i++) {
            int nx = x + neighbourCoords[i][0];
            int ny = y + neighbourCoords[i][1];

            //check if out of bounds
            if (nx < 0 || nx >= getWidth() || ny < 0 || ny >= getHeight()) {
                neighbours.add(null);
            } else {
                neighbours.add(getLabel(nx, ny));
            }
        }
        return neighbours;
    }
    public List<SegLabel> getNeighbours(SegLabel slabel) {
        return getNeighbours(slabel.x, slabel.y);
    }

    public int getLabelValue(int x, int y) {
        return getLabel(x, y).label;
    }

    public void setLabelValue(int x, int y, int label) {
        segmentation.get(y).get(x).label = label;
    }

    public List<SegLabel> getLabelsCollection() {
        List<SegLabel> coll = new ArrayList<>(getWidth() * getHeight());
        for (List<SegLabel> sll : segmentation) {
            coll.addAll(sll);
        }
        return coll;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < getHeight(); y++){
            for (int x = 0; x < getWidth(); x++){
                sb.append("[").append(segmentation.get(y).get(x).label).append("]");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
