package  solver;


import main.Population;

public interface GenerationSelector {

    Population selectNextGeneration(Population children, Population prevGeneration);
}
