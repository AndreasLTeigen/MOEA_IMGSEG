package imgseg_solver;

import imgseg_representation.Chromosome;
import imgseg_representation.Population;
import solver.ParentSelector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NsgaParentSelector implements ParentSelector {
    class ParetoFront {
        public List<Chromosome> chroms;

        public ParetoFront(List<Chromosome> chroms) {
            this.chroms = chroms;
        }
    };

    public static List<ParetoFront> nondominatedSort(Population parents, Population children) {
        return null;
    }

    /**
     * returns a sorted list of a given pareto front according to crowding distance
     */
    public static List<Chromosome> crowdingDistanceSort(List<Chromosome> front) {

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

    @Override
    public Population selectParents(Population population) {
        List<ParetoFront> paretoFronts = nondominatedSort(population, null);

        //fill until pareto split is needed
        //use crowding distances

        return tournamentSelection(null);
    }
}
