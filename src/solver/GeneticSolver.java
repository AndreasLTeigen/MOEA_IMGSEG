package solver;

import main.Population;
import solver.*;

import java.util.Collections;
import java.util.List;

public class GeneticSolver {

    protected PopulationInitializer populationInitializer;
    protected Evaluator evaluator;
    protected ParentSelector parentSelector;
    protected List<Crossover> crossovers;
    protected List<Mutation> mutations;
    protected TerminateCondition terminateCondition;

    public void solve() {

        Population pop = null;
        Population parents = null;
        Population crossedChildren = null;
        Population mutatedChildren = null;

        pop = populationInitializer.initPopulation();
        List<Float> evaluations = evaluator.evaluate(pop);

        float bestFitness = bestEval(evaluations);

        int iteration = 0;
        while (!terminateCondition.shouldTerminate(iteration, bestFitness, pop)) {

            parents = parentSelector.selectParents(pop);
            for (Crossover cross : crossovers) {
                //crossedChildren = cross.crossover();
            }


            ++iteration;
        }

    }

    private float bestEval(List<Float> evals) {
        return Collections.max(evals);
    }

}
