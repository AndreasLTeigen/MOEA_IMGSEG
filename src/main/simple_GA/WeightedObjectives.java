/*package simple_GA;

import imgseg_representation.Chromosome;
import imgseg_representation.Pair;
import imgseg_representation.Population;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeightedObjectives {

    public static List<Chromosome> simpleParentSelection (Population population, float w1, float w2){
        float fitness;
        Pair chromosomeFittness;
        List<Pair> chromosomeFittnesses = new ArrayList<>();

        for(Chromosome chromosome: population.chromosones){
            fitness = w1*chromosome.objectiveValues.get(0);
            fitness = fitness + w2*chromosome.objectiveValues.get(1);

            chromosomeFittness = new Pair(chromosome, fitness);
            chromosomeFittnesses.add(chromosomeFittness);
        }
        return chromosomeFittnesses;
    }

    //public static List<Chromosome> chooseParents(Population population){

}
*/