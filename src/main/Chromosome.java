package main;

import java.util.ArrayList;
import java.util.List;

public class Chromosome {
    public List<List<Integer>> segmentation;
    public int row_size;
    public int collumn_size;
    public int y_size, x_size;

    public Chromosome(){
        for (int y = 0; y < y_size; y++) {
            ArrayList row = new ArrayList();
            for (int x = 0; x < x_size; x++) {
                row.add(0);
            }

        }
    }

    public void print(){
        for (int y = 0; y < y_size; y++){
            for (int x = 0; x < x_size; x++){
                System.out.print(segmentation.get(y).get(x) + " ");
            }
        }
    }
}