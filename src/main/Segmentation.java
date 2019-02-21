package main;

import java.util.ArrayList;
import java.util.List;

public class Segmentation {

    private List<List<Integer>> segmentation;

    /**
     * Create a segmentation to the size of the given image
     * @param img
     */
    public Segmentation(Image img) {
        this(img.getWidth(), img.getHeight());
    }
    public Segmentation(int width, int height) {
        for (int y = 0; y < height; y++) {
            ArrayList<Integer> row = new ArrayList<>();

            for (int x = 0; x < width; x++) {
                row.add(0);
            }

        }
    }

    public int getWidth() {
        return 0;
    }
    public void getHeight() {

    }

    public int getLabel(int x, int y) {
        return segmentation.get(y).get(x);
    }

    public void setLabel(int x, int y, int label) {
        segmentation.get(y).set(x, label);
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
