package main;

import java.util.ArrayList;
import java.util.List;

public class Chromosome {
    public Segmentation segmentation;
    public Image img;

    /**
     * Create an empty chromosome with the size of the given image
     * @param img
     */
    public Chromosome(Image img){
        this.segmentation = new Segmentation(img);
        this.img = img;
    }


}

