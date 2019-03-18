package imgseg_solver;

import graphics.Plot;
import imgseg_representation.IsegImageIO;
import imgseg_representation.Population;
import imgseg_representation.Problem;
import solver.SolverListener;

public class IsegSolverListener implements SolverListener {

    private Plot generationPlot;

    public IsegSolverListener() {
//        generationPlot = new Plot();
    }

    @Override
    public void solverStart(Problem p) {

    }

    @Override
    public void populationInit(Population pop) {
        System.out.println("population inited");
        pop.chromosones.forEach(c -> IsegImageIO.saveSegmentation(c));
    }

    @Override
    public void iterationStart(int iteration, Population parents, Population pop) {
        System.out.println("iteration start: " + iteration);
    }

    @Override
    public void selectedParents(Population parents) {
        System.out.println("parents selected");

    }

    @Override
    public void crossedParents(Population parents, Population children) {
        System.out.println("children produced");

    }

    @Override
    public void mutatedChildren(Population prevChildren, Population mutatedChildren) {
        System.out.println("children mutated");

    }

    @Override
    public void iterationEnd(int iteration, Population parets, Population pop) {
    }

    @Override
    public void solverEnd(int iteration, Population parents, Population pop) {

    }
}
