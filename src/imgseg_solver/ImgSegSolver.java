package imgseg_solver;

import solver.*;
import solver_utils.IterationTermination;

import java.util.stream.Collectors;

public class ImgSegSolver extends GeneticSolver {

    public ImgSegSolver() {
        super();

        populationInitializer = new RandomPopulationInitializer();

        //creates a dummy-evaluation of 1 for each individual
        evaluator = p -> p.stream().map(c -> new ImgsEvaluation(1, c)).collect(Collectors.toList());

        parentSelector = p->p; //selects everything
        crossPop = p->p; //no crossover, returns the given population
        mutatePop = p->p; //no mutations, returns the given population

        generationSelector = (c, pop) -> c; //selects all children as the next gen

        terminateCondition = new IterationTermination(500);
    }


}
