package solver;

import main.Population;
import main.Problem;

public interface PopulationInitializer {
    Population initPopulation(Problem p, int populationSize);
}
