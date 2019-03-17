package  solver_utils;

import imgseg_representation.Population;
import solver.TerminateCondition;

public class IterationTermination implements TerminateCondition {

    private int iterations;

    public IterationTermination(int iterations) {
        this.iterations = iterations;
    }

    public boolean shouldTerminate(int iteration, Population pop) {
        return iteration >= this.iterations;
    }

}
