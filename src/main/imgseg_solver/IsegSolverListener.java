package imgseg_solver;

import graphics.Plot;
import imgseg_representation.Chromosome;
import imgseg_representation.IsegImageIO;
import imgseg_representation.Population;
import imgseg_representation.Problem;
import simple_GA.WeightedObjectives;
import solver.GeneticSolver;
import solver.SolverListener;

import java.util.List;

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
    public void solverEnd(int iteration, Population parents, Population pop, GeneticSolver solver) {
        if (solver.parentSelector instanceof WeightedObjectives) {
            WeightedObjectives wo = (WeightedObjectives) solver.parentSelector;;

            Population bestPop = wo.selectParents(parents, pop);

            bestPop.chromosones.forEach(c -> {
                IsegImageIO.saveSegmentation(c);
                IsegImageIO.saveSegmentationToEval(c);
            });

            return;
        }

        Population allPop = new Population();
        allPop.chromosones.addAll(parents.chromosones);
        allPop.chromosones.addAll(pop.chromosones);

        List<List<Chromosome>> rankedFronts = NsgaParentSelector.nondominatedSort(allPop);

        List<Chromosome> paretoFront = rankedFronts.get(0);
        paretoFront.forEach(c -> {
            IsegImageIO.saveSegmentation(c);
            IsegImageIO.saveSegmentationToEval(c);
        });


    }
}
