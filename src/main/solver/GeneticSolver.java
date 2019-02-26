package main.solver;

import main.Population;
import main.Problem;

import java.util.List;
import java.util.NoSuchElementException;

public class GeneticSolver {

    public int popSize = 10;


    protected PopulationInitializer populationInitializer = null;
    protected Evaluator evaluator= null;
    protected ParentSelector parentSelector= null;
    protected CrossoverPopulation crossPop= null;
    protected MutatePopulation mutatePop= null;
    protected GenerationSelector generationSelector= null;
    protected TerminateCondition terminateCondition= null;


    public void solve(Problem p) {

        Population pop = null;
        Population parents = null;
        Population children = null;

        pop = populationInitializer.initPopulation(p, popSize);
        List<Evaluation> evaluations = evaluator.evaluate(pop);

        float bestFitness = bestEval(evaluations);

        int iteration = 0;
        while (!terminateCondition.shouldTerminate(iteration, bestFitness, pop)) {

            parents = parentSelector.selectParents(pop);

            //performs any number of crossovers and mutations on any number of parents
            children = crossPop.crossoverPopulation(parents);
            children = mutatePop.mutatePopulation(children);

            //elitism may be baked into this
            pop = generationSelector.selectNextGeneration(children, pop);

            System.out.println("Iteration" + iteration);
            ++iteration;
        }

    }

    private float bestEval(List<Evaluation> evals) {
        return evals.stream()
                .map(Evaluation::getEval)
                .max(Float::compare)
                .orElseThrow(NoSuchElementException::new);
    }

}
