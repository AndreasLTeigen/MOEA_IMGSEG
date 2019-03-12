package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.ParetoObject;
import imgseg_representation.Population;
import solver.ParentSelector;

import java.util.ArrayList;
import java.util.List;

public class NsgaParentSelector implements ParentSelector {

    class ParetoFront {
        public List<Chromosome> chromosomes;
        public ParetoFront(List<Chromosome> chroms) {
            chromosomes = chroms;
        }
    };

    public static List<List<ParetoObject>> nondominatedSort(Population parents, Population children) {
        ParetoObject paretoObj;
        List<ParetoObject> paretoPopulace = new ArrayList<>();
        List<List<ParetoObject>> paretoFronts = new ArrayList<>();
        List<ParetoObject> singularFront = new ArrayList<>();

        List<Chromosome> populace = new ArrayList<>();
        for (Chromosome chromosome : parents.chromosones) {
            paretoObj = new ParetoObject(chromosome);
            paretoPopulace.add(paretoObj);
        }
        for (Chromosome chromosome : children.chromosones) {
            paretoObj = new ParetoObject(chromosome);
            paretoPopulace.add(paretoObj);
        }

        for (ParetoObject paretoObject : paretoPopulace) {
            paretoObject.dominationRank = 0;
            paretoObject.dominatedChromosomes = new ArrayList<>();

            for (ParetoObject competitor : paretoPopulace) {
                if (!(competitor == paretoObject)) {
                    if (dominates(paretoObject.chromosome, competitor.chromosome)) {
                        if (!paretoObject.dominatedChromosomes.contains(competitor)) {
                            paretoObject.dominatedChromosomes.add(competitor);
                        }
                    } else if (dominates(competitor.chromosome, paretoObject.chromosome)) {
                        paretoObject.dominationRank = paretoObject.dominationRank + 1;
                    }
                }
            }
            if (paretoObject.dominationRank == 0){
                singularFront.add(paretoObject);
            }
        }

        int i = 0;
        List<ParetoObject> previousFront = paretoFronts.get(i);
        List<ParetoObject> nextFront = new ArrayList<>();

        while( previousFront != null && !previousFront.isEmpty()){
            for (ParetoObject paretoObject: previousFront){
                for (ParetoObject recessive: paretoObject.dominatedChromosomes){
                    if (recessive.dominationRank != 0){
                        recessive.dominationRank = recessive.dominationRank-1;
                    }
                    if (recessive.dominationRank == 0){
                        if (!nextFront.contains(recessive)){
                            nextFront.add(recessive);
                        }
                    }
                }
            }
            if (nextFront.isEmpty() && !isDominatedChromosomesEmpty(previousFront)){
                int minimumRank = -1;

                for (ParetoObject paretoObject: previousFront){
                    while(hasRecessiveRankGreaterThanZero(paretoObject)){
                        for (ParetoObject recessive: paretoObject.dominatedChromosomes){
                            if ((minimumRank == -1) || minimumRank > recessive.dominationRank){
                                minimumRank = recessive.dominationRank;
                            }
                        }
                    }
                }
                if (minimumRank != -1){
                    for ( ParetoObject paretoObject: previousFront){
                        while (hasRecessiveRankGreaterThanZero(paretoObject)){
                            for ( ParetoObject recessive: paretoObject.dominatedChromosomes){
                                if(recessive.dominationRank != 0){
                                    recessive.dominationRank = recessive.dominationRank - minimumRank;
                                }
                                if(recessive.dominationRank == 0){
                                    if(!nextFront.contains(recessive)){
                                        nextFront.add(recessive);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(!nextFront.isEmpty()){
                i += 1;
                paretoFronts.set(i,nextFront);
            }
            else{
                break;
            }
            previousFront = nextFront;
            nextFront = new ArrayList<>();
        }
        return paretoFronts;
    }

    public static boolean dominates(Chromosome competitor1, Chromosome competitor2){
        boolean noWorseThan = true, strictlyBetterThan = true;

        for (int objectiveNr = 0; objectiveNr < competitor1.objectiveValues.size(); objectiveNr++){
            if ( competitor1.objectiveValues.get(objectiveNr) > competitor2.objectiveValues.get(objectiveNr)){
                noWorseThan = false;
            }
            if ( competitor1.objectiveValues.get(objectiveNr) >= competitor2.objectiveValues.get(objectiveNr)){
                strictlyBetterThan = false;
            }
        }
        if (noWorseThan && strictlyBetterThan){
            return true;
        }
        else{
            return false;
        }
    }

     /**
     * returns a sorted list of a given pareto front according to crowding distance
     */
    public static Population crowdingDistanceSort(Population paretoFront) {
        return null;
    }

    //use the above to calculate an absolute sorting
    public static Population sortedPopulation() {
        return null;
    }

    public static Population tournamentSelection(Population parents) {
        return null;
    }


    public static boolean isDominatedChromosomesEmpty(List<ParetoObject> front){
        for(ParetoObject paretoObject: front){
            if (!paretoObject.dominatedChromosomes.isEmpty()){
                return false;
            }
        }
        return true;
    }

    public static boolean hasRecessiveRankGreaterThanZero(ParetoObject paretoObject){
        if(paretoObject.dominatedChromosomes.isEmpty()){
            return false;
        }
        for (ParetoObject recessive: paretoObject.dominatedChromosomes){
            if(recessive.dominationRank > 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public Population selectParents(Population population) {
        List<ParetoFront> paretoFronts = nondominatedSort(population, null);

        //fill until pareto split is needed
        //use crowding distances

        return tournamentSelection(null);
    }
}
