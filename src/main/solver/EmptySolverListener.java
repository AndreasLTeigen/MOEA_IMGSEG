package solver;

import imgseg_representation.Population;
import imgseg_representation.Problem;

public class EmptySolverListener implements SolverListener {
    @Override
    public void solverStart(Problem p) {

    }

    @Override
    public void populationInit(Population pop) {

    }

    @Override
    public void iterationStart(int iteration, Population parents, Population pop) {

    }

    @Override
    public void selectedParents(Population parents) {

    }

    @Override
    public void crossedParents(Population parents, Population children) {

    }

    @Override
    public void mutatedChildren(Population prevChildren, Population mutatedChildren) {

    }

    @Override
    public void iterationEnd(int iteration, Population parets, Population pop) {

    }

    @Override
    public void solverEnd(int iteration, Population parents, Population pop) {

    }

}
