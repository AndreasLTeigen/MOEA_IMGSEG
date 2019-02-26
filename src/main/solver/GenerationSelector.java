package  solver;


import imgseg_representation.Population;

public interface GenerationSelector {

    Population selectNextGeneration(Population children, Population prevGeneration);
}
