package simple_GA;

import imgseg_representation.Chromosome;
import imgseg_representation.Image;
import imgseg_representation.Pair;
import imgseg_representation.Population;
import imgseg_representation.IsegImageIO;
import solver.ParentSelector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WeightedObjectives implements ParentSelector {

    public static List<Chromosome> simpleParentSelection (Population population, float w1, float w2, int populationSize){
        float fitness;
        Pair chromosomeFittness;
        List<Pair> chromosomeFittnesses = new ArrayList<>();
        List<Chromosome> childPopulation= new ArrayList<>();

        for(Chromosome chromosome: population.chromosones){
            fitness = w1*chromosome.objectiveValues.get(0);
            fitness = fitness + w2*chromosome.objectiveValues.get(1);

            chromosomeFittness = new Pair(chromosome, fitness);
            chromosomeFittnesses.add(chromosomeFittness);
        }
        Collections.sort(chromosomeFittnesses);
        Collections.reverse(chromosomeFittnesses);

        for(Pair child: chromosomeFittnesses){
            childPopulation.add(child.chromosome);
            if (childPopulation.size() == populationSize){
                break;
            }
        }

        IsegImageIO.saveSegmentation(childPopulation.get(0));

        return childPopulation;
    }

    public int populationSize;
    public  float w1;
    public float w2;

    public WeightedObjectives(int populationSize, float w1, float w2){
        this.populationSize = populationSize;
    }

    @Override
    public Population selectParents(Population population, Population children) {

        Population allPop = new Population();
        allPop.chromosones.addAll(population.chromosones);
        allPop.chromosones.addAll(children.chromosones);

        Population childPopulation = new Population();
        List<Chromosome> childChromosomePopulation = new ArrayList<>();
        childChromosomePopulation = simpleParentSelection (allPop, this.w1, this.w2, this.populationSize);
        childPopulation.chromosones = childChromosomePopulation;

        return childPopulation;
    }

    //public static List<Chromosome> chooseParents(Population population){

}
