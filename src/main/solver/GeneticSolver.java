package  solver;

import imgseg_representation.Population;
import imgseg_representation.Problem;

import java.util.List;
import java.util.NoSuchElementException;

public class GeneticSolver {

    protected PopulationInitializer populationInitializer = null;
//    protected Evaluator evaluator= null;
    protected ParentSelector parentSelector= null;
    protected CrossoverPopulation crossPop= null;
    protected MutatePopulation mutatePop= null;
//    protected GenerationSelector generationSelector= null;
    protected TerminateCondition terminateCondition= null;

    protected SolverListener listener = new EmptySolverListener();


    public void solve() {

        Population pop = new Population();
        Population parents = new Population();
        Population children = new Population();
        Population mutatedChildren = new Population();

        pop = populationInitializer.initPopulation();
        listener.populationInit(pop);

//        float bestFitness = bestEval(evaluations);

        int iteration = 0;
        while (!terminateCondition.shouldTerminate(iteration, pop)) {
            listener.iterationStart(iteration, parents, pop);

            parents = parentSelector.selectParents(parents, pop); //TODO: Check children validity
            listener.selectedParents(parents);

            //performs any number of crossovers and mutations on any number of parents
            children = crossPop.crossoverPopulation(parents);
            listener.crossedParents(parents, children);

            mutatedChildren = mutatePop.mutatePopulation(children);
            listener.mutatedChildren(children, mutatedChildren);

            pop = mutatedChildren;

            //elitism may be baked into this
//            pop = generationSelector.selectNextGeneration(children, pop);

            listener.iterationEnd(iteration, parents, children);

            ++iteration;
        }

        listener.solverEnd(iteration, parents, pop, this);

    }

    private float bestEval(List<Evaluation> evals) {
        return evals.stream()
                .map(Evaluation::getEval)
                .max(Float::compare)
                .orElseThrow(NoSuchElementException::new);
    }

}
