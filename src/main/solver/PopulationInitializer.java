package  solver;


import imgseg_representation.Population;
import imgseg_representation.Problem;

public interface PopulationInitializer {
    Population initPopulation(Problem p, int populationSize);
}
