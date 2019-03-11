package simple_GA;

import NSGAII.Configuration;
import imgseg_representation.Chromosome;
import imgseg_representation.Population;
import solver.IObjectiveFunction;

import java.util.ArrayList;
import java.util.List;

public class WeightedObjectives {

    public static List<Double> getPopulationFitness (Population population){
        List<Double> fitness = new ArrayList<>();
        double objectiveFitnesses;
        double objectiveWeight;
        List<Chromosome> chromosomes = population.getPopulace();

        Configuration.buildObjectives();
        for (int i = 0; i < chromosomes.size(); i++){
            fitness.add(0.0);
            for (IObjectiveFunction objective: Configuration.getObjectives()){
                objectiveWeight = objective.getObjectWeight();
                objectiveFitnesses = objectiveWeight * (objective.objectiveFunction(chromosomes.get(i)));
                fitness.set(i, fitness.get(i)+objectiveFitnesses);
            }
        }
        return fitness;
    }

    public static List<Chromosome> chooseParents(Population population){

    }
}