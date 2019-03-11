package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.Population;
import solver.ParentSelector;

import java.util.List;

public class NsgaParentSelector implements ParentSelector {

    class ParetoFront extends Population {
        public ParetoFront(List<Chromosome> chroms) {
            super(chroms);
        }
    };

    public static List<ParetoFront> nondominatedSort(Population parents, Population children) {
        return null;
    }

    /**
     * returns a sorted list of a given pareto front according to crowding distance
     */
    public static Population crowdingDistanceSort(Population paretoFront) {
        return null;
    }

    //use the above to calculate an absolute sorting
    public static Population sortedPopulation() {

    }

    public static Population tournamentSelection(Population parents) {
        return null;
    }

    @Override
    public Population selectParents(Population population) {
        List<ParetoFront> paretoFronts = nondominatedSort(population, null);

        //fill until pareto split is needed
        //use crowding distances

        return tournamentSelection(null);
    }
}
