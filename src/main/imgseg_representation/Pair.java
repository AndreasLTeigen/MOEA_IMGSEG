package imgseg_representation;

public class Pair implements Comparable<Pair>{
    public Chromosome chromosome;
    public float sortValue;

    public Pair(Chromosome chromosome, float sortValue){
        this.chromosome = chromosome;
        this.sortValue = sortValue;
    }
    @Override
    public int compareTo(Pair o) {
        // -1, jeg kommer først. 1, jeg kommer sist
        return (int)Math.signum(sortValue - o.sortValue);
    }
}
