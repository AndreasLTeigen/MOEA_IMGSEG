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
                row.add(new SegLabel());
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

//    public String toString(){
//        StringBuilder sb;
//        for (int y = 0; y < y_size; y++){
//            for (int x = 0; x < x_size; x++){
//                System.out.print(segmentation.get(y).get(x) + " ");
//            }
//        }
//    }
}
