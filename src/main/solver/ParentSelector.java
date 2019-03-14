package  solver;


import imgseg_representation.Population;

public interface ParentSelector {
    Population selectParents(Population population, Population children);
}
