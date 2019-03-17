package  solver;


import imgseg_representation.Population;

public interface TerminateCondition {

    boolean shouldTerminate(int iteration, Population pop);
}
