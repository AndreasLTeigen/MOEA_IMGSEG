package solver;

import imgseg_representation.Population;
import imgseg_representation.Problem;

public interface SolverListener {

    void solverStart(Problem p);
    void populationInit(Population pop);
    void iterationStart(int iteration, Population parents, Population pop);
    void selectedParents(Population parents);
    void crossedParents(Population parents, Population children);
    void mutatedChildren(Population prevChildren, Population mutatedChildren);
    void iterationEnd(int iteration, Population parents, Population pop);
    void solverEnd(int iteration, Population parents, Population pop);

}
