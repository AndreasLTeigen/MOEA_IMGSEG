package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.ParetoObject;
import imgseg_representation.Population;
import solver.ParentSelector;


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
        float obj1Max = Float.MIN_VALUE, obj1Min = Float.MAX_VALUE, obj2Max = Float.MIN_VALUE, obj2Min = Float.MAX_VALUE;
        float crowdingDistanceUpdate;
        List<Float> crowdingDistance = new ArrayList<>();
        Map<Float, Chromosome> chromosomeObjective1 = new TreeMap<>();
        Map<Float, Chromosome> chromosomeObjective2 = new TreeMap<>();

        for(Chromosome chromosome: front){
            chromosomeObjective1.put(chromosome.objectiveValues.get(0), chromosome);
            chromosomeObjective2.put(chromosome.objectiveValues.get(1), chromosome);
        }

        for (int i = 0; i < front.size(); i++){
            crowdingDistance.add((float) 0);
        }
        crowdingDistance.set(0, Float.MAX_VALUE);
        crowdingDistance.set(crowdingDistance.size()-1, Float.MAX_VALUE);

        List<Map.Entry<Float, Chromosome>> sortedChromObjective1 = new ArrayList<>(chromosomeObjective1.entrySet());
        obj1Min = sortedChromObjective1.get(0).getKey();
        obj1Max = sortedChromObjective1.get(sortedChromObjective1.size()-1).getKey();
        for (int i = 0; i < sortedChromObjective1.size(); i++){
            if (i != 0 || i != crowdingDistance.size()-1){
                crowdingDistanceUpdate = crowdingDistance.get(i) + ((sortedChromObjective1.get(i+1).getKey() - sortedChromObjective1.get(i-1).getKey())/(obj1Max-obj1Min));
                crowdingDistance.set(i, crowdingDistanceUpdate);
            }
        }

        List<Map.Entry<Float, Chromosome>> sortedChromObjective2 = new ArrayList<>(chromosomeObjective2.entrySet());
        obj2Min = sortedChromObjective2.get(0).getKey();
        obj2Max = sortedChromObjective2.get(sortedChromObjective2.size()-1).getKey();
        for (int i = 0; i < sortedChromObjective2.size(); i++){
            if (i != 0 || i != crowdingDistance.size()-1){
                crowdingDistanceUpdate = crowdingDistance.get(i) + ((sortedChromObjective2.get(i+1).getKey() - sortedChromObjective2.get(i-1).getKey())/(obj2Max-obj2Min));
                crowdingDistance.set(i, crowdingDistanceUpdate);
            }
        }


        return null;
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
        float distanceImportance = 3; // known as "alpha"

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
    public Population selectParents(Population population, Population childre) {
        List<List<Chromosome>> rankedFronts = nondominatedSort(population, null);

        List<Chromosome> childPopulace = new ArrayList<>();

        for(int i = 0; i < rankedFronts.size(); i++){

            List<Chromosome> singularFront = rankedFronts.get(i);
            int usableSpace = populationSize - childPopulace.size();

            if (singularFront != null && !singularFront.isEmpty() && usableSpace > 0){
                if (usableSpace >= singularFront.size()){
                    childPopulace.addAll(singularFront);
                }
                else {
                    List<Chromosome> latestFront = crowdingDistanceSort(singularFront);

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

        return tournamentSelection(null);
    }
}
