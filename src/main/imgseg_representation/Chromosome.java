package imgseg_representation;

import java.util.List;

public class Chromosome {
    public Segmentation segmentation;
    public Image img;
    private int dominationRank = 0;
    private List<Chromosome> dominatedChromosomes;

    /**
     * Create an empty chromosome with the size of the given image
     * @param img
     */
    public Chromosome(Image img){
        this.segmentation = new Segmentation(img);
        this.img = img;
    }
    public List<Chromosome> getDominatedChromosomes() {
        return dominatedChromosomes;
    }

    public void setDominatedChromosomes(List<Chromosome> dominatedChromosomes) {
        this.dominatedChromosomes = dominatedChromosomes;
    }

    public int getDominationRank() {
        return dominationRank;
    }

    public void setDominationRank(int dominationRank) {
        this.dominationRank = dominationRank;
    }

}

