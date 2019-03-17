package  imgseg_solver;

import imgseg_representation.Problem;
import solver.*;
import solver_utils.IterationTermination;

import java.util.stream.Collectors;

public class IsegSolver extends GeneticSolver {

    public Problem problem;
    public int populationSize = 10;
    public int iterations = 5;

    public IsegSolver(Problem p) {
        super();
        this.problem = p;
    }

    public void init() {
        populationInitializer = new HeuristicPopulationInitializer(problem, populationSize);

        parentSelector = new NsgaParentSelector(populationSize);
        crossPop = new IsegCrossover();
        mutatePop = p -> p; //new IsegMutation(); //no mutations, returns the given population

        terminateCondition = new IterationTermination(iterations);

        listener = new IsegSolverListener();

        //creates a dummy-evaluation of 1 for each individual
//        evaluator = p -> p.stream().map(c -> new IsegEvaluation(1, c)).collect(Collectors.toList());
//        generationSelector = (c, pop) -> c; //selects all children as the next gen
    }


}
