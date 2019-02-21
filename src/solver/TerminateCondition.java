package solver;

import main.Population;

public interface TerminateCondition {

    boolean shouldTerminate(int iteration, float bestFitness, Population pop);
}
