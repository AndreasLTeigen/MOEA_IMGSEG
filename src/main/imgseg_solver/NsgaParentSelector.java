package imgseg_solver;

import graphics.Plot;
import imgseg_representation.Chromosome;
import imgseg_representation.Pair;
import imgseg_representation.ParetoObject;
import imgseg_representation.Population;
import solver.ParentSelector;


import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NsgaParentSelector implements ParentSelector {
    class ParetoFront {
        public List<Chromosome> chroms;

        public ParetoFront(List<Chromosome> chroms) {
            this.chroms = chroms;
        }
    }

    public static List<List<Chromosome>> nondominatedSort(Population parents, Population children) {
        ParetoObject paretoObj;
        List<ParetoObject> paretoPopulace = new ArrayList<>();
        List<List<ParetoObject>> paretoFronts = new ArrayList<>();
        List<ParetoObject> singularFront = new ArrayList<>();

        for (Chromosome chromosome : parents.chromosones) {
            paretoObj = new ParetoObject(chromosome);
            paretoPopulace.add(paretoObj);
        }
        for (Chromosome chromosome : children.chromosones) {
            paretoObj = new ParetoObject(chromosome);
            paretoPopulace.add(paretoObj);
        }


        System.out.println(children.chromosones.size());
        System.out.println(parents.chromosones.size());
        System.out.println(paretoPopulace.size());

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

        paretoFronts.add(singularFront);

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
                paretoFronts.add(nextFront);
            }
            else{
                break;
            }
            previousFront = nextFront;
            nextFront = new ArrayList<>();
        }

        List<List<Chromosome>> rankedFronts = new ArrayList<>();

        for(List<ParetoObject> paretoFront: paretoFronts) {
            rankedFronts.add(getChromFrontFromParetoFront(paretoFront));
        }
        return rankedFronts;
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

    public static Chromosome getChromosomeFromParetoObject(ParetoObject paretoObject){
        return paretoObject.chromosome;
    }

    public static List<Chromosome> getChromFrontFromParetoFront (List<ParetoObject> paretoFront){
        List<Chromosome> chromFront = new ArrayList<>();

        for (ParetoObject paretoObject: paretoFront){
            chromFront.add(paretoObject.chromosome);
        }

        return chromFront;
    }

     /**
     * returns a sorted list of a given pareto front according to crowding distance
     */
    public static List<Chromosome> crowdingDistanceSortOld(List<Chromosome> front) {

        //create a nichCountList so we dont copute it more than necessary
        Map<Chromosome, Float> nichCounts = front.stream()
                .collect(Collectors.toMap(c -> c, c -> nichCount(c, front)));

        //a omparator to sort the nichingValues. They will naturally be sorted in descending order
        Comparator<Chromosome> nicheCountComparator = (c1, c2) -> (int)Math.signum(nichCounts.get(c1) - nichCounts.get(c2));

        //sort the front by niching. A new list to be sorted in place is used
        List<Chromosome> sortedFront = new ArrayList<>(front);
        sortedFront.sort(nicheCountComparator);

        //reverse the list, as we want high crowding distances first
        Collections.reverse(sortedFront);

        System.out.println(nichCounts.values());

        return sortedFront;
    }

    public static List<Chromosome> crowdingDistanceSort(List<Chromosome> front){

        /*class Pair implements  Comparable<Pair>{
            public Chromosome chromosome;
            float sortValue;

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
        */

        float obj1Max, obj1Min, obj2Max, obj2Min;
        Pair tempPair;
        List<Pair> crowdingDistance = new ArrayList<>();
        List<Pair> chromosomeObjective1 = new ArrayList<>();
        List<Pair> chromosomeObjective2 = new ArrayList<>();
        List<Chromosome> sortedByCD = new ArrayList<>();

        for(Chromosome chromosome: front){
            tempPair = new Pair(chromosome, chromosome.objectiveValues.get(0));
            chromosomeObjective1.add(tempPair);
            tempPair = new Pair(chromosome, chromosome.objectiveValues.get(1));
            chromosomeObjective2.add(tempPair);
            tempPair = new Pair(chromosome, 0);
            crowdingDistance.add(tempPair);
        }

        chromosomeObjective1 = sortByDecreasingObjectiveValues(chromosomeObjective1, 0);
        chromosomeObjective2 = sortByDecreasingObjectiveValues(chromosomeObjective2, 1);

        //Tar ikke hensyn til at flere punkter kan være på samme liste altså at chromosomeObjective1 og chromosomeObjective2 ikke er perfekt reversert sortert av hverandre

        for(Pair cdObject: crowdingDistance){
            if (cdObject.chromosome == chromosomeObjective1.get(0).chromosome && cdObject.chromosome == chromosomeObjective2.get(0).chromosome){
                cdObject.sortValue = Float.MAX_VALUE;
            }
            if (cdObject.chromosome == chromosomeObjective1.get(chromosomeObjective1.size()-1).chromosome || cdObject.chromosome == chromosomeObjective2.get(chromosomeObjective2.size()-1).chromosome){
                cdObject.sortValue = Float.MAX_VALUE;
            }
        }
        obj1Min = chromosomeObjective1.get(0).sortValue;
        obj1Max = chromosomeObjective1.get(chromosomeObjective1.size()-1).sortValue;
        obj2Min = chromosomeObjective1.get(0).sortValue;
        obj2Max = chromosomeObjective1.get(chromosomeObjective2.size()-1).sortValue;

        for(int i = 1; i < chromosomeObjective1.size()-1; i++){
            for(Pair cdObject: crowdingDistance){
                if (cdObject.chromosome == chromosomeObjective1.get(i).chromosome){
                    cdObject.sortValue = cdObject.sortValue + ( (chromosomeObjective1.get(i+1).sortValue - chromosomeObjective1.get(i-1).sortValue) / (obj1Max - obj1Min) );
                }
            }
        }

        for(int i = 1; i < chromosomeObjective2.size()-1; i++){
            for(Pair cdObject: crowdingDistance){
                if (cdObject.chromosome == chromosomeObjective2.get(i).chromosome){
                    cdObject.sortValue = cdObject.sortValue + ( (chromosomeObjective2.get(i+1).sortValue - chromosomeObjective2.get(i-1).sortValue) / (obj2Max - obj2Min) );
                }
            }
        }
        Collections.sort(crowdingDistance);
        Collections.reverse(crowdingDistance);
        for (Pair cdObject: crowdingDistance){
            sortedByCD.add(cdObject.chromosome);
        }


        for(Pair cdObject: crowdingDistance){
            System.out.println("Objective value 1: " + cdObject.chromosome.objectiveValues.get(0));
            System.out.println("Objective value 2: " + cdObject.chromosome.objectiveValues.get(1));
            System.out.println("CrowdingDistance: " + cdObject.sortValue);
            System.out.println();
        }

        return sortedByCD;
    }

    public static List<Pair> sortByDecreasingObjectiveValues(List<Pair> chromosomeObjective, int objectiveNr){
        int otherObjectiveNr;
        Collections.sort(chromosomeObjective);
        Pair otherChromosomeObject, chromosomeObject;
        List<Pair> equalValue = new ArrayList<>();
        List<Pair> otherChromosomeObjective = new ArrayList<>();

        if (objectiveNr == 1){
            otherObjectiveNr = 0;
        }
        else {
            otherObjectiveNr = 1;
        }

        for (int i = 0; i < chromosomeObjective.size(); i++){
            if (equalValue.get(equalValue.size()-1).sortValue != chromosomeObjective.get(i).sortValue){
                if ( equalValue.size() > 1){
                    //sort the equal values
                    for(Pair otherChromosome: equalValue){
                        otherChromosomeObject = new Pair(otherChromosome.chromosome, otherChromosome.chromosome.objectiveValues.get(otherObjectiveNr));
                        otherChromosomeObjective.add(otherChromosomeObject);
                    }
                    Collections.sort(otherChromosomeObjective);
                    for(int j = 0; j < otherChromosomeObjective.size(); j++){
                        chromosomeObject = new Pair(otherChromosomeObjective.get(j).chromosome, otherChromosomeObjective.get(j).chromosome.objectiveValues.get(objectiveNr));
                        chromosomeObjective.set(i - otherChromosomeObjective.size() + 1, chromosomeObject);
                    }
                }
                else{
                    equalValue.remove(0);
                    equalValue.add(chromosomeObjective.get(i));
                }
            }
            if(equalValue.get(equalValue.size()-1).sortValue == chromosomeObjective.get(i).sortValue){
                equalValue.add(chromosomeObjective.get(i));
            }
        }
        return chromosomeObjective;
    }

    private static float nichCount(Chromosome forChrom, List<Chromosome> frontOfChrom) {
        //compute the niching value, wich is the sum of sharing over the given chrom and paretoFront
        float nichingValue = (float)frontOfChrom.stream()
                .mapToDouble(frontChrom -> sharingFunc(forChrom, frontChrom))
                .sum();
        return nichingValue;
    }
    private static float sharingFunc(Chromosome c1, Chromosome c2) {
        float shareMaxDist = 3;
        float distanceImportance = 1; // known as "alpha"

        //max and min objective vals should be set somewhere else
        List<Float> minObjectiveVals = Arrays.asList(0f, 0f);
        List<Float> maxObjectiveVals = Arrays.asList(10f, 10f);

        float squaredObjectiveDistsSum = (float)IntStream.range(0, c1.objectiveValues.size())
                .mapToDouble(i -> {
                    //calculate the distance for each objective squared

                    float c1ObjValue = c1.objectiveValues.get(i);
                    float c2ObjValue = c2.objectiveValues.get(i);

                    float minObjValue = minObjectiveVals.get(i);
                    float maxObjValue = maxObjectiveVals.get(i);

                    float objectiveDist = (c1ObjValue - c2ObjValue) / (maxObjValue - minObjValue);
                    float objectiveDistSquared = objectiveDist * objectiveDist;

                    return objectiveDistSquared;
                }).sum();

        float dist = (float)Math.sqrt(squaredObjectiveDistsSum);

        float sharingVal = (dist < shareMaxDist) ? 1 - (float)Math.pow(dist / shareMaxDist, distanceImportance) : 0;

        return sharingVal;
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


    public int populationSize;

    public NsgaParentSelector(int populationSIze){
        this.populationSize = populationSIze;
    }

    @Override
    public Population selectParents(Population population, Population children) {
        Plot frontPlot = new Plot();
        List<List<Chromosome>> rankedFronts = nondominatedSort(population, children);

        List<Chromosome> childPopulace = new ArrayList<>();

        for(int i = 0; i < rankedFronts.size(); i++){

            List<Chromosome> singularFront = rankedFronts.get(i);
            int usableSpace = populationSize - childPopulace.size();

            if (singularFront != null && !singularFront.isEmpty() && usableSpace > 0){
                if (usableSpace >= singularFront.size()){
                    childPopulace.addAll(singularFront);
                    //Test start
                    frontPlot.addParetoFront(singularFront);
                    //Test end
                }
                else {
                    List<Chromosome> latestFront = crowdingDistanceSort(singularFront);

                    //Test start
                    frontPlot.addParetoFront(latestFront);
                    //Test end

                    for(int k = 0; k < usableSpace; k++){
                        childPopulace.add(latestFront.get(k));
                    }
                }
            }
            else{
                break;
            }
        }

        //fill until pareto split is needed
        //use crowding distances

        //TODO return childPopulace

        Population childPopulation = new Population(childPopulace);

        return childPopulation;//tournamentSelection(null);
    }
}
