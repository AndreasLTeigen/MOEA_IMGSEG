package imgseg_representation;

import java.util.ArrayList;
import java.util.List;

public class ParetoObject {
    public Chromosome chromosome;
    public int dominationRank = 0;
    public List<ParetoObject> dominatedChromosomes;

    public ParetoObject(Chromosome chromosome){
        this.chromosome = chromosome;
        this.dominationRank = 0;
        this.dominatedChromosomes = new ArrayList<>();
    }
}
