package NSGAII;

import imgseg_representation.Chromosome;

public class ParetoObject {

    private Chromosome chromosome = null;
    private double crowdingDistance = -1f;
    private boolean crowdingDistanceSorted = false;

    public ParetoObject(Chromosome chromosome) {
        this(chromosome, -1f);
    }

    public ParetoObject(Chromosome chromosome, float crowdingDistance) {
        this.chromosome = chromosome;
        this.crowdingDistance = crowdingDistance;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public boolean isCrowdingDistanceSorted() {
        return crowdingDistanceSorted;
    }

    public void setCrowdingDistanceSorted(boolean crowdingDistanceSorted) {
        this.crowdingDistanceSorted = crowdingDistanceSorted;
    }
}
