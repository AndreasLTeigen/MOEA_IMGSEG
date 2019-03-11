package imgseg_representation;

import imgseg_solver.ChromosomeEvaluations;

public class Chromosome {
    public Segmentation segmentation;
    public Image img;

    public final float overallDev, connectivity;

    /**
     * Create an empty chromosome with the size of the given image
     * @param img
     */
    public Chromosome(Image img){
        this.segmentation = new Segmentation(img);
        this.img = img;

        overallDev = ChromosomeEvaluations.overallDeviation(this);
        connectivity = ChromosomeEvaluations.connectivity(this);
    }


}

